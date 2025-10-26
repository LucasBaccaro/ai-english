package com.baccaro.ai.data.openai

import com.baccaro.ai.data.webrtc.RealtimeWebRTCManager
import com.baccaro.ai.domain.models.openai.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock

/**
 * Client for OpenAI Realtime API
 * Manages WebRTC connection and session configuration
 */
class OpenAIRealtimeClient(
    private val apiKey: String,
    private val httpClient: HttpClient
) {
    private val webRTCManager = RealtimeWebRTCManager()

    // API endpoint
    private val realtimeEndpoint = "https://api.openai.com/v1/realtime"

    // Expose WebRTC connection states
    val connectionState: StateFlow<com.shepeliev.webrtckmp.PeerConnectionState?>
        get() = webRTCManager.connectionState

    val iceConnectionState: StateFlow<com.shepeliev.webrtckmp.IceConnectionState?>
        get() = webRTCManager.iceConnectionState

    // Session configuration
    private var sessionId: String? = null

    /**
     * Connect to OpenAI Realtime API
     * 1. Initialize WebRTC
     * 2. Create SDP offer
     * 3. Send offer to OpenAI and get answer
     * 4. Set remote description
     * 5. Configure session with VAD
     */
    suspend fun connect(
        model: String = "gpt-realtime-mini-2025-10-06",
        voice: String = "alloy",
        instructions: String = "Eres un asistente conversacional amigable. Responde de forma natural como en una llamada telefónica.",
        onServerEvent: (ServerEvent) -> Unit
    ): Result<Unit> {
        return try {
            println("🔄 Initializing WebRTC...")

            // 1. Initialize WebRTC manager
            webRTCManager.initialize()

            // Set up event callback
            webRTCManager.setServerEventCallback(onServerEvent)

            // 2. Create SDP offer
            println("🔄 Creating SDP offer...")
            val offer = webRTCManager.createOffer()
                ?: return Result.failure(Exception("Failed to create SDP offer"))

            println("✅ SDP offer created")

            // 3. Send offer to OpenAI and get answer
            println("🔄 Sending offer to OpenAI API...")
            val answer = sendOfferToOpenAI(offer.sdp, model)

            println("✅ Received SDP answer from OpenAI")

            // 4. Set remote description (answer from OpenAI)
            println("🔄 Setting remote description...")
            webRTCManager.setRemoteDescription(answer)

            println("✅ Remote description set")

            // 5. Configure session with VAD and settings
            println("🔄 Configuring session...")
            configureSession(voice, instructions)

            println("✅ OpenAI Realtime connection established successfully!")

            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ Error connecting to OpenAI Realtime API: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Send SDP offer to OpenAI API and receive SDP answer
     */
    private suspend fun sendOfferToOpenAI(sdpOffer: String, model: String): String {
        val response = httpClient.post(realtimeEndpoint) {
            url {
                parameters.append("model", model)
            }
            headers {
                append(HttpHeaders.Authorization, "Bearer $apiKey")
                append(HttpHeaders.ContentType, "application/sdp")
            }
            setBody(sdpOffer)
        }

        if (!response.status.isSuccess()) {
            throw Exception("OpenAI API request failed: ${response.status} - ${response.bodyAsText()}")
        }

        return response.bodyAsText()
    }

    /**
     * Configure session with VAD, voice, and instructions
     */
    private suspend fun configureSession(voice: String, instructions: String) {
        val sessionConfig = SessionConfig(
            type = "realtime",
            model = "gpt-4o-realtime-preview-2024-10-01",
            outputModalities = listOf("audio", "text"),
            audio = AudioConfig(
                input = AudioInputConfig(
                    format = AudioFormat(type = "pcm16", rate = 24000),
                    turnDetection = TurnDetection(
                        type = "server_vad",
                        threshold = 0.5,
                        prefixPaddingMs = 300,
                        silenceDurationMs = 500
                    ),
                    transcription = TranscriptionConfig(
                        model = "whisper-1"
                    )
                ),
                output = AudioOutputConfig(
                    format = AudioFormat(type = "pcm16", rate = 24000),
                    voice = voice
                )
            ),
            instructions = instructions,
            voice = voice
        )

        val sessionUpdateEvent = SessionUpdateEvent(
            session = sessionConfig
        )

        webRTCManager.sendClientEvent(sessionUpdateEvent)
        println("📤 Session configuration sent")
    }

    /**
     * Send a client event to OpenAI
     */
    suspend fun sendEvent(event: ClientEvent) {
        webRTCManager.sendClientEvent(event)
    }

    /**
     * Create a new conversation item (user message)
     */
    suspend fun sendUserMessage(text: String) {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val conversationItem = ConversationItem(
            id = "msg_$timestamp",
            type = "message",
            role = "user",
            content = listOf(
                ContentPart(
                    type = "input_text",
                    text = text
                )
            )
        )

        val createItemEvent = ConversationItemCreateEvent(
            item = conversationItem
        )

        sendEvent(createItemEvent)

        // Request AI response
        val responseEvent = ResponseCreateEvent()
        sendEvent(responseEvent)
    }

    /**
     * Mute/unmute microphone
     */
    fun setMuted(muted: Boolean) {
        webRTCManager.setMuted(muted)
    }

    /**
     * Set callback for remote audio track (AI voice)
     */
    fun setRemoteAudioTrackCallback(callback: (com.shepeliev.webrtckmp.MediaStreamTrack) -> Unit) {
        webRTCManager.setRemoteAudioTrackCallback(callback)
    }

    /**
     * Disconnect and clean up resources
     */
    fun disconnect() {
        println("🔌 Disconnecting from OpenAI Realtime API...")
        webRTCManager.close()
        sessionId = null
        println("✅ Disconnected")
    }
}
