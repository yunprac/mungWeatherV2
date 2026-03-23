package com.example.myapp.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.NavigationTarget
import com.example.myapp.data.repository.AuthRepository
import com.example.myapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onPasswordConfirmationChange(passwordConfirmation: String) {
        _uiState.update {
            it.copy(
                passwordConfirmation = passwordConfirmation,
                errorMessage = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun togglePasswordConfirmationVisibility() {
        _uiState.update {
            it.copy(passwordConfirmationVisible = !it.passwordConfirmationVisible)
        }
    }

    fun signup() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password
        val passwordConfirmation = state.passwordConfirmation

        when {
            email.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "이메일을 입력해주세요.") }
                return
            }

            password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "비밀번호를 입력해주세요.") }
                return
            }

            passwordConfirmation.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "비밀번호 확인을 입력해주세요.") }
                return
            }

            password != passwordConfirmation -> {
                _uiState.update { it.copy(errorMessage = "비밀번호가 일치하지 않습니다.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    navigationTarget = NavigationTarget.None
                )
            }

            val signupResult = authRepository.signup(email, password)

            if (signupResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = signupResult.exceptionOrNull()?.message ?: "회원가입에 실패했습니다."
                    )
                }
                return@launch
            }

            val firebaseUser = signupResult.getOrNull()
            val uid = firebaseUser?.uid
            val savedEmail = firebaseUser?.email ?: email

            if (uid.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "회원가입 후 사용자 정보를 가져오지 못했습니다."
                    )
                }
                return@launch
            }

            val createProfileResult = userRepository.createUserProfile(uid = uid, email = savedEmail)
            createProfileResult.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "사용자 프로필 생성에 실패했습니다."
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    navigationTarget = NavigationTarget.Login
                )
            }
        }
    }
}
