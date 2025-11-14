package tn.esprit.dam_android.models.auth.repositories

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tn.esprit.dam_android.api.local.RetrofitClient
import tn.esprit.dam_android.api.local.RetrofitClient.deviceService
import tn.esprit.dam_android.api.local.TokenManager
import tn.esprit.dam_android.models.auth.*
import tn.esprit.dam_android.models.device.DeviceStatusResponse
import tn.esprit.dam_android.models.device.RegisterDeviceRequest
import tn.esprit.dam_android.models.device.RegisterDeviceResponse
import tn.esprit.dam_android.models.user.RegisterRequest

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

class AuthRepository(private val tokenManager: TokenManager) {

    private val authApi = RetrofitClient.authApi
    private val TAG = "AuthRepository"

    /**
     * Register a new user
     * POST /api/users/register
     */
    suspend fun register(registerRequest: RegisterRequest): ApiResult<RegisterResponse> {
        return try {
            Log.d(TAG, "Registering user: ${registerRequest.email}")
            val response = authApi.register(registerRequest)

            if (response.isSuccessful && response.body() != null) {
                val registerResponse = response.body()!!
                Log.d(TAG, "Registration successful: ${registerResponse.email}")
                ApiResult.Success(registerResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Registration failed: ${response.message()}"
                }
                Log.e(TAG, "Registration failed: $errorMessage")
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration exception: ${e.message}", e)
            ApiResult.Error(e.message ?: "Network error occurred")
        }
    }

    /**
     * Login user
     * POST /api/auth/login
     */
    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        return try {
            Log.d(TAG, "Logging in user: $email")
            val response = authApi.login(
                LoginRequest(email = email, password = password)
            )

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!

                // Save tokens
                tokenManager.saveToken(loginResponse.accessToken)
                tokenManager.saveRefreshToken(loginResponse.refreshToken)

                // Save user info
                tokenManager.saveUserInfo(loginResponse.user.uid, loginResponse.user.email)

                Log.d(TAG, "Login successful for user: ${loginResponse.user.email}")
                ApiResult.Success(loginResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Login failed: ${response.message()}"
                }
                Log.e(TAG, "Login failed: $errorMessage")
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login exception: ${e.message}", e)
            ApiResult.Error(e.message ?: "Network error occurred")
        }
    }

    /**
     * Forgot Password - Send reset code
     * POST /api/auth/forgot-password
     */
    suspend fun forgotPassword(email: String): ApiResult<GenericMessageResponse> {
        return try {
            Log.d(TAG, "Requesting password reset for: $email")
            val response = authApi.forgotPassword(
                ForgotPasswordRequest(email = email)
            )

            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d(TAG, "Password reset email sent: ${result.message}")
                ApiResult.Success(result)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Failed to send reset email"
                }
                Log.e(TAG, "Forgot password failed: $errorMessage")
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Forgot password exception: ${e.message}", e)
            ApiResult.Error(e.message ?: "Network error occurred")
        }
    }

    /**
     * Verify OTP Code
     * POST /api/auth/verify-otp
     */
    suspend fun verifyOtp(email: String, otp: String): ApiResult<VerifyOtpResponse> {
        return try {
            Log.d(TAG, "Verifying OTP for: $email")
            val response = authApi.verifyOtp(
                VerifyOtpRequest(email = email, otp = otp)
            )

            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d(TAG, "OTP verification result: ${result.valid}")
                ApiResult.Success(result)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "OTP verification failed"
                }
                Log.e(TAG, "OTP verification failed: $errorMessage")
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "OTP verification exception: ${e.message}", e)
            ApiResult.Error(e.message ?: "Network error occurred")
        }
    }

    /**
     * Reset Password with OTP
     * POST /api/auth/reset-password
     */
    suspend fun resetPassword(email: String, resetToken: String, newPassword: String): ApiResult<GenericMessageResponse> {
        return try {
            Log.d(TAG, "Resetting password for: $email")
            val response = authApi.resetPassword(
                ResetPasswordRequest(
                    email = email,
                    resetToken = resetToken,
                    newPassword = newPassword
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d(TAG, "Password reset successful: ${result.message}")
                ApiResult.Success(result)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Password reset failed"
                }
                Log.e(TAG, "Password reset failed: $errorMessage")
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Password reset exception: ${e.message}", e)
            ApiResult.Error(e.message ?: "Network error occurred")
        }
    }

    /**
     * Refresh access token
     * POST /api/auth/refresh
     */
    suspend fun refreshToken(): ApiResult<LoginResponse> {
        return try {
            // Get refresh token from storage
            val refreshToken = tokenManager.getRefreshTokenSync()
            if (refreshToken.isNullOrEmpty()) {
                return ApiResult.Error("No refresh token available")
            }

            Log.d(TAG, "Refreshing access token")
            val response = authApi.refreshToken(
                mapOf("refreshToken" to refreshToken)
            )

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!

                // Update tokens
                tokenManager.saveToken(loginResponse.accessToken)
                tokenManager.saveRefreshToken(loginResponse.refreshToken)

                Log.d(TAG, "Token refresh successful")
                ApiResult.Success(loginResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Token refresh failed"
                }
                Log.e(TAG, "Token refresh failed: $errorMessage")
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Token refresh exception: ${e.message}", e)
            ApiResult.Error(e.message ?: "Network error occurred")
        }
    }

    /**
     * Logout - Clear tokens
     */
     suspend fun logout() {
        Log.d(TAG, "Logging out user")
        tokenManager.clearToken()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return tokenManager.getToken().map { token ->
            !token.isNullOrEmpty()
        }
    }

    /**
     * Get current user profile
     * GET /api/users/profile
     */
    suspend fun getCurrentUser(): ApiResult<tn.esprit.dam_android.models.user.User> {
        return try {
            val token = tokenManager.getTokenSync()
            if (token.isNullOrEmpty()) {
                return ApiResult.Error("No access token available")
            }

            Log.d(TAG, "Fetching current user profile")
            val response = authApi.getCurrentUser("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                Log.d(TAG, "User profile fetched: ${user.email}")
                ApiResult.Success(user)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Failed to fetch user profile"
                }
                Log.e(TAG, "Get user failed: $errorMessage")
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get user exception: ${e.message}", e)
            ApiResult.Error(e.message ?: "Network error occurred")
        }
    }

    suspend fun registerDevice(
        platform: String,
        osVersion: String,
        deviceModel: String,
        appVersion: String,
        deviceIdentifier: String
    ): ApiResult<RegisterDeviceResponse> {
        return try {
            val token = tokenManager.getTokenSync()
            if (token?.isEmpty() ?: true) return ApiResult.Error("Not authenticated")

            val response = deviceService.registerDevice(
                authorization = "Bearer $token",
                request = RegisterDeviceRequest(
                    platform = platform,
                    osVersion = osVersion,
                    deviceModel = deviceModel,
                    appVersion = appVersion,
                    deviceIdentifier = deviceIdentifier
                )
            )

            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Registration failed: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error")
        }
    }

    /**
     * Get device registration status
     * GET /api/devices/status
     */
    suspend fun getDeviceStatus(): ApiResult<DeviceStatusResponse> {
        return try {
            var token = tokenManager.getTokenSync()
            if (token?.isEmpty() ?: true) return ApiResult.Error("Not authenticated")

            Log.d(TAG, "Fetching device status")
            var response = deviceService.getDeviceStatus("Bearer $token")

            // If 401, try to refresh token and retry
            if (response.code() == 401) {
                Log.d(TAG, "Token expired, attempting refresh")
                val refreshResult = refreshToken()
                if (refreshResult is ApiResult.Success) {
                    // Retry with new token
                    token = tokenManager.getTokenSync()
                    if (!token.isNullOrEmpty()) {
                        Log.d(TAG, "Token refreshed, retrying device status")
                        response = deviceService.getDeviceStatus("Bearer $token")
                    } else {
                        return ApiResult.Error("Token refresh failed", 401)
                    }
                } else {
                    Log.e(TAG, "Token refresh failed, returning 401")
                    return ApiResult.Error("Unauthorized - please login again", 401)
                }
            }

            if (response.isSuccessful && response.body() != null) {
                val status = response.body()!!
                Log.d(TAG, "Device status: isRegistered=${status.isDeviceRegistered}, count=${status.deviceCount}")
                ApiResult.Success(status)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Failed to fetch device status"
                }
                Log.e(TAG, "Get device status failed: $errorMessage (code: ${response.code()})")
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get device status exception: ${e.message}", e)
            ApiResult.Error(e.message ?: "Network error occurred")
        }
    }

}