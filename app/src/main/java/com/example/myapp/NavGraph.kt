package com.example.myapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapp.login.LoginScreen
import com.example.myapp.signup.SignupScreen
import com.example.myapp.choice.ChoiceScreen
import com.example.myapp.home.screen.HomeScreen

sealed class NavigationTarget {
    data object None : NavigationTarget()
    data object Login : NavigationTarget()
    data object Signup : NavigationTarget()
    data object Choice : NavigationTarget()
    data object Main : NavigationTarget()
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
                }
            )
        }
    }
}
