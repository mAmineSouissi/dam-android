package tn.esprit.dam_android.models.device


data class RegisterDeviceRequest(
    val platform: String,
    val osVersion: String,
    val deviceModel: String,
    val appVersion: String,
    val deviceIdentifier: String
)

data class RegisterDeviceResponse(
    val message: String,
    val device: DeviceInfo,
    val isRegistered: Boolean? = null
)

data class DeviceInfo(
    val _id: String,
    val platform: String,
    val osVersion: String,
    val deviceModel: String,
    val appVersion: String,
    val deviceIdentifier: String? = null,
    val lastSeen: String? = null,
    val lastScanAt: String? = null,
    val lastRiskScore: Int? = null
)

data class DeviceStatusResponse(
    val isDeviceRegistered: Boolean,
    val deviceCount: Int,
    val devices: List<DeviceInfo>
)