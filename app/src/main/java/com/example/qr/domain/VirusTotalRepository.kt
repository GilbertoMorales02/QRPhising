package com.example.qr.domain

import android.util.Base64
import com.example.qr.data.remote.VirusTotalService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets

class VirusTotalRepository {

    private val service: VirusTotalService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.virustotal.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(VirusTotalService::class.java)
    }

    suspend fun checkUrl(url: String, apiKey: String): Boolean {
        try {
            // The URL ID is the base64 encoding of the URL without the padding characters
            val urlId = Base64.encodeToString(url.toByteArray(StandardCharsets.UTF_8), Base64.NO_PADDING or Base64.NO_WRAP)
            val response = service.getUrlReport(apiKey, urlId)
            val stats = response.data.attributes.lastAnalysisStats
            
            // Simple logic: if malicious or suspicious count > 0, consider it unsafe
            return stats.malicious > 0 || stats.suspicious > 0
        } catch (e: Exception) {
            e.printStackTrace()
            // In case of error (e.g. 404 not found which means unknown url), we might treat as unknown/safe or handle differently.
            // For this example, let's assume false (safe) if check fails or it's just not found in VT yet.
            // A production app should handle 404 specifically.
            return false
        }
    }
}
