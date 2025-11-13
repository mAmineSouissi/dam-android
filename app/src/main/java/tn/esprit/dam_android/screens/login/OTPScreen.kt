package tn.esprit.dam_android.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tn.esprit.dam_android.api.local.TokenManager
import tn.esprit.dam_android.models.auth.repositories.ApiResult
import tn.esprit.dam_android.models.auth.repositories.AuthRepository
import tn.esprit.dam_android.ui.components.*
import tn.esprit.dam_android.ui.theme.ShadowGuardTheme
import tn.esprit.dam_android.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    email: String,
    onBack: () -> Unit,
    onVerifySuccess: (resetToken: String) -> Unit,
    authRepository: AuthRepository
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var otp by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var otpError by rememberSaveable { mutableStateOf<String?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(Spacing.base),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Verify OTP",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "Enter the 6-digit code sent to $email",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Global Error
            if (errorMessage != null) {
                SGCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.base))
            }

            // OTP Field
            SGTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 6) {
                        otp = it
                        otpError = null
                    }
                },
                label = "OTP Code",
                placeholder = "Enter 6-digit code",
                keyboardType = KeyboardType.Number,
                isError = otpError != null,
                errorMessage = otpError
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Verify Button
            SGButton(
                text = if (isLoading) "Verifying..." else "Verify Code",
                onClick = {
                    errorMessage = null
                    otpError = null

                    if (otp.length != 6) {
                        otpError = "OTP must be 6 digits"
                        return@SGButton
                    }

                    isLoading = true

                    scope.launch {
                        when (val result = authRepository.verifyOtp(email, otp)) {
                            is ApiResult.Success -> {
                                isLoading = false
                                if (result.data.valid) {
                                    snackbarHostState.showSnackbar("OTP verified")
                                    onVerifySuccess(otp)  // Use OTP as resetToken
                                } else {
                                    errorMessage = "Invalid OTP code"
                                }
                            }
                            is ApiResult.Error -> {
                                isLoading = false
                                errorMessage = result.message
                            }
                            else -> {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.PRIMARY,
                enabled = !isLoading
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OtpScreenPreview() {
    ShadowGuardTheme {
        val context = LocalContext.current
        val tokenManager = remember { TokenManager(context) }
        val authRepository = remember { AuthRepository(tokenManager) }
        OtpScreen(
            email = "test@example.com",
            onBack = {},
            onVerifySuccess = {},
            authRepository = authRepository,
        )
    }
}