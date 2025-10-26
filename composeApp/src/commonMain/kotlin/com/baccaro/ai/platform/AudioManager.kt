package com.baccaro.ai.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberAudioManager(onPermissionResult: (Boolean) -> Unit): AudioManager

expect class AudioManager {
    fun hasRecordAudioPermission(): Boolean
    fun requestRecordAudioPermission()
}
