package tn.esprit.dam_android.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
fun ResetPasswordScreen(
    email: String,
    resetToken: String,
    onBack: () -> Unit,
    onResetSuccess: () -> Unit,
    authRepository: AuthRepository
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
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
                text = "Set New Password",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "Enter your new password",
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

            // Password Field
            SGTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = "New Password",
                placeholder = "Enter new password",
                isPassword = !isPasswordVisible,
                isError = passwordError != null,
                errorMessage = passwordError,
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle visibility"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            // Confirm Password Field
            SGTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                label = "Confirm Password",
                placeholder = "Confirm new password",
                isPassword = !isConfirmPasswordVisible,
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError,
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(
                            imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle visibility"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            // Password Requirements
            SGCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = "Password Requirements:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    PasswordRequirement(
                        text = "At least 6 characters",
                        isMet = password.length >= 6
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Reset Button
            SGButton(
                text = if (isLoading) "Resetting..." else "Reset Password",
                onClick = {
                    errorMessage = null
                    passwordError = null
                    confirmPasswordError = null

                    var hasError = false

                    if (password.isBlank()) {
                        passwordError = "Password is required"; hasError = true
                    } else if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"; hasError = true
                    }

                    if (confirmPassword != password) {
                        confirmPasswordError = "Passwords do not match"; hasError = true
                    }

                    if (hasError) return@SGButton

                    isLoading = true

                    scope.launch {
                        when (val result = authRepository.resetPassword(email, resetToken, password)) {
                            is ApiResult.Success -> {
                                isLoading = false
                                snackbarHostState.showSnackbar("Password reset successful")
                                onResetSuccess()
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

@Composable
private fun PasswordRequirement(
    text: String,
    isMet: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isMet)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordScreenPreview() {
    ShadowGuardTheme {
        val context = LocalContext.current
        val tokenManager = remember { TokenManager(context) }
        val authRepository = remember { AuthRepository(tokenManager) }
        ResetPasswordScreen(
            email = "test@example.com",
            resetToken = "123456",
            onBack = {},
            onResetSuccess = {},
            authRepository = authRepository
        )
    }
}