package tn.esprit.dam_android.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tn.esprit.dam_android.api.local.TokenManager
import tn.esprit.dam_android.models.auth.repositories.ApiResult
import tn.esprit.dam_android.models.auth.repositories.AuthRepository
import tn.esprit.dam_android.types.LoginUiState

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val authRepository = AuthRepository(tokenManager)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login(onSuccess: () -> Unit) {
        val validation = validateLoginForm(_uiState.value)
        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    emailError = validation.emailError,
                    passwordError = validation.passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.login(_uiState.value.email, _uiState.value.password)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
                is ApiResult.Loading -> {
                    // Already handled
                }
            }
        }
    }
}