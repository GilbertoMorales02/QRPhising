package com.example.qr.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface VirusTotalService {
    @GET("urls/{id}")
    suspend fun getUrlReport(
        @Header("x-apikey") apiKey: String,
        @Path("id") id: String
    ): VirusTotalResponse
}

data class VirusTotalResponse(
    @SerializedName("data") val data: UrlData
)

data class UrlData(
    @SerializedName("attributes") val attributes: UrlAttributes
)

data class UrlAttributes(
    @SerializedName("last_analysis_stats") val lastAnalysisStats: LastAnalysisStats
)

data class LastAnalysisStats(
    @SerializedName("malicious") val malicious: Int,
    @SerializedName("suspicious") val suspicious: Int,
    @SerializedName("harmless") val harmless: Int,
    @SerializedName("undetected") val undetected: Int
)
