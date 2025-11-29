package com.example.qr.domain

import android.content.Context
import android.content.Intent
import android.net.Uri

enum class RiskLabel {
    SAFE,
    SUSPICIOUS,
    MALICIOUS
}

data class QrAnalysisResult(
    val content: String,
    val isUrl: Boolean,
    val label: RiskLabel,
    val score: Int,
    val reasons: List<String>,
    val parsedUri: Uri?,
    val prettyDetails: String
)

object QrAnalyzer {

    fun analyze(content: String): QrAnalysisResult {
        val reasons = mutableListOf<String>()
        var score = 0

        val uri = runCatching { Uri.parse(content) }.getOrNull()
        val isUrl = uri != null && !uri?.scheme.isNullOrBlank()

        if (!isUrl) {
            reasons += "El contenido no parece ser una URL. Es menos probable un phishing clásico, pero revisa manualmente."
            return QrAnalysisResult(
                content = content,
                isUrl = false,
                label = RiskLabel.SAFE,
                score = 0,
                reasons = reasons,
                parsedUri = null,
                prettyDetails = "No se detectó una URL, solo texto plano."
            )
        }

        val scheme = uri!!.scheme ?: ""
        val host = uri.host ?: ""
        val full = content.trim()

        val details = StringBuilder().apply {
            append("Esquema: $scheme\n")
            append("Dominio: $host\n")
            if (!uri.path.isNullOrBlank()) append("Ruta: ${uri.path}\n")
            if (!uri.query.isNullOrBlank()) append("Query: ${uri.query}\n")
        }.toString()

        // 1. HTTP sin cifrado
        if (scheme == "http") {
            score += 2
            reasons += "La URL usa HTTP sin cifrado; es más fácil interceptar o modificar el tráfico."
        }

        // 2. Host como IP
        val ipRegex = Regex("^\\d+\\.\\d+\\.\\d+\\.\\d+$")
        if (ipRegex.matches(host)) {
            score += 3
            reasons += "El enlace apunta a una dirección IP directa en lugar de un dominio, común en enlaces maliciosos."
        }

        // 3. TLD sospechosos
        val suspiciousTlds = listOf(
            ".ru", ".tk", ".xyz", ".top", ".zip", ".click", ".fit", ".monster"
        )
        if (suspiciousTlds.any { host.endsWith(it, ignoreCase = true) }) {
            score += 3
            reasons += "El dominio usa un TLD frecuentemente asociado a campañas de phishing o spam."
        }

        // 4. URL muy larga
        if (full.length > 120) {
            score += 1
            reasons += "La URL es inusualmente larga, lo que puede ocultar parámetros maliciosos."
        }

        // 5. Caracter '@'
        if (full.contains("@")) {
            score += 3
            reasons += "La URL contiene '@', técnica usada para engañar al usuario sobre el dominio real."
        }

        // 6. Punycode
        if (host.contains("xn--")) {
            score += 2
            reasons += "El dominio contiene punycode (xn--), que puede usarse para simular dominios legítimos."
        }

        val label = when {
            score >= 6 -> RiskLabel.MALICIOUS
            score in 3..5 -> RiskLabel.SUSPICIOUS
            else -> RiskLabel.SAFE
        }

        if (reasons.isEmpty()) {
            reasons += "No se detectaron indicadores claros de riesgo, pero valida siempre el origen y el contexto."
        }

        return QrAnalysisResult(
            content = content,
            isUrl = true,
            label = label,
            score = score,
            reasons = reasons,
            parsedUri = uri,
            prettyDetails = details
        )
    }
}

/**
 * Extensión para abrir el resultado del análisis en el navegador.
 * - Usa parsedUri si existe.
 * - Si no, intenta parsear el content como Uri.
 */
fun QrAnalysisResult.openInBrowser(context: Context) {
    val uri = this.parsedUri ?: runCatching { Uri.parse(this.content) }.getOrNull()
    if (uri != null) {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}