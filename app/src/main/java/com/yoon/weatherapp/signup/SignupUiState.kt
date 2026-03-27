package com.yoon.weatherapp.signup

import com.yoon.weatherapp.NavigationTarget

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val passwordVisible: Boolean = false,
    val passwordConfirmationVisible: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigationTarget: NavigationTarget = NavigationTarget.None
)