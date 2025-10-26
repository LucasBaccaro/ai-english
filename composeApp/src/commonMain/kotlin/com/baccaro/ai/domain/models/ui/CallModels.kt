package com.baccaro.ai.domain.models.ui

import kotlinx.datetime.Instant

/**
 * Represents a message in the conversation
 */
data class Message(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Instant,
    val audioUrl: String? = null,
    val isTranscribing: Boolean = false
)

/**
 * Represents the current state of the voice call
 */
data class CallState(
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isAISpeaking: Boolean = false,
    val isUserSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val connectionError: String? = null,
    val sessionId: String? = null
)

/**
 * Sealed interface for call-related UI events
 */
sealed interface CallEvent {
    data object StartCall : CallEvent
    data object EndCall : CallEvent
    data object ToggleMute : CallEvent
    data class SendTextMessage(val text: String) : CallEvent
    data class Error(val message: String) : CallEvent
}

/**
 * State for the entire conversation screen
 */
data class ConversationState(
    val messages: List<Message> = emptyList(),
    val callState: CallState = CallState(),
    val currentTranscript: String = ""
)
