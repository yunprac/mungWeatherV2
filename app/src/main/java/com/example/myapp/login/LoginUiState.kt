package com.example.myapp.login

import com.example.myapp.NavigationTarget

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val firstLogin: Boolean = false,
    val navigationTarget: NavigationTarget = NavigationTarget.None
)