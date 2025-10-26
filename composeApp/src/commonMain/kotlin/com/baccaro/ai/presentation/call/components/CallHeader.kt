package com.baccaro.ai.presentation.call.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.baccaro.ai.domain.models.ui.CallState

@Composable
fun CallHeader(callState: CallState) {
    val (backgroundColor, statusText) = when {
        callState.connectionError != null -> Color.Red to "Error: ${callState.connectionError}"
        callState.isConnecting -> Color.Yellow to "Connecting..."
        callState.isConnected -> Color.Green to "Connected"
        else -> Color.Gray to "Disconnected"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
    }
}
