package tn.esprit.dam_android.screens.login

import tn.esprit.dam_android.types.LoginUiState

data class ValidationResult(
    val isValid: Boolean,
    val emailError: String? = null,
    val passwordError: String? = null
)

fun validateLoginForm(uiState: LoginUiState): ValidationResult {
    var emailError: String? = null
    var passwordError: String? = null

    // Validate Email
    when {
        uiState.email.isBlank() -> {
            emailError = "Email is required"
        }
        !isValidEmail(uiState.email) -> {
            emailError = "Please enter a valid email"
        }
    }

    // Validate Password
    when {
        uiState.password.isBlank() -> {
            passwordError = "Password is required"
        }
        uiState.password.length < 6 -> {
            passwordError = "Password must be at least 6 characters"
        }
    }

    val isValid = emailError == null && passwordError == null

    return ValidationResult(
        isValid = isValid,
        emailError = emailError,
        passwordError = passwordError
    )
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}