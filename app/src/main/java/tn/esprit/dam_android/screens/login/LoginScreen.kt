package tn.esprit.dam_android.screens.login

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    authRepository: AuthRepository
) {
    val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.base),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LogoSection()

            Spacer(modifier = Modifier.height(Spacing.xxl))

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

            // Email
            SGTextField(
                value = email,
                onValueChange = {
                    email = it.trim()
                    emailError = null
                },
                label = "Email",
                placeholder = "Enter your email",
                keyboardType = KeyboardType.Email,
                isError = emailError != null,
                errorMessage = emailError,
                leadingIcon = { Icon(Icons.Default.Email, null) }
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            // Password
            SGTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = "Password",
                placeholder = "Enter your password",
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

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Forgot Password
            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Login Button
            SGButton(
                text = if (isLoading) "Signing In..." else "Sign In",
                onClick = {
                    // Reset errors
                    emailError = null
                    passwordError = null
                    errorMessage = null

                    var hasError = false

                    if (email.isBlank()) {
                        emailError = "Email is required"; hasError = true
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Invalid email"; hasError = true
                    }

                    if (password.isBlank()) {
                        passwordError = "Password is required"; hasError = true
                    } else if (password.length < 6) {
                        passwordError = "Password too short"; hasError = true
                    }

                    if (hasError) return@SGButton

                    isLoading = true

                    scope.launch {
                        when (val result = authRepository.login(email, password)) {
                            is ApiResult.Success -> {
                                isLoading = false
                                onLoginSuccess()
                            }
                            is ApiResult.Error -> {
                                isLoading = false
                                errorMessage = result.message
                            }
                            else -> Unit
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.PRIMARY,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            // Sign Up Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToSignUp) {
                    Text("Sign Up", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
private fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(80.dp), shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.primary) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Shield, null, tint = MaterialTheme.colorScheme.background)
            }
        }
        Spacer(modifier = Modifier.height(Spacing.base))
        Text("ShadowGuard", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text("Protect Your Privacy", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    ShadowGuardTheme {
        val context = LocalContext.current
        val tokenManager = remember { TokenManager(context) }
        val authRepository = remember { AuthRepository(tokenManager) }
        LoginScreen(
            onLoginSuccess = {},
            onNavigateToSignUp = {},
            onForgotPassword = {},
            authRepository = authRepository
        )
    }
}