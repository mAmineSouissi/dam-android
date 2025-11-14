package tn.esprit.dam_android.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import tn.esprit.dam_android.models.scan.ScanRequest
import tn.esprit.dam_android.models.scan.ScanResponse

interface ScanService {
    @POST("/api/devices/{deviceId}/scan")
    suspend fun uploadScan(
        @Path("deviceId") deviceId: String,
        @Header("Authorization") authorization: String,
        @Body request: ScanRequest
    ): Response<ScanResponse>
}