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
import tn.esprit.dam_android.screens.settings.PrivacyPolicyScreen
import tn.esprit.dam_android.screens.settings.TermsScreen
import tn.esprit.dam_android.screens.profile.UpdateProfileScreen

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
            // Get device identifier (Android ID)
            val deviceIdentifier = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            ) ?: ""

            val localDeviceIdentifier = tn.esprit.dam_android.utils.SharedPrefs.getDeviceIdentifier(context)
            
            // Use check endpoint for efficient device registration verification
            if (deviceIdentifier.isNotEmpty()) {
                val checkResult = authRepository.checkDeviceRegistration(
                    deviceIdentifier = deviceIdentifier,
                    platform = "android"
                )
                startDestination = when (checkResult) {
                    is tn.esprit.dam_android.models.auth.repositories.ApiResult.Success -> {
                        val checkResponse = checkResult.data
                        if (checkResponse.isRegistered && checkResponse.device != null) {
                            // Device is registered - save identifier and go to home
                            val returnedDeviceIdentifier = checkResponse.device.deviceIdentifier ?: deviceIdentifier
                            tn.esprit.dam_android.utils.SharedPrefs.saveDeviceIdentifier(context, returnedDeviceIdentifier)
                            Screen.Home.route
                        } else {
                            // Device not registered - go to registration screen
                            Screen.DeviceRegistration.route
                        }
                    }
                    is tn.esprit.dam_android.models.auth.repositories.ApiResult.Error -> {
                        // If 401 (Unauthorized), token is invalid - redirect to login
                        if (checkResult.code == 401) {
                            authRepository.logout()
                            Screen.Login.route
                        } else {
                            if (!localDeviceIdentifier.isNullOrEmpty()) {
                                Screen.Home.route
                            } else {
                                Screen.DeviceRegistration.route
                            }
                        }
                    }
                    else -> {

                        if (!localDeviceIdentifier.isNullOrEmpty()) {
                            Screen.Home.route
                        } else {
                            Screen.DeviceRegistration.route
                        }
                    }
                }
            } else {
                if (!localDeviceIdentifier.isNullOrEmpty()) {
                    startDestination = Screen.Home.route
                } else {
                    startDestination = Screen.DeviceRegistration.route
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
            LoginScreen(
                onLoginSuccess = {
                    // Always navigate to Home after successful login
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
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
                        tn.esprit.dam_android.utils.SharedPrefs.clearDeviceInfo(context) // Clear all device info
                    }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.UpdateProfile.route) {
            UpdateProfileScreen(
                onBack = { navController.popBackStack() },
                onUpdateSuccess = {
                    // Profile updated successfully, already navigated back
                }
            )
        }

        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TermsOfService.route) {
            TermsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}