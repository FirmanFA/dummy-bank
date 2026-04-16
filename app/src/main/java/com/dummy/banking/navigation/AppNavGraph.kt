package com.dummy.banking.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dummy.banking.ui.history.HistoryScreen
import com.dummy.banking.ui.home.HomeScreen
import com.dummy.banking.ui.login.LoginScreen
import com.dummy.banking.ui.transfer.TransferScreen
import com.dummy.banking.ui.transfer.TransferSuccessScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Transfer : Screen("transfer")
    object TransferSuccess : Screen("transfer_success/{amount}/{recipientName}/{recipientAccount}") {
        fun createRoute(amount: Long, recipientName: String, recipientAccount: String) =
            "transfer_success/$amount/$recipientName/$recipientAccount"
    }
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
                onBack = { navController.popBackStack() },
                onTransferSuccess = { amount, name, account ->
                    navController.navigate(Screen.TransferSuccess.createRoute(amount, name, account)) {
                        popUpTo(Screen.Transfer.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.TransferSuccess.route,
            arguments = listOf(
                navArgument("amount") { type = NavType.LongType },
                navArgument("recipientName") { type = NavType.StringType },
                navArgument("recipientAccount") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getLong("amount") ?: 0L
            val name = backStackEntry.arguments?.getString("recipientName") ?: ""
            val account = backStackEntry.arguments?.getString("recipientAccount") ?: ""
            
            TransferSuccessScreen(
                amount = amount,
                recipientName = name,
                recipientAccount = account,
                onDone = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
