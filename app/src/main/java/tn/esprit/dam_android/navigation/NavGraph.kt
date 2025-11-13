// SetupNavGraph.kt
package tn.esprit.dam_android.navigation

import android.R.attr.type
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import tn.esprit.dam_android.api.local.TokenManager
import tn.esprit.dam_android.models.auth.repositories.AuthRepository
import tn.esprit.dam_android.screens.login.LoginScreen
import tn.esprit.dam_android.screens.signup.SignUpScreen
import tn.esprit.dam_android.screens.home.HomeScreen
import tn.esprit.dam_android.screens.scans.ScansScreen
import tn.esprit.dam_android.screens.apps.AppsScreen
import tn.esprit.dam_android.screens.alerts.AlertsScreen
import tn.esprit.dam_android.screens.login.ForgotPasswordScreen
import tn.esprit.dam_android.screens.login.OtpScreen
import tn.esprit.dam_android.screens.login.ResetPasswordScreen
import tn.esprit.dam_android.screens.settings.SettingsScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val authRepository = remember { AuthRepository(tokenManager) }

    // Auto-login: Check token on app start
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = tokenManager.getTokenSync()
        startDestination = if (!token.isNullOrEmpty()) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }
    }

    if (startDestination == null) {
        // Show splash or loading
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!
    ) {
        // LOGIN
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                authRepository = authRepository
            )
        }

        // SIGN-UP
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    // Go back to Login after registration
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
                authRepository = authRepository
            )
        }

        // FORGOT PASSWORD
        composable(Screen.ForgotPassword.route) {
            val context = LocalContext.current
            val tokenManager = remember { TokenManager(context) }
            val authRepository = remember { AuthRepository(tokenManager) }

            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onSendSuccess = { email ->
                    navController.navigate("${Screen.OtpVerification.route}?email=$email")
                },
                authRepository = authRepository
            )
        }

        // OTP VERIFICATION
        composable(
            route = "${Screen.OtpVerification.route}?email={email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val context = LocalContext.current
            val tokenManager = remember { TokenManager(context) }
            val authRepository = remember { AuthRepository(tokenManager) }

            OtpScreen(
                email = email,
                onBack = { navController.popBackStack() },
                onVerifySuccess = { resetToken ->
                    navController.navigate("${Screen.ResetPassword.route}?email=$email&resetToken=$resetToken")
                },
                authRepository = authRepository
            )
        }

        composable(
            route = "${Screen.ResetPassword.route}?email={email}&resetToken={resetToken}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("resetToken") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val resetToken = backStackEntry.arguments?.getString("resetToken") ?: ""
            val context = LocalContext.current
            val tokenManager = remember { TokenManager(context) }
            val authRepository = remember { AuthRepository(tokenManager) }

            ResetPasswordScreen(
                email = email,
                resetToken = resetToken,
                onBack = { navController.popBackStack() },
                onResetSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                authRepository = authRepository
            )
        }

        // MAIN SCREENS
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Scans.route) { ScansScreen(navController = navController) }
        composable(Screen.Apps.route) { AppsScreen(navController = navController) }
        composable(Screen.Alerts.route) { AlertsScreen(navController = navController) }
        composable(Screen.Settings.route) {
            val scope = rememberCoroutineScope()
            SettingsScreen(
                navController = navController,
                onLogout = {
                    scope.launch {
                        authRepository.logout()
                    }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}