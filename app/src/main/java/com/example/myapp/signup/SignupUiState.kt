package com.example.myapp.signup

import com.example.myapp.NavigationTarget

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