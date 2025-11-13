// screens/login/ForgotPasswordScreen.kt
package tn.esprit.dam_android.screens.login

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import tn.esprit.dam_android.navigation.Screen
import tn.esprit.dam_android.ui.components.*
import tn.esprit.dam_android.ui.theme.ShadowGuardTheme
import tn.esprit.dam_android.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onSendSuccess: (email: String) -> Unit,
    authRepository: AuthRepository
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
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
                text = "Reset Password",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "Enter your email to receive a reset code",
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

            // Email Field
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

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Send Button
            SGButton(
                text = if (isLoading) "Sending..." else "Send Reset Code",
                onClick = {
                    errorMessage = null
                    emailError = null

                    if (email.isBlank()) {
                        emailError = "Email is required"
                        return@SGButton
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Invalid email format"
                        return@SGButton
                    }

                    isLoading = true

                    scope.launch {
                        when (val result = authRepository.forgotPassword(email)) {
                            is ApiResult.Success -> {
                                isLoading = false
                                snackbarHostState.showSnackbar("Reset code sent to your email")
                                onSendSuccess(email)
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
fun ForgotPasswordScreenPreview() {
    ShadowGuardTheme {
        val context = LocalContext.current
        val tokenManager = remember { TokenManager(context) }
        val authRepository = remember { AuthRepository(tokenManager) }
        ForgotPasswordScreen(
            onBack = {},
            onSendSuccess = {},
            authRepository = authRepository,

        )
    }
}