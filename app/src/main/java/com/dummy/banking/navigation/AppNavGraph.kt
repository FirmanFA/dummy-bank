package com.dummy.banking.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dummy.banking.ui.history.HistoryScreen
import com.dummy.banking.ui.home.HomeScreen
import com.dummy.banking.ui.login.LoginScreen
import com.dummy.banking.ui.transfer.TransferScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Transfer : Screen("transfer")
    object History : Screen("history")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTransfer = { navController.navigate(Screen.Transfer.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Transfer.route) {
            TransferScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
