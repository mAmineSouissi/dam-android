// SetupNavGraph.kt
package tn.esprit.dam_android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import tn.esprit.dam_android.api.local.TokenManager
import tn.esprit.dam_android.models.auth.repositories.AuthRepository
import tn.esprit.dam_android.screens.login.LoginScreen
import tn.esprit.dam_android.screens.signup.SignUpScreen
import tn.esprit.dam_android.screens.home.HomeScreen
import tn.esprit.dam_android.screens.scans.ScansScreen
import tn.esprit.dam_android.screens.apps.AppScreen
import tn.esprit.dam_android.screens.alerts.AlertsScreen
import tn.esprit.dam_android.screens.login.ForgotPasswordScreen
import tn.esprit.dam_android.screens.login.OtpScreen
import tn.esprit.dam_android.screens.login.ResetPasswordScreen
import tn.esprit.dam_android.screens.settings.DeviceRegistrationScreen
import tn.esprit.dam_android.screens.settings.SettingsScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val authRepository = remember { AuthRepository(tokenManager) }

    // Auto-login + device check
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = tokenManager.getTokenSync()
        if (token.isNullOrEmpty()) {
            startDestination = Screen.Login.route
        } else {
            // First check local SharedPrefs for quick fallback
            val localDeviceId = tn.esprit.dam_android.utils.SharedPrefs.getDeviceId(context)
            
            // Check device registration status from backend
            val statusResult = authRepository.getDeviceStatus()
            startDestination = when (statusResult) {
                is tn.esprit.dam_android.models.auth.repositories.ApiResult.Success -> {
                    val status = statusResult.data
                    // If we have devices in the list, use the first one (even if isDeviceRegistered is false)
                    // This handles backend inconsistencies where devices exist but flag is false
                    if (status.devices.isNotEmpty()) {
                        val deviceId = status.devices.first()._id
                        tn.esprit.dam_android.utils.SharedPrefs.saveDeviceId(context, deviceId)
                        Screen.Home.route
                    } else if (status.isDeviceRegistered) {
                        // If flag is true but no devices (shouldn't happen, but handle it)
                        Screen.DeviceRegistration.route
                    } else {
                        // No devices and not registered - check local fallback
                        if (!localDeviceId.isNullOrEmpty()) {
                            Screen.Home.route
                        } else {
                            Screen.DeviceRegistration.route
                        }
                    }
                }
                is tn.esprit.dam_android.models.auth.repositories.ApiResult.Error -> {
                    // If 401 (Unauthorized), token is invalid - redirect to login
                    if (statusResult.code == 401) {
                        // Clear invalid token
                        authRepository.logout()
                        Screen.Login.route
                    } else {
                        // Other errors - use local check as fallback
                        if (!localDeviceId.isNullOrEmpty()) {
                            Screen.Home.route
                        } else {
                            Screen.DeviceRegistration.route
                        }
                    }
                }
                else -> {
                    // API failed - use local check as fallback
                    if (!localDeviceId.isNullOrEmpty()) {
                        Screen.Home.route
                    } else {
                        Screen.DeviceRegistration.route
                    }
                }
            }
        }
    }

    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!
    ) {
        // AUTH FLOW
        composable(Screen.Login.route) {
            val scope = rememberCoroutineScope()
            LoginScreen(
                onLoginSuccess = {
                    scope.launch {
                        // Check device status after login
                        val statusResult = authRepository.getDeviceStatus()
                        val destination = when (statusResult) {
                            is tn.esprit.dam_android.models.auth.repositories.ApiResult.Success -> {
                                val status = statusResult.data
                                // If we have devices, use them (even if isDeviceRegistered is false)
                                if (status.devices.isNotEmpty()) {
                                    val deviceId = status.devices.first()._id
                                    tn.esprit.dam_android.utils.SharedPrefs.saveDeviceId(context, deviceId)
                                    Screen.Home.route
                                } else {
                                    Screen.DeviceRegistration.route
                                }
                            }
                            is tn.esprit.dam_android.models.auth.repositories.ApiResult.Error -> {
                                // If still 401 after login, something is wrong - go to registration
                                if (statusResult.code == 401) {
                                    Screen.DeviceRegistration.route
                                } else {
                                    // Other errors - fallback to local check
                                    val deviceId = tn.esprit.dam_android.utils.SharedPrefs.getDeviceId(context)
                                    if (deviceId.isNullOrEmpty()) {
                                        Screen.DeviceRegistration.route
                                    } else {
                                        Screen.Home.route
                                    }
                                }
                            }
                            else -> {
                                // Fallback to local check
                                val deviceId = tn.esprit.dam_android.utils.SharedPrefs.getDeviceId(context)
                                if (deviceId.isNullOrEmpty()) {
                                    Screen.DeviceRegistration.route
                                } else {
                                    Screen.Home.route
                                }
                            }
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                authRepository = authRepository
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
                authRepository = authRepository
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onSendSuccess = { email ->
                    navController.navigate("${Screen.OtpVerification.route}?email=$email")
                },
                authRepository = authRepository
            )
        }

        composable(
            route = "${Screen.OtpVerification.route}?email={email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
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

        // DEVICE REGISTRATION (NEW)
        composable(Screen.DeviceRegistration.route) {
            DeviceRegistrationScreen(
                onBack = {
                    // If we can pop, do it. Otherwise navigate to Login (no back stack)
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.DeviceRegistration.route) { inclusive = true }
                    }
                }
            )
        }

        // MAIN SCREENS
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Scans.route) { ScansScreen(navController = navController) }
        composable(Screen.Apps.route) { AppScreen() } // Updated
        composable(Screen.Alerts.route) { AlertsScreen(navController = navController) }
        composable(Screen.Settings.route) {
            val scope = rememberCoroutineScope()
            SettingsScreen(
                navController = navController,
                onLogout = {
                    scope.launch {
                        authRepository.logout()
                        tn.esprit.dam_android.utils.SharedPrefs.saveDeviceId(context, "") // Clear device ID
                    }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}