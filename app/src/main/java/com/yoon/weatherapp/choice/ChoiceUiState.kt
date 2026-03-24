package com.yoon.weatherapp.choice

import android.net.Uri
import com.yoon.weatherapp.NavigationTarget

enum class ProfileImageType {
    NONE, ALBUM, BASIC
}

data class ChoiceUiState(
    val name: String = "",
    val breed: String = "",
    val imageUri: Uri? = null,
    val profileImageType: ProfileImageType = ProfileImageType.NONE,
    val showProfileDialog: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigationTarget: NavigationTarget = NavigationTarget.None
)