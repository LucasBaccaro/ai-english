package com.baccaro.ai.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted

@Composable
actual fun rememberAudioManager(onPermissionResult: (Boolean) -> Unit): AudioManager {
    return remember { AudioManager(onPermissionResult) }
}

actual class AudioManager(private val onPermissionResult: (Boolean) -> Unit) {

    actual fun hasRecordAudioPermission(): Boolean {
        return AVAudioSession.sharedInstance().recordPermission() == AVAudioSessionRecordPermissionGranted
    }

    actual fun requestRecordAudioPermission() {
        AVAudioSession.sharedInstance().requestRecordPermission { granted ->
            onPermissionResult(granted)
        }
    }
}
