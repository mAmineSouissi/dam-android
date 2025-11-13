package tn.esprit.dam_android.models.auth

import com.google.gson.annotations.SerializedName
import tn.esprit.dam_android.models.user.User


data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

data class VerifyOtpRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("otp")
    val otp: String
)

data class ResetPasswordRequest(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("resetToken")
    val resetToken: String,

    @SerializedName("newPassword")
    val newPassword: String
)

// Response Models
data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("user")
    val user: User
)

data class RegisterResponse(
    @SerializedName("_id")
    val _id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("surname")
    val surname: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("role")
    val role: String? = null
)


data class GenericMessageResponse(
    @SerializedName("message")
    val message: String
)

data class VerifyOtpResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("valid")
    val valid: Boolean
)

data class ErrorResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("statusCode")
    val statusCode: Int
)