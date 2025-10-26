package com.baccaro.ai.data.openai

import com.baccaro.ai.domain.models.openai.*

/**
 * Handler for processing OpenAI Realtime API server events
 * Provides callbacks for different event types
 */
class EventHandler {

    // Session events
    var onSessionCreated: ((SessionCreatedEvent) -> Unit)? = null
    var onSessionUpdated: ((SessionUpdatedEvent) -> Unit)? = null

    // Audio buffer events (Voice Activity Detection)
    var onSpeechStarted: ((InputAudioBufferSpeechStartedEvent) -> Unit)? = null
    var onSpeechStopped: ((InputAudioBufferSpeechStoppedEvent) -> Unit)? = null
    var onAudioBufferCommitted: ((InputAudioBufferCommittedEvent) -> Unit)? = null
    var onAudioBufferCleared: ((InputAudioBufferClearedEvent) -> Unit)? = null

    // Conversation events
    var onConversationItemCreated: ((ConversationItemCreatedEvent) -> Unit)? = null
    var onConversationItemDone: ((ConversationItemDoneEvent) -> Unit)? = null

    // Input audio transcription
    var onInputTranscriptionCompleted: ((ConversationItemInputAudioTranscriptionCompletedEvent) -> Unit)? = null
    var onInputTranscriptionDelta: ((ConversationItemInputAudioTranscriptionDeltaEvent) -> Unit)? = null

    // Response events
    var onResponseCreated: ((ResponseCreatedEvent) -> Unit)? = null
    var onResponseDone: ((ResponseDoneEvent) -> Unit)? = null

    // Output item events
    var onOutputItemAdded: ((ResponseOutputItemAddedEvent) -> Unit)? = null
    var onOutputItemDone: ((ResponseOutputItemDoneEvent) -> Unit)? = null

    // Content part events
    var onContentPartAdded: ((ResponseContentPartAddedEvent) -> Unit)? = null
    var onContentPartDone: ((ResponseContentPartDoneEvent) -> Unit)? = null

    // Text streaming (AI text response)
    var onTextDelta: ((ResponseOutputTextDeltaEvent) -> Unit)? = null
    var onTextDone: ((ResponseOutputTextDoneEvent) -> Unit)? = null

    // Audio streaming (AI voice)
    var onAudioDelta: ((ResponseOutputAudioDeltaEvent) -> Unit)? = null
    var onAudioDone: ((ResponseOutputAudioDoneEvent) -> Unit)? = null

    // Audio transcript streaming (transcription of AI voice)
    var onAudioTranscriptDelta: ((ResponseAudioTranscriptDeltaEvent) -> Unit)? = null
    var onAudioTranscriptDone: ((ResponseAudioTranscriptDoneEvent) -> Unit)? = null

    // Function calling events
    var onFunctionCallArgumentsDelta: ((ResponseFunctionCallArgumentsDeltaEvent) -> Unit)? = null
    var onFunctionCallArgumentsDone: ((ResponseFunctionCallArgumentsDoneEvent) -> Unit)? = null

    // Error events
    var onError: ((ErrorEvent) -> Unit)? = null

    // Rate limits
    var onRateLimitsUpdated: ((RateLimitsUpdatedEvent) -> Unit)? = null

    /**
     * Process a server event and invoke the appropriate callback
     */
    fun handleEvent(event: ServerEvent) {
        try {
            when (event) {
                // Session
                is SessionCreatedEvent -> {
                    println("📋 Session created: ${event.session.id}")
                    onSessionCreated?.invoke(event)
                }
                is SessionUpdatedEvent -> {
                    println("📋 Session updated")
                    onSessionUpdated?.invoke(event)
                }

                // Audio buffer / VAD
                is InputAudioBufferSpeechStartedEvent -> {
                    println("🎤 User started speaking (VAD detected)")
                    onSpeechStarted?.invoke(event)
                }
                is InputAudioBufferSpeechStoppedEvent -> {
                    println("🎤 User stopped speaking (VAD detected)")
                    onSpeechStopped?.invoke(event)
                }
                is InputAudioBufferCommittedEvent -> {
                    println("✅ Audio buffer committed: ${event.itemId}")
                    onAudioBufferCommitted?.invoke(event)
                }
                is InputAudioBufferClearedEvent -> {
                    println("🗑️ Audio buffer cleared")
                    onAudioBufferCleared?.invoke(event)
                }

                // Conversation items
                is ConversationItemCreatedEvent -> {
                    println("💬 Conversation item created: ${event.item.id}")
                    onConversationItemCreated?.invoke(event)
                }
                is ConversationItemDoneEvent -> {
                    println("✅ Conversation item done: ${event.item.id}")
                    onConversationItemDone?.invoke(event)
                }

                // Input transcription
                is ConversationItemInputAudioTranscriptionCompletedEvent -> {
                    println("📝 Input transcription completed: ${event.transcript}")
                    onInputTranscriptionCompleted?.invoke(event)
                }
                is ConversationItemInputAudioTranscriptionDeltaEvent -> {
                    println("📝 Input transcription delta: ${event.delta}")
                    onInputTranscriptionDelta?.invoke(event)
                }

                // Response
                is ResponseCreatedEvent -> {
                    println("🤖 AI response created: ${event.response.id}")
                    onResponseCreated?.invoke(event)
                }
                is ResponseDoneEvent -> {
                    println("✅ AI response done: ${event.response.id}")
                    onResponseDone?.invoke(event)
                }

                // Output items
                is ResponseOutputItemAddedEvent -> {
                    println("➕ Output item added: ${event.item.id}")
                    onOutputItemAdded?.invoke(event)
                }
                is ResponseOutputItemDoneEvent -> {
                    println("✅ Output item done: ${event.item.id}")
                    onOutputItemDone?.invoke(event)
                }

                // Content parts
                is ResponseContentPartAddedEvent -> {
                    println("➕ Content part added")
                    onContentPartAdded?.invoke(event)
                }
                is ResponseContentPartDoneEvent -> {
                    println("✅ Content part done")
                    onContentPartDone?.invoke(event)
                }

                // Text streaming
                is ResponseOutputTextDeltaEvent -> {
                    print(event.delta) // Print text as it arrives
                    onTextDelta?.invoke(event)
                }
                is ResponseOutputTextDoneEvent -> {
                    println("\n✅ Text output done")
                    onTextDone?.invoke(event)
                }

                // Audio streaming
                is ResponseOutputAudioDeltaEvent -> {
                    // Audio data chunk received
                    onAudioDelta?.invoke(event)
                }
                is ResponseOutputAudioDoneEvent -> {
                    println("🔊 Audio output done")
                    onAudioDone?.invoke(event)
                }

                // Audio transcript streaming
                is ResponseAudioTranscriptDeltaEvent -> {
                    print(event.delta) // Print transcript as it arrives
                    onAudioTranscriptDelta?.invoke(event)
                }
                is ResponseAudioTranscriptDoneEvent -> {
                    println("\n📝 Audio transcript done")
                    onAudioTranscriptDone?.invoke(event)
                }

                // Function calling
                is ResponseFunctionCallArgumentsDeltaEvent -> {
                    println("🔧 Function call arguments delta")
                    onFunctionCallArgumentsDelta?.invoke(event)
                }
                is ResponseFunctionCallArgumentsDoneEvent -> {
                    println("✅ Function call arguments done")
                    onFunctionCallArgumentsDone?.invoke(event)
                }

                // Error
                is ErrorEvent -> {
                    println("❌ Error: ${event.error.message}")
                    onError?.invoke(event)
                }

                // Rate limits
                is RateLimitsUpdatedEvent -> {
                    println("📊 Rate limits updated")
                    onRateLimitsUpdated?.invoke(event)
                }

                // Unknown events (shouldn't happen with proper EventParser)
                else -> {
                    println("⚠️ Unknown event type: ${event.type}")
                }
            }
        } catch (e: Exception) {
            println("❌ Error handling event: ${e.message}")
            e.printStackTrace()
        }
    }
}
