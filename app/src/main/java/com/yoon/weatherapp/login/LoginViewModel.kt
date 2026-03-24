package com.yoon.weatherapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoon.weatherapp.NavigationTarget
import com.yoon.weatherapp.data.repository.AuthRepository
import com.yoon.weatherapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            passwordVisible = !_uiState.value.passwordVisible
        )
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        when {
            email.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "이메일을 입력해주세요.") }
                return
            }
            password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "비밀번호를 입력해주세요.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                navigationTarget = NavigationTarget.None,
                firstLogin = false
            )

            val result = authRepository.login(email, password)

            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "로그인에 실패했습니다."
                )
                return@launch
            }

            val firebaseUser = result.getOrNull()
            val uid = firebaseUser?.uid
            val savedEmail = firebaseUser?.email

            if (uid.isNullOrBlank() || savedEmail.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "로그인한 사용자 정보를 가져오지 못했습니다."
                )
                return@launch
            }

            handleLoginUser(uid, savedEmail)
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    navigationTarget = NavigationTarget.None,
                    firstLogin = false
                )
            }

            val googleResult = authRepository.loginWithGoogle(idToken)

            if (googleResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = googleResult.exceptionOrNull()?.message ?: "구글 로그인에 실패했습니다."
                    )
                }
                return@launch
            }

            val firebaseUser = googleResult.getOrNull()
            val uid = firebaseUser?.uid
            val savedEmail = firebaseUser?.email

            if (uid.isNullOrBlank() || savedEmail.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "구글 계정 정보를 가져오지 못했습니다."
                    )
                }
                return@launch
            }

            handleLoginUser(uid, savedEmail)
        }
    }

    private suspend fun handleLoginUser(uid: String, email: String) {
        val profileResult = userRepository.getUserProfile(uid)

        if (profileResult.isFailure) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = profileResult.exceptionOrNull()?.message ?: "사용자 정보 조회에 실패했습니다."
            )
            return
        }

        val profile = profileResult.getOrNull()

        if (profile == null) {
            val createProfileResult = userRepository.createUserProfile(
                uid = uid,
                email = email
            )

            if (createProfileResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = createProfileResult.exceptionOrNull()?.message ?: "사용자 문서 생성에 실패했습니다."
                )
                return
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                navigationTarget = NavigationTarget.Login,
                firstLogin = true
            )
            return
        }

        val isFirstLogin = profile.name.isNullOrBlank() || profile.breed.isNullOrBlank()

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            navigationTarget = NavigationTarget.Login,
            firstLogin = isFirstLogin
        )
    }
}
