package com.baccaro.ai.presentation.call.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.baccaro.ai.domain.models.ui.CallState

@Composable
fun SpeakingIndicator(callState: CallState) {
    val speakingText = when {
        callState.isUserSpeaking -> "You are speaking..."
        callState.isAISpeaking -> "AI is speaking..."
        else -> ""
    }

    if (speakingText.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = speakingText)
        }
    }
}
