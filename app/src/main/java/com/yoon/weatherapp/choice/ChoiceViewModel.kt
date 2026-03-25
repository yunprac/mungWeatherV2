package com.yoon.weatherapp.choice

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ChoiceViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChoiceUiState())
    val uiState: StateFlow<ChoiceUiState> = _uiState.asStateFlow()

    fun onProfileClick() {
        _uiState.update { it.copy(showProfileDialog = true) }
    }

    fun dismissProfileDialog() {
        _uiState.update { it.copy(showProfileDialog = false) }
    }

    fun onBasicClick() {
        _uiState.update {
            it.copy(
                profileImageType = ProfileImageType.BASIC,
                imageUri = null,
                showProfileDialog = false
            )
        }
    }

    fun onAlbumClick() {
        _uiState.update { it.copy(showProfileDialog = false) }
    }

    fun onAlbumImageSelected(uri: Uri?) {
        if (uri == null) return

        _uiState.update {
            it.copy(
                profileImageType = ProfileImageType.ALBUM,
                imageUri = uri
            )
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onBreedChange(breed: String) {
        _uiState.update { it.copy(breed = breed, errorMessage = null) }
    }

    fun saveProfile(onSuccess: () -> Unit, onFail: (String) -> Unit) {
        fun fail(message: String) {
            _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            onFail(message)
        }

        val currentUser = authRepository.currentUser
        if (currentUser == null) {
            fail("로그인한 사용자가 없습니다.")
            return
        }

        val currentState = uiState.value
        val name = currentState.name.trim()
        val breed = currentState.breed.trim()

        when {
            name.isBlank() -> {
                fail("이름을 입력해주세요.")
                return
            }

            breed.isBlank() -> {
                fail("견종을 선택해주세요.")
                return
            }

            currentState.profileImageType == ProfileImageType.NONE -> {
                fail("프로필 이미지를 선택해주세요.")
                return
            }
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            val result = userRepository.saveProfile(
                uid = currentUser.uid,
                name = name,
                breed = breed,
                imageUri = currentState.imageUri,
                isDefaultImage = currentState.profileImageType == ProfileImageType.BASIC
            )

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { e ->
                    val message = e.message ?: "프로필 저장 중 오류가 발생했습니다."
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                    onFail(message)
                }
        }
    }
}
