package com.yoon.weatherapp.home.viewModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoon.weatherapp.BuildConfig
import com.yoon.weatherapp.NavigationTarget
import com.yoon.weatherapp.data.location.LocationProvider
import com.yoon.weatherapp.data.repository.AIRepository
import com.yoon.weatherapp.data.repository.AuthRepository
import com.yoon.weatherapp.data.repository.UserRepository
import com.yoon.weatherapp.data.repository.WeatherRepository
import com.google.firebase.auth.GoogleAuthProvider
import com.yoon.weatherapp.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val weatherRepository: WeatherRepository,
    private val aiRepository: AIRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val locationProvider = LocationProvider(context)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadHomeData() {
        viewModelScope.launch {
            val currentUser = authRepository.currentUser
            if (currentUser == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "로그인한 사용자가 없습니다."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.getUserProfile(currentUser.uid)
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            email = profile?.email.orEmpty(),
                            name = profile?.name.orEmpty(),
                            breed = profile?.breed.orEmpty(),
                            imageUrl = profile?.imageUri,
                            isDefaultImage = profile?.isDefaultImage ?: true
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "사용자 정보를 불러오지 못했습니다."
                        )
                    }
                    return@launch
                }

            loadWeather()
        }
    }

    fun loadWeather() {
        viewModelScope.launch {
            if (!hasLocationPermission()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "위치 권한이 필요합니다."
                    )
                }
                return@launch
            }

            try {
                val location = locationProvider.getCurrentLocation()
                val weatherInfo = weatherRepository.getCurrentWeather(
                    lat = location.latitude,
                    lon = location.longitude,
                    apiKey = BuildConfig.OPENWEATHER_API_KEY,
                    context = context
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        locationText = weatherInfo.region,
                        temperatureText = "${weatherInfo.temperature}℃",
                        weatherStatusText = weatherInfo.weatherStatus,
                        outfitAiMessage = "",
                        timeAiMessage = "",
                        isOutfitAiLoading = false,
                        isTimeAiLoading = false
                    )
                }

                applyWeatherStage(_uiState.value.breed, weatherInfo.temperature)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "날씨 정보를 불러오지 못했습니다."
                    )
                }
            }
        }
    }

    fun updateCurrentPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }

    fun openProfilePanel() {
        _uiState.update { it.copy(isProfilePanelOpen = true) }
    }

    fun closeProfilePanel() {
        _uiState.update { it.copy(isProfilePanelOpen = false) }
    }

    fun openDogDescriptionSheet() {
        _uiState.update { it.copy(isDogDescriptionSheetOpen = true) }
    }

    fun closeDogDescriptionSheet() {
        _uiState.update { it.copy(isDogDescriptionSheetOpen = false) }
    }

    fun updateProfile(
        name: String,
        breed: String,
        imageUri: Uri?,
        isDefaultImage: Boolean,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        val currentUser = authRepository.currentUser
        if (currentUser == null) {
            onFailure("로그인한 사용자가 없습니다.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = userRepository.updateProfile(
                uid = currentUser.uid,
                name = name.trim(),
                breed = breed.trim(),
                imageUri = imageUri,
                isDefaultImage = isDefaultImage
            )

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = name.trim(),
                            breed = breed.trim(),
                            imageUrl = if (isDefaultImage) "default:${breed.trim()}" else imageUri?.toString(),
                            isDefaultImage = isDefaultImage,
                            outfitAiMessage = "",
                            timeAiMessage = ""
                        )
                    }
                    onSuccess()
                }
                .onFailure { e ->
                    val message = e.message ?: "프로필 수정 중 오류가 발생했습니다."
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                    onFailure(message)
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout(context)
            _uiState.update { it.copy(navigationTarget = NavigationTarget.Login) }
        }
    }

    fun isGoogleAccount(): Boolean {
        val currentUser = authRepository.currentUser ?: return false
        return currentUser.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }
    }

    fun deleteAccount(
        password: String,
        googleIdToken: String? = null,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        val currentUser = authRepository.currentUser
        if (currentUser == null) {
            onFailure("로그인한 사용자가 없습니다.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val googleProviderId = GoogleAuthProvider.PROVIDER_ID
                val isGoogleUser = currentUser.providerData.any { it.providerId == googleProviderId }

                if (isGoogleUser) {
                    if (googleIdToken.isNullOrBlank()) {
                        throw IllegalArgumentException("구글 계정 재인증 정보가 없습니다.")
                    }
                    authRepository.reauthenticateWithGoogle(googleIdToken).getOrThrow()

                } else {
                    if (password.isBlank()) {
                        throw IllegalArgumentException("비밀번호를 입력해주세요.")
                    }
                    authRepository.reauthenticateWithPassword(password).getOrThrow()
                }

                userRepository.deleteCurrentUserData(currentUser.uid).getOrThrow()

                authRepository.deleteCurrentAccount(context).getOrThrow()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigationTarget = NavigationTarget.Login
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                val message = e.message ?: "계정 삭제 중 오류가 발생했습니다."
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                onFailure(message)
            }
        }
    }

    fun generateOutfitAiMessage() {
        val currentState = _uiState.value
        val temperature = currentState.temperatureText.filter { it.isDigit() || it == '-' }.toIntOrNull()

        if (currentState.breed.isBlank() || currentState.outfit.isBlank() || temperature == null) {
            _uiState.update { it.copy(errorMessage = "코디 설명을 생성할 정보가 부족합니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isOutfitAiLoading = true,
                    outfitAiMessage = "",
                    errorMessage = null
                )
            }

            aiRepository.generateOutfitMessage(
                breed = currentState.breed,
                region = currentState.locationText,
                temperature = temperature,
                weatherStatus = currentState.weatherStatusText,
                outfit = currentState.outfit
            )
                .onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            isOutfitAiLoading = false,
                            outfitAiMessage = message
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isOutfitAiLoading = false,
                            errorMessage = e.message ?: "코디 설명 생성에 실패했습니다."
                        )
                    }
                }
        }
    }

    fun generateTimeAiMessage() {
        val currentState = _uiState.value
        val temperature = currentState.temperatureText.filter { it.isDigit() || it == '-' }.toIntOrNull()

        if (currentState.breed.isBlank() || currentState.time.isBlank() || temperature == null) {
            _uiState.update { it.copy(errorMessage = "시간 설명을 생성할 정보가 부족합니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isTimeAiLoading = true,
                    timeAiMessage = "",
                    errorMessage = null
                )
            }

            aiRepository.generateTimeMessage(
                breed = currentState.breed,
                region = currentState.locationText,
                temperature = temperature,
                weatherStatus = currentState.weatherStatusText,
                time = currentState.time
            )
                .onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            isTimeAiLoading = false,
                            timeAiMessage = message
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isTimeAiLoading = false,
                            errorMessage = e.message ?: "시간 설명 생성에 실패했습니다."
                        )
                    }
                }
        }
    }

    fun applyWeatherStage(breed: String, temperature: Int) {
        val normalizedBreed = breed.trim()
        val stage = getWeatherStage(temperature)
        val resource = DogWeatherResources.getResource(normalizedBreed, stage)
        val descriptionRes = DogWeatherResources.getBreedDescriptionRes(normalizedBreed)

        _uiState.update {
            it.copy(
                dogImageRes = resource.imageRes,
                outfit = context.getString(resource.outfitRes),
                time = context.getString(resource.timeRes),
                dogDescription = context.getString(descriptionRes)
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    fun shouldRequestLocationPermission(): Boolean = !hasLocationPermission()
}

fun getWeatherStage(temperature: Int): Int {
    return when {
        temperature <= -6 -> 0
        temperature in -5..5 -> 1
        temperature in 6..15 -> 2
        temperature in 16..24 -> 3
        else -> 4
    }
}
