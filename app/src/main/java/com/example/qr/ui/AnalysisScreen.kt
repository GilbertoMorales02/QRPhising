package com.example.qr.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.qr.domain.QrAnalyzer
import com.example.qr.domain.RiskLabel
import com.example.qr.domain.openInBrowser  // IMPORTANT: extensión

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    qrContent: String,
    onBack: () -> Unit,
    onRescan: () -> Unit
) {
    val analysis = QrAnalyzer.analyze(qrContent)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis del QR") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Resultado del análisis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // “Semáforo” de riesgo
            val riskText = when (analysis.label) {
                RiskLabel.SAFE -> "Riesgo BAJO (posiblemente confiable)"
                RiskLabel.SUSPICIOUS -> "Riesgo MEDIO (enlace sospechoso)"
                RiskLabel.MALICIOUS -> "Riesgo ALTO (potencialmente malicioso)"
            }

            val riskColor = when (analysis.label) {
                RiskLabel.SAFE -> MaterialTheme.colorScheme.primary
                RiskLabel.SUSPICIOUS -> MaterialTheme.colorScheme.tertiary
                RiskLabel.MALICIOUS -> MaterialTheme.colorScheme.error
            }

            Surface(
                color = riskColor.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = riskText,
                    modifier = Modifier.padding(16.dp),
                    color = riskColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "Contenido del QR:",
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = qrContent,
                style = MaterialTheme.typography.bodyMedium
            )

            Divider()

            Text(
                text = "Detalle del enlace",
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = analysis.prettyDetails,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Motivos del nivel de riesgo:",
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = analysis.reasons.joinToString(
                    separator = "\n• ",
                    prefix = "• "
                ),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRescan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Escanear otro código")
            }

            if (analysis.isUrl && analysis.parsedUri != null) {
                OutlinedButton(
                    onClick = { analysis.openInBrowser(context) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Abrir enlace bajo mi responsabilidad")
                }
            }

            Text(
                text = "Recuerda: aunque el análisis ayude, siempre valida el origen del QR y el contexto.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
