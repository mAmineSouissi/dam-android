package tn.esprit.dam_android.models.auth.services

import retrofit2.Response
import retrofit2.http.*
import tn.esprit.dam_android.models.auth.*
import tn.esprit.dam_android.models.user.RegisterRequest
import tn.esprit.dam_android.models.user.User

interface AuthService {

    // Register - POST /api/users/register
    @POST("/api/users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    // Login - POST /api/auth/login
    @POST("/api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Get Current User - GET /api/users/profile
    @GET("/api/users/profile")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<User>

    // Refresh Token - POST /api/auth/refresh
    @POST("/api/auth/refresh")
    suspend fun refreshToken(
        @Body refreshToken: Map<String, String>
    ): Response<LoginResponse>

    // Forgot Password - POST /api/auth/forgot-password
    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<GenericMessageResponse>

    // Verify OTP - POST /api/auth/verify-otp
    @POST("/api/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<VerifyOtpResponse>

    // Reset Password - POST /api/auth/reset-password
    @POST("/api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<GenericMessageResponse>
}