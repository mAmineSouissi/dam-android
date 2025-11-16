package tn.esprit.dam_android.models.user

import com.google.gson.annotations.SerializedName

data class User(
    // backend sometimes returns `_id`, sometimes `id`. Keep both and expose a convenience id.
    @SerializedName("_id")
    val _id: String? = null,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("surname")
    val surname: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("avatar")
    val avatar: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("isDeviceRegistered")
    val isDeviceRegistered: Boolean? = null,
) {
    val uid: String
        get() = _id ?: id.orEmpty()
}

data class RegisterRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("surname")
    val surname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("phone")
    val phone: String
)

data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("surname")
    val surname: String? = null,

    @SerializedName("phone")
    val phone: String? = null
)