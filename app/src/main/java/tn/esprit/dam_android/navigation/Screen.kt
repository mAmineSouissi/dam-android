package tn.esprit.dam_android.navigation

sealed class Screen(val route: String) {
    // Auth Screens
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object OtpVerification : Screen("otp_verification")
    object ResetPassword : Screen("reset_password")
    object DeviceRegistration : Screen("device_registration")

    // Main Screens
    object Home : Screen("home")
    object Scans : Screen("scans")
    object Apps : Screen("apps")
    object Alerts : Screen("alerts")
    object Settings : Screen("settings")
}

val screensWithBottomNav = listOf(
    Screen.Home.route,
    Screen.Scans.route,
    Screen.Apps.route,
    Screen.Alerts.route,
    Screen.Settings.route
)
