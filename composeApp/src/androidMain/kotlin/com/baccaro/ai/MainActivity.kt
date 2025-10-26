package com.baccaro.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.baccaro.ai.di.DefaultAppContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = DefaultAppContainer()
        setContent {
            App(appContainer)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val appContainer = DefaultAppContainer()
    App(appContainer)
}
