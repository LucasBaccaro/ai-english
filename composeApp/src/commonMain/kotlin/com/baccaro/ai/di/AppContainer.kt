package com.baccaro.ai.di

import com.baccaro.ai.data.openai.OpenAIRealtimeClient
import com.baccaro.ai.presentation.call.CallViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface AppContainer {
    val callViewModel: CallViewModel
}

class DefaultAppContainer : AppContainer {

    private val openAIApiKey = ""

    private val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor Logger: $message")
                    }
                }
                level = LogLevel.ALL
            }
        }
    }

    private val openAIRealtimeClient: OpenAIRealtimeClient by lazy {
        OpenAIRealtimeClient(
            apiKey = openAIApiKey,
            httpClient = httpClient
        )
    }

    override val callViewModel: CallViewModel by lazy {
        CallViewModel(openAIRealtimeClient)
    }
}
