package tn.esprit.dam_android.screens.signup

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SegmentedButtonDefaults.Icon
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
import tn.esprit.dam_android.models.user.RegisterRequest
import tn.esprit.dam_android.ui.components.*
import tn.esprit.dam_android.ui.theme.ShadowGuardTheme
import tn.esprit.dam_android.ui.theme.Spacing

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authRepository: AuthRepository // Injected or passed
) {
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }

    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var surnameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneError by rememberSaveable { mutableStateOf<String?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.base)
        ) {
            // Back Button
            IconButton(onClick = onNavigateToLogin) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.height(Spacing.base))

            // Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Shield, null, tint = MaterialTheme.colorScheme.background)
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.base))
                Text("Create Account", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text("Join ShadowGuard for better privacy", style = MaterialTheme.typography.bodyMedium)
            }

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

            // Name
            SGTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = "Name (Optional)",
                placeholder = "Enter your name",
                isError = nameError != null,
                errorMessage = nameError,
                leadingIcon = { Icon(Icons.Default.Person, null) }
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            // Surname
            SGTextField(
                value = surname,
                onValueChange = {
                    surname = it
                    surnameError = null
                },
                label = "Surname (Optional)",
                placeholder = "Enter your surname",
                isError = surnameError != null,
                errorMessage = surnameError,
                leadingIcon = { Icon(Icons.Default.Person, null) }
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            // Email
            SGTextField(
                value = email,
                onValueChange = {
                    email = it
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

            Spacer(modifier = Modifier.height(Spacing.base))

            // Confirm Password
            SGTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                label = "Confirm Password",
                placeholder = "Re-enter your password",
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

            // Phone
            SGTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    phoneError = null
                },
                label = "Phone (Optional)",
                placeholder = "+1234567890",
                keyboardType = KeyboardType.Phone,
                isError = phoneError != null,
                errorMessage = phoneError,
                leadingIcon = { Icon(Icons.Default.Phone, null) }
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            // Password Requirements
            SGCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text("Password Requirements:", style = MaterialTheme.typography.labelMedium)
                    PasswordRequirement("At least 6 characters", password.length >= 6)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.base))

            // Terms
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = acceptedTerms, onCheckedChange = { acceptedTerms = it })
                Spacer(modifier = Modifier.width(Spacing.sm))
                Column {
                    Row {
                        Text("I agree to the ", style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { /* Open Terms */ }, contentPadding = PaddingValues(0.dp)) {
                            Text("Terms", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    TextButton(onClick = { /* Open Privacy */ }, contentPadding = PaddingValues(0.dp)) {
                        Text("& Privacy Policy", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Sign Up Button
            SGButton(
                text = if (isLoading) "Creating Account..." else "Create Account",
                onClick = {
                    // Reset errors
                    nameError = null; surnameError = null; emailError = null
                    passwordError = null; confirmPasswordError = null; phoneError = null
                    errorMessage = null

                    var hasError = false

                    if (email.isBlank()) {
                        emailError = "Email is required"; hasError = true
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Invalid email"; hasError = true
                    }

                    if (password.isBlank()) {
                        passwordError = "Password is required"; hasError = true
                    } else if (password.length < 6) {
                        passwordError = "≥6 characters"; hasError = true
                    }

                    if (confirmPassword != password) {
                        confirmPasswordError = "Passwords do not match"; hasError = true
                    }

                    if (phone.isNotBlank() && !phone.matches(Regex("^\\+?[1-9]\\d{1,14}\$"))) {
                        phoneError = "Invalid phone"; hasError = true
                    }

                    if (!acceptedTerms) {
                        errorMessage = "Accept Terms & Privacy"; hasError = true
                    }

                    if (hasError) return@SGButton

                    isLoading = true
                    errorMessage = null

                    scope.launch {
                        val req = RegisterRequest(
                            name = name,
                            surname = surname,
                            email = email,
                            password = password,
                            phone = phone
                        )

                        when (val result = authRepository.register(req)) {
                            is ApiResult.Success -> {
                                isLoading = false
                                onSignUpSuccess()
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

            // Login Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToLogin) {
                    Text("Sign In", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun PasswordRequirement(text: String, isMet: Boolean) {
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
            color = if (isMet) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    ShadowGuardTheme {
        SignUpScreen(
            onSignUpSuccess = {},
            onNavigateToLogin = {},
            authRepository = AuthRepository(
                TokenManager(LocalContext.current)
            )
        )
    }
}
