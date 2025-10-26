package com.baccaro.ai.platform

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberAudioManager(onPermissionResult: (Boolean) -> Unit): AudioManager {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onPermissionResult
    )
    return remember { AudioManager(context, launcher) }
}

actual class AudioManager(private val context: Context, private val launcher: androidx.activity.result.ActivityResultLauncher<String>) {

    actual fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    actual fun requestRecordAudioPermission() {
        launcher.launch(Manifest.permission.RECORD_AUDIO)
    }
}
