package tn.esprit.dam_android.api.local

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tn.esprit.dam_android.api.ScanService
import tn.esprit.dam_android.models.auth.services.AuthService
import tn.esprit.dam_android.models.device.services.DeviceService
import tn.esprit.dam_android.models.user.services.UserService
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5001/" // For Android Emulator
    // For physical device, use: "http://YOUR_IP:3000/"
    // For production, use: "https://api.shadowguard.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthService = retrofit.create(AuthService::class.java)
    val deviceService: DeviceService by lazy {
        retrofit.create(DeviceService::class.java)
    }
    val scanService: ScanService by lazy {
        retrofit.create(ScanService::class.java)
    }
    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}