package com.baccaro.ai

import androidx.compose.ui.window.ComposeUIViewController
import com.baccaro.ai.di.DefaultAppContainer

fun MainViewController() = ComposeUIViewController { 
    val appContainer = DefaultAppContainer()
    App(appContainer)
}
