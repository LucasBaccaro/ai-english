package com.baccaro.ai.domain.models.openai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base sealed interface for all server events received from OpenAI Realtime API
 */
@Serializable
sealed interface ServerEvent {
    val type: String
    val eventId: String?
        get() = null
}

// Session Events

@Serializable
@SerialName("session.created")
data class SessionCreatedEvent(
    override val type: String = "session.created",
    @SerialName("event_id") override val eventId: String,
    val session: SessionInfo
) : ServerEvent

@Serializable
@SerialName("session.updated")
data class SessionUpdatedEvent(
    override val type: String = "session.updated",
    @SerialName("event_id") override val eventId: String,
    val session: SessionInfo
) : ServerEvent

// Input Audio Buffer Events

@Serializable
@SerialName("input_audio_buffer.speech_started")
data class InputAudioBufferSpeechStartedEvent(
    override val type: String = "input_audio_buffer.speech_started",
    @SerialName("event_id") override val eventId: String,
    @SerialName("audio_start_ms") val audioStartMs: Int,
    @SerialName("item_id") val itemId: String
) : ServerEvent

@Serializable
@SerialName("input_audio_buffer.speech_stopped")
data class InputAudioBufferSpeechStoppedEvent(
    override val type: String = "input_audio_buffer.speech_stopped",
    @SerialName("event_id") override val eventId: String,
    @SerialName("audio_end_ms") val audioEndMs: Int,
    @SerialName("item_id") val itemId: String
) : ServerEvent

@Serializable
@SerialName("input_audio_buffer.committed")
data class InputAudioBufferCommittedEvent(
    override val type: String = "input_audio_buffer.committed",
    @SerialName("event_id") override val eventId: String,
    @SerialName("previous_item_id") val previousItemId: String?,
    @SerialName("item_id") val itemId: String
) : ServerEvent

@Serializable
@SerialName("input_audio_buffer.cleared")
data class InputAudioBufferClearedEvent(
    override val type: String = "input_audio_buffer.cleared",
    @SerialName("event_id") override val eventId: String
) : ServerEvent

// Conversation Events

@Serializable
@SerialName("conversation.item.created")
data class ConversationItemCreatedEvent(
    override val type: String = "conversation.item.created",
    @SerialName("event_id") override val eventId: String,
    @SerialName("previous_item_id") val previousItemId: String?,
    val item: ConversationItem
) : ServerEvent

@Serializable
@SerialName("conversation.item.added")
data class ConversationItemAddedEvent(
    override val type: String = "conversation.item.added",
    @SerialName("event_id") override val eventId: String,
    @SerialName("previous_item_id") val previousItemId: String?,
    val item: ConversationItem
) : ServerEvent

@Serializable
@SerialName("conversation.item.done")
data class ConversationItemDoneEvent(
    override val type: String = "conversation.item.done",
    @SerialName("event_id") override val eventId: String,
    @SerialName("previous_item_id") val previousItemId: String?,
    val item: ConversationItem
) : ServerEvent

@Serializable
@SerialName("conversation.item.input_audio_transcription.completed")
data class ConversationItemInputAudioTranscriptionCompletedEvent(
    override val type: String = "conversation.item.input_audio_transcription.completed",
    @SerialName("event_id") override val eventId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("content_index") val contentIndex: Int,
    val transcript: String
) : ServerEvent

@Serializable
@SerialName("conversation.item.input_audio_transcription.delta")
data class ConversationItemInputAudioTranscriptionDeltaEvent(
    override val type: String = "conversation.item.input_audio_transcription.delta",
    @SerialName("event_id") override val eventId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("content_index") val contentIndex: Int,
    val delta: String
) : ServerEvent

// Response Events

@Serializable
@SerialName("response.created")
data class ResponseCreatedEvent(
    override val type: String = "response.created",
    @SerialName("event_id") override val eventId: String,
    val response: ResponseInfo
) : ServerEvent

@Serializable
@SerialName("response.done")
data class ResponseDoneEvent(
    override val type: String = "response.done",
    @SerialName("event_id") override val eventId: String,
    val response: ResponseInfo
) : ServerEvent

// Response Output Item Events

@Serializable
@SerialName("response.output_item.added")
data class ResponseOutputItemAddedEvent(
    override val type: String = "response.output_item.added",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("output_index") val outputIndex: Int,
    val item: ConversationItem
) : ServerEvent

@Serializable
@SerialName("response.output_item.done")
data class ResponseOutputItemDoneEvent(
    override val type: String = "response.output_item.done",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("output_index") val outputIndex: Int,
    val item: ConversationItem
) : ServerEvent

// Response Content Part Events

@Serializable
@SerialName("response.content_part.added")
data class ResponseContentPartAddedEvent(
    override val type: String = "response.content_part.added",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("content_index") val contentIndex: Int,
    val part: ContentPart
) : ServerEvent

@Serializable
@SerialName("response.content_part.done")
data class ResponseContentPartDoneEvent(
    override val type: String = "response.content_part.done",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("content_index") val contentIndex: Int,
    val part: ContentPart
) : ServerEvent

// Response Text Events

@Serializable
@SerialName("response.output_text.delta")
data class ResponseOutputTextDeltaEvent(
    override val type: String = "response.output_text.delta",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("content_index") val contentIndex: Int,
    val delta: String
) : ServerEvent

@Serializable
@SerialName("response.output_text.done")
data class ResponseOutputTextDoneEvent(
    override val type: String = "response.output_text.done",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("content_index") val contentIndex: Int,
    val text: String
) : ServerEvent

// Response Audio Events

@Serializable
@SerialName("response.output_audio.delta")
data class ResponseOutputAudioDeltaEvent(
    override val type: String = "response.output_audio.delta",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("content_index") val contentIndex: Int,
    val delta: String // Base64-encoded audio chunk
) : ServerEvent

@Serializable
@SerialName("response.output_audio.done")
data class ResponseOutputAudioDoneEvent(
    override val type: String = "response.output_audio.done",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("content_index") val contentIndex: Int
) : ServerEvent

// Response Audio Transcript Events

@Serializable
@SerialName("response.audio_transcript.delta")
data class ResponseAudioTranscriptDeltaEvent(
    override val type: String = "response.audio_transcript.delta",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("content_index") val contentIndex: Int,
    val delta: String
) : ServerEvent

@Serializable
@SerialName("response.audio_transcript.done")
data class ResponseAudioTranscriptDoneEvent(
    override val type: String = "response.audio_transcript.done",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("content_index") val contentIndex: Int,
    val transcript: String
) : ServerEvent

// Function Call Events

@Serializable
@SerialName("response.function_call_arguments.delta")
data class ResponseFunctionCallArgumentsDeltaEvent(
    override val type: String = "response.function_call_arguments.delta",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("call_id") val callId: String,
    val delta: String
) : ServerEvent

@Serializable
@SerialName("response.function_call_arguments.done")
data class ResponseFunctionCallArgumentsDoneEvent(
    override val type: String = "response.function_call_arguments.done",
    @SerialName("event_id") override val eventId: String,
    @SerialName("response_id") val responseId: String,
    @SerialName("item_id") val itemId: String,
    @SerialName("output_index") val outputIndex: Int,
    @SerialName("call_id") val callId: String,
    val arguments: String // JSON string
) : ServerEvent

// Error Event

@Serializable
@SerialName("error")
data class ErrorEvent(
    override val type: String = "error",
    @SerialName("event_id") override val eventId: String?,
    val error: ErrorInfo
) : ServerEvent

// Rate Limits Event

@Serializable
@SerialName("rate_limits.updated")
data class RateLimitsUpdatedEvent(
    override val type: String = "rate_limits.updated",
    @SerialName("event_id") override val eventId: String,
    @SerialName("rate_limits") val rateLimits: List<RateLimit>
) : ServerEvent

// Supporting Data Models

@Serializable
data class SessionInfo(
    val id: String,
    val `object`: String = "realtime.session",
    val type: String = "realtime",
    val model: String,
    @SerialName("output_modalities") val outputModalities: List<String>? = null,
    val audio: AudioConfig? = null,
    val instructions: String? = null,
    val voice: String? = null,
    val tools: List<ToolDefinition>? = null,
    @SerialName("tool_choice") val toolChoice: String? = null,
    @SerialName("turn_detection") val turnDetection: TurnDetection? = null
)

@Serializable
data class ResponseInfo(
    val id: String,
    val `object`: String = "realtime.response",
    val status: String, // "in_progress", "completed", "cancelled", "failed"
    @SerialName("status_details") val statusDetails: String? = null,
    val output: List<ConversationItem> = emptyList(),
    val usage: UsageInfo? = null,
    val metadata: Map<String, String>? = null
)

@Serializable
data class UsageInfo(
    @SerialName("total_tokens") val totalTokens: Int,
    @SerialName("input_tokens") val inputTokens: Int,
    @SerialName("output_tokens") val outputTokens: Int,
    @SerialName("input_token_details") val inputTokenDetails: InputTokenDetails? = null,
    @SerialName("output_token_details") val outputTokenDetails: OutputTokenDetails? = null
)

@Serializable
data class InputTokenDetails(
    @SerialName("text_tokens") val textTokens: Int,
    @SerialName("audio_tokens") val audioTokens: Int,
    @SerialName("cached_tokens") val cachedTokens: Int,
    @SerialName("cached_tokens_details") val cachedTokensDetails: CachedTokensDetails? = null
)

@Serializable
data class OutputTokenDetails(
    @SerialName("text_tokens") val textTokens: Int,
    @SerialName("audio_tokens") val audioTokens: Int
)

@Serializable
data class CachedTokensDetails(
    @SerialName("text_tokens") val textTokens: Int,
    @SerialName("audio_tokens") val audioTokens: Int
)

@Serializable
data class ErrorInfo(
    val type: String, // e.g., "invalid_request_error", "server_error"
    val code: String? = null,
    val message: String,
    val param: String? = null,
    @SerialName("event_id") val eventId: String? = null
)

@Serializable
data class RateLimit(
    val name: String,
    val limit: Int,
    val remaining: Int,
    @SerialName("reset_seconds") val resetSeconds: Double
)
