package com.yoon.weatherapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.yoon.weatherapp.login.LoginScreen
import com.yoon.weatherapp.signup.SignupScreen
import com.yoon.weatherapp.choice.ChoiceScreen
import com.yoon.weatherapp.home.screen.EditProfileScreen
import com.yoon.weatherapp.home.screen.HomeScreen
import com.yoon.weatherapp.home.screen.Policy

sealed class NavigationTarget {
    data object None : NavigationTarget()
    data object Login : NavigationTarget()
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToChoice = {
                    navController.navigate("choice") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignupScreen(onNavigateToLogin = { navController.popBackStack() })
        }

        composable("choice") {
            ChoiceScreen(
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("choice") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive= true }
                    }
                },
                onNavigateToEditProfile = {
                    navController.navigate("editProfile")
                },
                onNavigateToPolicy = {
                    navController.navigate("policy")
                }
            )
        }

        composable("policy") {
            Policy(onCloseClick = { navController.popBackStack() })
        }

        composable("editProfile") {
            EditProfileScreen(
                onCancelClick = { navController.popBackStack() },
                onCompleteClick = { navController.popBackStack() }
            )
        }
    }
}
