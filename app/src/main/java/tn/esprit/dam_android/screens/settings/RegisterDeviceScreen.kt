package tn.esprit.dam_android.screens.settings

import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import kotlinx.coroutines.launch
import tn.esprit.dam_android.api.local.RetrofitClient
import tn.esprit.dam_android.api.local.TokenManager
import tn.esprit.dam_android.models.auth.ErrorResponse
import tn.esprit.dam_android.models.auth.repositories.ApiResult
import tn.esprit.dam_android.models.auth.repositories.AuthRepository
import tn.esprit.dam_android.models.device.RegisterDeviceRequest
import tn.esprit.dam_android.ui.components.SGButton
import tn.esprit.dam_android.ui.components.SGCard
import tn.esprit.dam_android.ui.theme.ShadowGuardTheme
import tn.esprit.dam_android.ui.theme.Spacing
import tn.esprit.dam_android.utils.SharedPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceRegistrationScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isDeviceRegistered by rememberSaveable { mutableStateOf(false) }
    var registeredDeviceId by rememberSaveable { mutableStateOf<String?>(null) }
    var isCheckingStatus by rememberSaveable { mutableStateOf(true) }

    // Auto-fill device info
    val osVersion = Build.VERSION.RELEASE
    val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
    val appVersion = "1.2.3" // TODO: BuildConfig.VERSION_NAME

    // TokenManager (DataStore)
    val tokenManager = remember { TokenManager(context) }
    val authRepository = remember { AuthRepository(tokenManager) }

    // Get device identifier (Android ID)
    val deviceIdentifier = remember {
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"
    }

    // Check device registration on screen load
    LaunchedEffect(Unit) {
        val token = tokenManager.getTokenSync()
        if (!token.isNullOrEmpty() && deviceIdentifier != "unknown") {
            // Use the check endpoint to verify device registration
            val checkResult = authRepository.checkDeviceRegistration(
                deviceIdentifier = deviceIdentifier,
                platform = "android"
            )
            when (checkResult) {
                is ApiResult.Success -> {
                    val checkResponse = checkResult.data
                    isDeviceRegistered = checkResponse.isRegistered
                    if (checkResponse.isRegistered && checkResponse.device != null) {
                        val device = checkResponse.device
                        registeredDeviceId = device._id
                        // Save deviceIdentifier (primary identifier)
                        val returnedDeviceIdentifier = device.deviceIdentifier ?: deviceIdentifier
                        SharedPrefs.saveDeviceIdentifier(context, returnedDeviceIdentifier)
                    }
                }
                else -> {
                    // Check local SharedPrefs as fallback
                    val localDeviceIdentifier = SharedPrefs.getDeviceIdentifier(context)
                    if (!localDeviceIdentifier.isNullOrEmpty()) {
                        isDeviceRegistered = true
                        // Note: registeredDeviceId is for display only, we use deviceIdentifier for operations
                    }
                }
            }
        } else {
            // Check local SharedPrefs if no token
            val localDeviceIdentifier = SharedPrefs.getDeviceIdentifier(context)
            if (!localDeviceIdentifier.isNullOrEmpty()) {
                isDeviceRegistered = true
                // Note: registeredDeviceId is for display only, we use deviceIdentifier for operations
            }
        }
        isCheckingStatus = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Register Device", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(Spacing.base),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                if (isDeviceRegistered) Icons.Default.CheckCircle else Icons.Default.PhoneAndroid,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = if (isDeviceRegistered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            if (isCheckingStatus) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(Spacing.sm))
            }

            // Prominent status badge when device is registered
            if (isDeviceRegistered && !isCheckingStatus) {
                Surface(
                    modifier = Modifier.padding(horizontal = Spacing.base),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "Device Already Registered",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
            }

            Text(
                if (isDeviceRegistered) {
                    "This device is already registered and ready to use"
                } else {
                    "Register this device to enable app scanning"
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = if (isDeviceRegistered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

            if (isDeviceRegistered && registeredDeviceId != null) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    "Device ID: $registeredDeviceId",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Show registration status card if device is registered
            if (isDeviceRegistered && !isCheckingStatus) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.base),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Device Registered",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (registeredDeviceId != null) {
                                Text(
                                    "ID: $registeredDeviceId",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.base))
            }

            SGCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Spacing.base)) {
                    InfoRow("Platform", "Android")
                    InfoRow("OS Version", osVersion)
                    InfoRow("Device Model", deviceModel)
                    InfoRow("App Version", appVersion)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            errorMessage?.let {
                SGCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(Spacing.sm)
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.base))
            }

            SGButton(
                text = when {
                    isLoading -> "Registering..."
                    isDeviceRegistered -> "Device Already Registered"
                    else -> "Register Device"
                },
                onClick = {
                    if (isDeviceRegistered) {
                        // If already registered, just navigate to success
                        onSuccess()
                        return@SGButton
                    }
                    errorMessage = null
                    isLoading = true

                    scope.launch {
                        val token = tokenManager.getTokenSync() ?: ""
                        if (token.isEmpty()) {
                            errorMessage = "You must be logged in"
                            isLoading = false
                            return@launch
                        }

                        // Get device identifier (Android ID)
                        val deviceIdentifier = Settings.Secure.getString(
                            context.contentResolver,
                            Settings.Secure.ANDROID_ID
                        ) ?: "unknown"

                        try {
                            val response = RetrofitClient.deviceService.registerDevice(
                                authorization = "Bearer $token",
                                request = RegisterDeviceRequest(
                                    platform = "android",
                                    osVersion = osVersion,
                                    deviceModel = deviceModel,
                                    appVersion = appVersion,
                                    deviceIdentifier = deviceIdentifier
                                )
                            )

                            if (response.isSuccessful && response.body() != null) {
                                val result = response.body()!!
                                val returnedDeviceIdentifier = result.device.deviceIdentifier ?: deviceIdentifier
                                val isRegistered = result.isRegistered ?: false
                                
                                // Save deviceIdentifier only (primary identifier for all operations)
                                SharedPrefs.saveDeviceIdentifier(context, returnedDeviceIdentifier)
                                
                                val message = if (isRegistered) {
                                    "Device already registered"
                                } else {
                                    "Device registered successfully"
                                }
                                
                                snackbarHostState.showSnackbar(message)
                                onSuccess()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = try {
                                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                                    errorResponse.message
                                } catch (e: Exception) {
                                    "Registration failed: HTTP ${response.code()}"
                                }
                                throw Exception(errorMessage)
                            }
                        } catch (e: Exception) {
                            errorMessage = "Failed: ${e.message}"
                            snackbarHostState.showSnackbar("Registration failed: ${e.message}")
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && !isCheckingStatus
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceRegistrationScreenPreview() {
    ShadowGuardTheme {
        DeviceRegistrationScreen(onBack = {}, onSuccess = {})
    }
}
