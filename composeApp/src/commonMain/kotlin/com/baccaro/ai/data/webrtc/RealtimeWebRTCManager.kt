package com.baccaro.ai.data.webrtc

import com.baccaro.ai.domain.models.openai.ClientEvent
import com.baccaro.ai.domain.models.openai.ServerEvent
import com.shepeliev.webrtckmp.DataChannel
import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.IceServer
import com.shepeliev.webrtckmp.MediaDevices
import com.shepeliev.webrtckmp.MediaStreamTrack
import com.shepeliev.webrtckmp.MediaStreamTrackKind
import com.shepeliev.webrtckmp.OfferAnswerOptions
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.RtcConfiguration
import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.SessionDescriptionType
import com.shepeliev.webrtckmp.audioTracks
import com.shepeliev.webrtckmp.onConnectionStateChange
import com.shepeliev.webrtckmp.onIceCandidate
import com.shepeliev.webrtckmp.onIceConnectionStateChange
import com.shepeliev.webrtckmp.onSignalingStateChange
import com.shepeliev.webrtckmp.onTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * Manager for WebRTC connection with OpenAI Realtime API
 * Handles PeerConnection, audio streams, and data channel for events
 */
class RealtimeWebRTCManager {

    // Coroutine scope for flow collection
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // WebRTC components
    private var peerConnection: PeerConnection? = null
    private var dataChannel: DataChannel? = null
    private var localAudioTrack: MediaStreamTrack? = null
    private var remoteAudioTrack: MediaStreamTrack? = null

    // State flows
    private val _connectionState = MutableStateFlow<PeerConnectionState?>(null)
    val connectionState: StateFlow<PeerConnectionState?> = _connectionState.asStateFlow()

    private val _iceConnectionState = MutableStateFlow<IceConnectionState?>(null)
    val iceConnectionState: StateFlow<IceConnectionState?> = _iceConnectionState.asStateFlow()

    // Callbacks
    private var onServerEvent: ((ServerEvent) -> Unit)? = null
    private var onRemoteAudioTrack: ((MediaStreamTrack) -> Unit)? = null

    /**
     * Initialize WebRTC factory and components
     */
    suspend fun initialize() {
        // Configure ICE servers (STUN servers for NAT traversal)
        val iceServers = listOf(
            IceServer(
                urls = listOf("stun:stun.l.google.com:19302"),
                username = "",
                password = ""
            )
        )

        val rtcConfiguration = RtcConfiguration(
            iceServers = iceServers
        )

        // Create PeerConnection
        peerConnection = PeerConnection(rtcConfiguration = rtcConfiguration)

        // Set up observers using Flows
        setupPeerConnectionObservers()

        // Set up local audio track (microphone)
        setupLocalAudio()

        // Create data channel for JSON events
        setupDataChannel()
    }

    /**
     * Set up PeerConnection observers using Flows
     */
    private fun setupPeerConnectionObservers() {
        val pc = peerConnection ?: return

        // Monitor ICE candidates
        pc.onIceCandidate
            .onEach { candidate ->
                println("New ICE candidate: $candidate")
                // For OpenAI Realtime API, ICE candidates are handled internally
            }
            .launchIn(scope)

        // Monitor connection state changes
        pc.onConnectionStateChange
            .onEach { state ->
                println("Connection state changed to: $state")
                _connectionState.value = state
            }
            .launchIn(scope)

        // Monitor ICE connection state changes
        pc.onIceConnectionStateChange
            .onEach { state ->
                println("ICE connection state changed to: $state")
                _iceConnectionState.value = state
            }
            .launchIn(scope)

        // Monitor signaling state changes
        pc.onSignalingStateChange
            .onEach { state ->
                println("Signaling state changed to: $state")
            }
            .launchIn(scope)

        // Monitor remote tracks (AI audio)
        pc.onTrack
            .map { it.track }
            .filterNotNull()
            .onEach { track ->
                println("Track received: ${track.kind}")
                if (track.kind == MediaStreamTrackKind.Audio) {
                    remoteAudioTrack = track
                    onRemoteAudioTrack?.invoke(track)
                }
            }
            .launchIn(scope)
    }

    /**
     * Set up local audio track from microphone
     */
    private suspend fun setupLocalAudio() {
        val pc = peerConnection ?: return

        // Get user media (audio only) using simple boolean approach
        val mediaStream = MediaDevices.getUserMedia(audio = true, video = false)

        // Add all audio tracks to peer connection
        mediaStream.audioTracks.forEach { track ->
            localAudioTrack = track
            pc.addTrack(track)
            println("Added local audio track to peer connection")
        }
    }

    /**
     * Set up DataChannel for sending/receiving JSON events
     */
    private fun setupDataChannel() {
        val pc = peerConnection ?: return

        // Create data channel with parameters directly (no DataChannelInit class)
        dataChannel = pc.createDataChannel(
            label = "oai-events",
            id = -1,                    // Auto-assign ID
            ordered = true,             // Guarantee message order
            maxPacketLifeTimeMs = -1,   // No limit
            maxRetransmits = -1,        // No limit
            protocol = "",              // Default protocol
            negotiated = false          // In-band negotiation
        )

        // Monitor DataChannel state
        dataChannel?.onOpen
            ?.onEach {
                println("DataChannel opened")
            }
            ?.launchIn(scope)

        dataChannel?.onClose
            ?.onEach {
                println("DataChannel closed")
            }
            ?.launchIn(scope)

        // Monitor incoming messages (Flow<ByteArray>)
        dataChannel?.onMessage
            ?.map { it.decodeToString() }  // Convert ByteArray to String
            ?.onEach { messageText ->
                try {
                    println("Received message from DataChannel: $messageText")

                    // Parse and handle server event
                    val event = EventParser.parseServerEvent(messageText)
                    event?.let { onServerEvent?.invoke(it) }
                } catch (e: Exception) {
                    println("Error parsing message: ${e.message}")
                }
            }
            ?.launchIn(scope)
    }

    /**
     * Create an SDP offer to start the WebRTC connection
     */
    suspend fun createOffer(): SessionDescription? {
        return try {
            val offer = peerConnection?.createOffer(
                OfferAnswerOptions(
                    offerToReceiveAudio = true,
                    offerToReceiveVideo = false
                )
            )
            offer?.let {
                peerConnection?.setLocalDescription(it)
                println("Created and set local offer")
            }
            offer
        } catch (e: Exception) {
            println("Error creating offer: ${e.message}")
            null
        }
    }

    /**
     * Set the remote SDP answer from OpenAI
     */
    suspend fun setRemoteDescription(sdp: String) {
        try {
            val answer = SessionDescription(SessionDescriptionType.Answer, sdp)
            peerConnection?.setRemoteDescription(answer)
            println("Remote description set successfully")
        } catch (e: Exception) {
            println("Error setting remote description: ${e.message}")
        }
    }

    /**
     * Send a client event to OpenAI via DataChannel
     */
    suspend fun sendClientEvent(event: ClientEvent) {
        try {
            val jsonString = EventParser.serializeClientEvent(event)
            val success = dataChannel?.send(jsonString.encodeToByteArray()) ?: false
            if (success) {
                println("Sent client event: ${event.type}")
            } else {
                println("Failed to send client event: ${event.type}")
            }
        } catch (e: Exception) {
            println("Error sending client event: ${e.message}")
        }
    }

    /**
     * Set callback for server events
     */
    fun setServerEventCallback(callback: (ServerEvent) -> Unit) {
        onServerEvent = callback
    }

    /**
     * Set callback for remote audio track
     */
    fun setRemoteAudioTrackCallback(callback: (MediaStreamTrack) -> Unit) {
        onRemoteAudioTrack = callback
    }

    /**
     * Mute/unmute local audio
     */
    fun setMuted(muted: Boolean) {
        localAudioTrack?.enabled = !muted
    }

    /**
     * Close the WebRTC connection and clean up resources
     */
    fun close() {
        scope.cancel()
        dataChannel?.close()
        peerConnection?.close()

        localAudioTrack = null
        remoteAudioTrack = null
        dataChannel = null
        peerConnection = null
    }
}
