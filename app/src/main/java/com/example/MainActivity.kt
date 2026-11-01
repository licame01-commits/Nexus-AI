package com.example

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.NexusViewModel
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.NexusTheme
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

    private val nexusViewModel: NexusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force virtual screen to wake up, turn on display and bypass lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        // STEP 1: Mandatory Firebase App Check configuration with launch intent before any UI
        configureAppCheck(this, intent)

        // STEP 2: Edge-to-edge support
        enableEdgeToEdge()

        // STEP 3: Render Nexus UI
        setContent {
            NexusTheme {
                MainAppScreen(viewModel = nexusViewModel)
            }
        }
    }
}
