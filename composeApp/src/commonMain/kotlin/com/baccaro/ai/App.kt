package com.baccaro.ai

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.baccaro.ai.di.AppContainer
import com.baccaro.ai.presentation.call.CallScreen

@Composable
fun App(appContainer: AppContainer) {
    MaterialTheme {
        CallScreen(viewModel = appContainer.callViewModel)
    }
}
