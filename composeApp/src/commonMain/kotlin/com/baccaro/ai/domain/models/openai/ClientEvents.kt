package com.baccaro.ai.domain.models.openai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base sealed interface for all client events sent to OpenAI Realtime API
 */
@Serializable
sealed interface ClientEvent {
    val type: String
    val eventId: String?
        get() = null
}

/**
 * Session Update Event - Configure the realtime session
 */
@Serializable
@SerialName("session.update")
data class SessionUpdateEvent(
    override val type: String = "session.update",
    @SerialName("event_id") override val eventId: String? = null,
    val session: SessionConfig
) : ClientEvent

/**
 * Conversation Item Create Event - Add a message to the conversation
 */
@Serializable
@SerialName("conversation.item.create")
data class ConversationItemCreateEvent(
    override val type: String = "conversation.item.create",
    @SerialName("event_id") override val eventId: String? = null,
    val item: ConversationItem
) : ClientEvent

/**
 * Response Create Event - Trigger a model response
 */
@Serializable
@SerialName("response.create")
data class ResponseCreateEvent(
    override val type: String = "response.create",
    @SerialName("event_id") override val eventId: String? = null,
    val response: ResponseConfig? = null
) : ClientEvent

/**
 * Input Audio Buffer Append Event - Stream audio input
 */
@Serializable
@SerialName("input_audio_buffer.append")
data class InputAudioBufferAppendEvent(
    override val type: String = "input_audio_buffer.append",
    @SerialName("event_id") override val eventId: String? = null,
    val audio: String // Base64-encoded audio
) : ClientEvent

/**
 * Input Audio Buffer Commit Event - Commit the audio buffer
 */
@Serializable
@SerialName("input_audio_buffer.commit")
data class InputAudioBufferCommitEvent(
    override val type: String = "input_audio_buffer.commit",
    @SerialName("event_id") override val eventId: String? = null
) : ClientEvent

/**
 * Input Audio Buffer Clear Event - Clear the audio buffer
 */
@Serializable
@SerialName("input_audio_buffer.clear")
data class InputAudioBufferClearEvent(
    override val type: String = "input_audio_buffer.clear",
    @SerialName("event_id") override val eventId: String? = null
) : ClientEvent

// Configuration Models

@Serializable
data class SessionConfig(
    val type: String = "realtime",
    val model: String = "gpt-realtime",
    @SerialName("output_modalities") val outputModalities: List<String> = listOf("audio", "text"),
    val audio: AudioConfig? = null,
    val instructions: String? = null,
    val voice: String? = null,
    val tools: List<ToolDefinition>? = null,
    @SerialName("tool_choice") val toolChoice: String = "auto"
)

@Serializable
data class AudioConfig(
    val input: AudioInputConfig? = null,
    val output: AudioOutputConfig? = null
)

@Serializable
data class AudioInputConfig(
    val format: AudioFormat,
    @SerialName("turn_detection") val turnDetection: TurnDetection? = null,
    val transcription: TranscriptionConfig? = null,
    @SerialName("noise_reduction") val noiseReduction: NoiseReduction? = null
)

@Serializable
data class AudioOutputConfig(
    val format: AudioFormat,
    val voice: String = "alloy"
)

@Serializable
data class AudioFormat(
    val type: String, // "audio/pcm", "audio/pcmu", "audio/pcma"
    val rate: Int = 24000
)

@Serializable
data class TurnDetection(
    val type: String, // "semantic_vad" or "server_vad"
    val threshold: Double? = null,
    @SerialName("prefix_padding_ms") val prefixPaddingMs: Int? = null,
    @SerialName("silence_duration_ms") val silenceDurationMs: Int? = null,
    @SerialName("interrupt_response") val interruptResponse: Boolean? = null,
    @SerialName("create_response") val createResponse: Boolean? = null
)

@Serializable
data class NoiseReduction(
    val type: String // "near_field", "far_field", or null
)

@Serializable
data class TranscriptionConfig(
    val model: String = "gpt-4o-transcribe",
    val language: String? = null,
    val prompt: String? = null
)

@Serializable
data class ConversationItem(
    val type: String, // "message", "function_call", "function_call_output"
    val id: String? = null,
    val role: String? = null, // "user", "assistant", "system"
    val content: List<ContentPart>? = null,
    val name: String? = null, // for function_call
    @SerialName("call_id") val callId: String? = null,
    val arguments: String? = null, // JSON string for function arguments
    val output: String? = null // for function_call_output
)

@Serializable
data class ContentPart(
    val type: String, // "input_text", "input_audio", "output_text", "output_audio"
    val text: String? = null,
    val audio: String? = null, // Base64-encoded
    val transcript: String? = null
)

@Serializable
data class ResponseConfig(
    val conversation: String? = null, // "default" or "none"
    val metadata: Map<String, String>? = null,
    @SerialName("output_modalities") val outputModalities: List<String>? = null,
    val instructions: String? = null,
    val input: List<ConversationItem>? = null,
    val tools: List<ToolDefinition>? = null,
    @SerialName("tool_choice") val toolChoice: String? = null,
    val audio: AudioConfig? = null
)

@Serializable
data class ToolDefinition(
    val type: String = "function",
    val name: String,
    val description: String,
    val parameters: Map<String, kotlinx.serialization.json.JsonElement>
)
