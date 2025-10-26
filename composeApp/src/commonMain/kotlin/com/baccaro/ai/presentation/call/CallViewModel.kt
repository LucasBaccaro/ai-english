package com.baccaro.ai.presentation.call

import com.baccaro.ai.data.openai.OpenAIRealtimeClient
import com.baccaro.ai.domain.models.openai.*
import com.baccaro.ai.domain.models.ui.CallEvent
import com.baccaro.ai.domain.models.ui.ConversationState
import com.baccaro.ai.domain.models.ui.Message
import com.shepeliev.webrtckmp.PeerConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CallViewModel(
    private val openAIRealtimeClient: OpenAIRealtimeClient
) {

    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _uiState = MutableStateFlow(ConversationState())
    val uiState = _uiState.asStateFlow()

    init {
        observeConnectionState()
    }

    fun onEvent(event: CallEvent) {
        when (event) {
            is CallEvent.StartCall -> startCall()
            is CallEvent.EndCall -> endCall()
            is CallEvent.ToggleMute -> toggleMute()
            is CallEvent.SendTextMessage -> {
                viewModelScope.launch {
                    openAIRealtimeClient.sendUserMessage(event.text)
                }
            }
            is CallEvent.Error -> {
                _uiState.update { it.copy(callState = it.callState.copy(connectionError = event.message)) }
            }
        }
    }

    private fun startCall() {
        viewModelScope.launch {
            _uiState.update { it.copy(callState = it.callState.copy(isConnecting = true)) }
            val result = openAIRealtimeClient.connect(onServerEvent = ::handleServerEvent)
            result.onFailure {
                _uiState.update {
                    it.copy(
                        callState = it.callState.copy(
                            isConnecting = false,
                            connectionError = "Failed to connect: ${it.messages}"
                        )
                    )
                }
            }
        }
    }

    private fun endCall() {
        openAIRealtimeClient.disconnect()
        _uiState.value = ConversationState() // Reset state
    }

    private fun toggleMute() {
        val isMuted = _uiState.value.callState.isMuted
        openAIRealtimeClient.setMuted(!isMuted)
        _uiState.update { it.copy(callState = it.callState.copy(isMuted = !isMuted)) }
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            openAIRealtimeClient.connectionState.collect { state ->
                _uiState.update {
                    val callState = when (state) {
                        PeerConnectionState.New, PeerConnectionState.Connecting -> it.callState.copy(isConnecting = true, isConnected = false)
                        PeerConnectionState.Connected -> it.callState.copy(isConnecting = false, isConnected = true, connectionError = null)
                        PeerConnectionState.Disconnected, PeerConnectionState.Closed -> it.callState.copy(isConnected = false, isConnecting = false)
                        PeerConnectionState.Failed -> it.callState.copy(isConnected = false, isConnecting = false, connectionError = "Connection failed")
                        else -> it.callState
                    }
                    it.copy(callState = callState)
                }
            }
        }
    }

    private fun handleServerEvent(event: ServerEvent) {
        viewModelScope.launch {
            when (event) {
                is SessionCreatedEvent -> {
                    _uiState.update { it.copy(callState = it.callState.copy(sessionId = event.session.id)) }
                }
                is InputAudioBufferSpeechStartedEvent -> {
                    _uiState.update { it.copy(callState = it.callState.copy(isUserSpeaking = true)) }
                }
                is InputAudioBufferSpeechStoppedEvent -> {
                    _uiState.update { it.copy(callState = it.callState.copy(isUserSpeaking = false)) }
                }
                is ConversationItemInputAudioTranscriptionDeltaEvent -> {
                    _uiState.update { it.copy(currentTranscript = it.currentTranscript + event.delta) }
                }
                is ConversationItemInputAudioTranscriptionCompletedEvent -> {
                    val newMessage = Message(
                        id = event.itemId,
                        text = event.transcript,
                        isUser = true,
                        timestamp = Clock.System.now()
                    )
                    _uiState.update {
                        it.copy(
                            messages = it.messages + newMessage,
                            currentTranscript = ""
                        )
                    }
                }
                is ResponseOutputItemAddedEvent -> {
                    // AI started talking, add a placeholder message
                    if (event.item.role == "assistant") {
                        val newMessage = Message(
                            id = event.item.id ?: "",
                            text = "",
                            isUser = false,
                            timestamp = Clock.System.now()
                        )
                        _uiState.update { it.copy(messages = it.messages + newMessage) }
                    }
                }
                is ResponseOutputTextDeltaEvent -> {
                    // Update AI message with streaming text
                    _uiState.update {
                        val updatedMessages = it.messages.map {
                            if (it.id == event.itemId) {
                                it.copy(text = it.text + event.delta)
                            } else {
                                it
                            }
                        }
                        it.copy(messages = updatedMessages)
                    }
                }
                is ResponseAudioTranscriptDeltaEvent -> {
                    // Update AI message with streaming audio transcript
                    _uiState.update {
                        val updatedMessages = it.messages.map { message ->
                            if (message.id == event.itemId && !message.isUser) {
                                message.copy(text = message.text + event.delta)
                            } else {
                                message
                            }
                        }
                        it.copy(messages = updatedMessages)
                    }
                }
                is ResponseAudioTranscriptDoneEvent -> {
                    // Audio transcript completed - ensure full text is set
                    _uiState.update {
                        val updatedMessages = it.messages.map { message ->
                            if (message.id == event.itemId && !message.isUser) {
                                message.copy(text = event.transcript)
                            } else {
                                message
                            }
                        }
                        it.copy(messages = updatedMessages)
                    }
                }
                is ResponseCreatedEvent -> {
                    _uiState.update { it.copy(callState = it.callState.copy(isAISpeaking = true)) }
                }
                is ResponseDoneEvent -> {
                    _uiState.update { it.copy(callState = it.callState.copy(isAISpeaking = false)) }
                }
                is ErrorEvent -> {
                    _uiState.update { it.copy(callState = it.callState.copy(connectionError = event.error.message)) }
                }

                else -> {}
            }
        }
    }

    fun onCleared() {
        endCall()
    }
}
