package com.example.qr.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.qr.domain.VirusTotalRepository
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

// NOTE: You need to put your valid VirusTotal API Key here
const val VIRUS_TOTAL_API_KEY = "YOUR_API_KEY_HERE"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen() {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    if (cameraPermissionState.status.isGranted) {
        CameraScreen()
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Camera permission is required to scan QR codes.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Request Permission")
            }
        }
    }
}

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    var scannedUrl by remember { mutableStateOf<String?>(null) }
    var isMalicious by remember { mutableStateOf<Boolean?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }

    val repository = remember { VirusTotalRepository() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                
                val executor = ContextCompat.getMainExecutor(ctx)
                
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            val scanner = BarcodeScanning.getClient()
                            
                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        barcode.rawValue?.let { url ->
                                            // Avoid repeated checks for the same URL in a short loop or if currently checking
                                            if (!isChecking && !showDialog) {
                                                isChecking = true
                                                scannedUrl = url
                                                scope.launch {
                                                    // Verify with VirusTotal
                                                    val isSafe = !repository.checkUrl(url, VIRUS_TOTAL_API_KEY)
                                                    isMalicious = !isSafe
                                                    isChecking = false
                                                    showDialog = true
                                                }
                                            }
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("QRScanner", "Use case binding failed", e)
                    }
                }, executor)
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isChecking) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (showDialog && scannedUrl != null) {
            AlertDialog(
                onDismissRequest = { 
                    showDialog = false 
                    // Optional: delay rescanning
                },
                title = { Text(if (isMalicious == true) "¡Alerta de seguridad!" else "Enlace Seguro") },
                text = { 
                    Column {
                        Text("URL: $scannedUrl")
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isMalicious == true) {
                            Text("Esta URL ha sido marcada como maliciosa o sospechosa. ¿Estás seguro de que quieres acceder?")
                        } else {
                            Text("Esta URL parece segura. ¿Quieres ir al sitio web?")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scannedUrl))
                            context.startActivity(intent)
                            showDialog = false
                        },
                        colors = if (isMalicious == true) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) else ButtonDefaults.buttonColors()
                    ) {
                        Text(if (isMalicious == true) "Sí, acceder (riesgoso)" else "Ir al sitio")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
