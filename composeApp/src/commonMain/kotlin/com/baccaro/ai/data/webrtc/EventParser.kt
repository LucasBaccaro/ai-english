package com.baccaro.ai.data.webrtc

import kotlinx.serialization.json.*
import com.baccaro.ai.domain.models.openai.*

/**
 * Parser for OpenAI Realtime API server events
 * Handles polymorphic deserialization based on the "type" field
 */
object EventParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Parse a JSON string into a ServerEvent
     * Uses the "type" field to determine which event type to deserialize
     */
    fun parseServerEvent(jsonString: String): ServerEvent? {
        return try {
            val jsonElement = json.parseToJsonElement(jsonString)
            val jsonObject = jsonElement.jsonObject

            val eventType = jsonObject["type"]?.jsonPrimitive?.content
                ?: return null

            when (eventType) {
                // Session events
                "session.created" -> json.decodeFromJsonElement<SessionCreatedEvent>(jsonObject)
                "session.updated" -> json.decodeFromJsonElement<SessionUpdatedEvent>(jsonObject)

                // Input audio buffer events
                "input_audio_buffer.speech_started" ->
                    json.decodeFromJsonElement<InputAudioBufferSpeechStartedEvent>(jsonObject)
                "input_audio_buffer.speech_stopped" ->
                    json.decodeFromJsonElement<InputAudioBufferSpeechStoppedEvent>(jsonObject)
                "input_audio_buffer.committed" ->
                    json.decodeFromJsonElement<InputAudioBufferCommittedEvent>(jsonObject)
                "input_audio_buffer.cleared" ->
                    json.decodeFromJsonElement<InputAudioBufferClearedEvent>(jsonObject)

                // Conversation events
                "conversation.item.created" ->
                    json.decodeFromJsonElement<ConversationItemCreatedEvent>(jsonObject)
                "conversation.item.added" ->
                    json.decodeFromJsonElement<ConversationItemAddedEvent>(jsonObject)
                "conversation.item.done" ->
                    json.decodeFromJsonElement<ConversationItemDoneEvent>(jsonObject)
                "conversation.item.input_audio_transcription.completed" ->
                    json.decodeFromJsonElement<ConversationItemInputAudioTranscriptionCompletedEvent>(jsonObject)
                "conversation.item.input_audio_transcription.delta" ->
                    json.decodeFromJsonElement<ConversationItemInputAudioTranscriptionDeltaEvent>(jsonObject)

                // Response events
                "response.created" -> json.decodeFromJsonElement<ResponseCreatedEvent>(jsonObject)
                "response.done" -> json.decodeFromJsonElement<ResponseDoneEvent>(jsonObject)

                // Response output item events
                "response.output_item.added" ->
                    json.decodeFromJsonElement<ResponseOutputItemAddedEvent>(jsonObject)
                "response.output_item.done" ->
                    json.decodeFromJsonElement<ResponseOutputItemDoneEvent>(jsonObject)

                // Response content part events
                "response.content_part.added" ->
                    json.decodeFromJsonElement<ResponseContentPartAddedEvent>(jsonObject)
                "response.content_part.done" ->
                    json.decodeFromJsonElement<ResponseContentPartDoneEvent>(jsonObject)

                // Response text events
                "response.output_text.delta" ->
                    json.decodeFromJsonElement<ResponseOutputTextDeltaEvent>(jsonObject)
                "response.output_text.done" ->
                    json.decodeFromJsonElement<ResponseOutputTextDoneEvent>(jsonObject)

                // Response audio events
                "response.output_audio.delta" ->
                    json.decodeFromJsonElement<ResponseOutputAudioDeltaEvent>(jsonObject)
                "response.output_audio.done" ->
                    json.decodeFromJsonElement<ResponseOutputAudioDoneEvent>(jsonObject)

                // Response audio transcript events
                "response.output_audio_transcript.delta" ->
                    json.decodeFromJsonElement<ResponseOutputAudioTranscriptDeltaEvent>(jsonObject)
                "response.output_audio_transcript.done" ->
                    json.decodeFromJsonElement<ResponseOutputAudioTranscriptDoneEvent>(jsonObject)

                // Function call events
                "response.function_call_arguments.delta" ->
                    json.decodeFromJsonElement<ResponseFunctionCallArgumentsDeltaEvent>(jsonObject)
                "response.function_call_arguments.done" ->
                    json.decodeFromJsonElement<ResponseFunctionCallArgumentsDoneEvent>(jsonObject)

                // Error event
                "error" -> json.decodeFromJsonElement<ErrorEvent>(jsonObject)

                // Rate limits event
                "rate_limits.updated" ->
                    json.decodeFromJsonElement<RateLimitsUpdatedEvent>(jsonObject)

                else -> {
                    println("Unknown event type: $eventType")
                    null
                }
            }
        } catch (e: Exception) {
            println("Error parsing server event: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Serialize a ClientEvent to JSON string
     */
    fun serializeClientEvent(event: ClientEvent): String {
        return when (event) {
            is SessionUpdateEvent -> json.encodeToString(SessionUpdateEvent.serializer(), event)
            is ConversationItemCreateEvent -> json.encodeToString(ConversationItemCreateEvent.serializer(), event)
            is ResponseCreateEvent -> json.encodeToString(ResponseCreateEvent.serializer(), event)
            is InputAudioBufferAppendEvent -> json.encodeToString(InputAudioBufferAppendEvent.serializer(), event)
            is InputAudioBufferCommitEvent -> json.encodeToString(InputAudioBufferCommitEvent.serializer(), event)
            is InputAudioBufferClearEvent -> json.encodeToString(InputAudioBufferClearEvent.serializer(), event)
        }
    }
}
