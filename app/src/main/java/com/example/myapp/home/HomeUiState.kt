package com.example.myapp.home

import com.example.myapp.NavigationTarget

data class HomeUiState(

    // Profile State
    val email: String = "",
    val name: String = "",
    val breed: String = "",
    val imageUrl: String? = null,
    val isDefaultImage: Boolean = true,

    // Home State
    val temperatureText: String = "-- C",
    val locationText: String = "",
    val weatherStatusText: String = "",
    val dogImageRes: Int? = null,
    val dogDescription: String = "",

    // AI State
    val outfit: String = "",
    val time: String = "",
    val outfitAiMessage: String = "",
    val timeAiMessage: String = "",

    val isOutfitAiLoading: Boolean = false,
    val isTimeAiLoading: Boolean = false,


    val currentPage: Int = 1,
    val isProfilePanelOpen: Boolean = false,
    val isDogDescriptionSheetOpen: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigationTarget: NavigationTarget = NavigationTarget.None
)
