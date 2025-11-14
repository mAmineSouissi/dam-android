package tn.esprit.dam_android.models.device.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import tn.esprit.dam_android.models.device.DeviceStatusResponse
import tn.esprit.dam_android.models.device.RegisterDeviceRequest
import tn.esprit.dam_android.models.device.RegisterDeviceResponse

interface DeviceService {
    @POST("/api/devices/register")
    suspend fun registerDevice(
        @Header("Authorization") authorization: String,
        @Body request: RegisterDeviceRequest
    ): Response<RegisterDeviceResponse>
    
    @GET("/api/devices/status")
    suspend fun getDeviceStatus(
        @Header("Authorization") authorization: String
    ): Response<DeviceStatusResponse>
}