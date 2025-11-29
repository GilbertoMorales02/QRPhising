package com.example.qr.ui

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

// NOTE: You need to put your valid VirusTotal API Key here
const val VIRUS_TOTAL_API_KEY = "YOUR_API_KEY_HERE"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(
    onQrScanned: (String) -> Unit,
    onClose: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        CameraScreen(
            onQrScanned = onQrScanned,
            onClose = onClose
        )
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

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(
    onQrScanned: (String) -> Unit,
    onClose: () -> Unit
) {
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

        // Vista de la cámara
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
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            val scanner = BarcodeScanning.getClient()

                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        barcode.rawValue?.let { url ->
                                            // Evitar múltiples lecturas mientras se analiza
                                            if (!isChecking && !showDialog) {
                                                isChecking = true
                                                scannedUrl = url
                                                scope.launch {
                                                    // Verificar con VirusTotal
                                                    val isSafe =
                                                        !repository.checkUrl(url, VIRUS_TOTAL_API_KEY)
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

        // Botón para cerrar/volver
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar"
            )
        }

        // Loader mientras consulta VirusTotal
        if (isChecking) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // Diálogo de resultado (usa VirusTotal como antes, pero ahora pasa el QR hacia afuera)
        if (showDialog && scannedUrl != null) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text(
                        if (isMalicious == true) "¡Alerta de seguridad!"
                        else "Enlace analizado"
                    )
                },
                text = {
                    Column {
                        Text("URL escaneada:")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(scannedUrl!!)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isMalicious == true) {
                            Text("VirusTotal marcó esta URL como maliciosa o sospechosa.")
                        } else {
                            Text("VirusTotal no reporta esta URL como maliciosa.")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("¿Quieres ver un análisis detallado del QR?")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            // En lugar de abrir el navegador, mandamos el contenido a la siguiente pantalla
                            onQrScanned(scannedUrl!!)
                        }
                    ) {
                        Text("Ver análisis")
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
