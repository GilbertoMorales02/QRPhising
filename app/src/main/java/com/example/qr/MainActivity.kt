package com.example.qr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.qr.ui.QRScannerScreen
import com.example.qr.ui.theme.QRTheme
import com.example.qr.ui.HomeScreen
import com.example.qr.ui.AnalysisScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot()
                }
            }
        }
    }
}

private enum class Screen {
    HOME,
    SCANNER,
    ANALYSIS
}

@Composable
private fun AppRoot() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var lastQrContent by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            onScanClick = { currentScreen = Screen.SCANNER }
        )

        Screen.SCANNER -> QRScannerScreen(
            onQrScanned = { qrText ->
                lastQrContent = qrText
                currentScreen = Screen.ANALYSIS
            },
            onClose = {
                currentScreen = Screen.HOME
            }
        )

        Screen.ANALYSIS -> AnalysisScreen(
            qrContent = lastQrContent.orEmpty(),
            onBack = { currentScreen = Screen.HOME },
            onRescan = { currentScreen = Screen.SCANNER }
        )
    }
}
