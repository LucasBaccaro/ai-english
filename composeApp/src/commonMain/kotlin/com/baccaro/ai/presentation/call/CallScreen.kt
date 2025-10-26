package com.baccaro.ai.presentation.call

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.baccaro.ai.presentation.call.components.CallControls
import com.baccaro.ai.presentation.call.components.CallHeader
import com.baccaro.ai.presentation.call.components.MessageList
import com.baccaro.ai.presentation.call.components.SpeakingIndicator

@Composable
fun CallScreen(viewModel: CallViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        CallHeader(callState = uiState.callState)
        MessageList(
            messages = uiState.messages,
            modifier = Modifier.weight(1f)
        )
        SpeakingIndicator(callState = uiState.callState)
        CallControls(
            callState = uiState.callState,
            onEvent = viewModel::onEvent
        )
    }
}
