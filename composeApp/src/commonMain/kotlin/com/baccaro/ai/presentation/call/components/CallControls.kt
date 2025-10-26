package com.baccaro.ai.presentation.call.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.baccaro.ai.domain.models.ui.CallEvent
import com.baccaro.ai.domain.models.ui.CallState

@Composable
fun CallControls(
    callState: CallState,
    onEvent: (CallEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Start/End Call Button
        val callButtonText = if (callState.isConnected || callState.isConnecting) "End Call" else "Start Call"
        val callButtonColor = if (callState.isConnected || callState.isConnecting) Color.Red else Color.Green
        Button(
            onClick = { onEvent(if (callState.isConnected) CallEvent.EndCall else CallEvent.StartCall) },
            colors = ButtonDefaults.buttonColors(containerColor = callButtonColor)
        ) {
            Text(callButtonText)
        }

        // Mute/Unmute Button
        if (callState.isConnected) {
            val muteButtonText = if (callState.isMuted) "Unmute" else "Mute"
            Button(onClick = { onEvent(CallEvent.ToggleMute) }) {
                Text(muteButtonText)
            }
        }
    }
}
