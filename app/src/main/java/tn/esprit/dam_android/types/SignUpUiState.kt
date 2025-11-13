// tn.esprit.dam_android.types/SignUpUiState.kt
package tn.esprit.dam_android.types

data class SignUpUiState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phone: String = "",

    // UI flags
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val acceptedTerms: Boolean = false,
    val isLoading: Boolean = false,

    // Errors
    val nameError: String? = null,
    val surnameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val phoneError: String? = null,
    val errorMessage: String? = null
)