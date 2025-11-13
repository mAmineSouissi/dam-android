package tn.esprit.dam_android.screens.apps

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import tn.esprit.dam_android.ui.theme.ShadowGuardTheme
import tn.esprit.dam_android.ui.theme.Spacing

@Composable
fun AppsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.base),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📱",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(Spacing.base))
        Text(
            text = "Apps Screen",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = "Installed apps list coming soon...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Preview(
    name = "Apps - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AppsScreenLightPreview() {
    ShadowGuardTheme {
        val navController = rememberNavController()
        AppsScreen(navController = navController)
    }
}

@Preview(
    name = "Apps - Dark",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AppsScreenDarkPreview() {
    ShadowGuardTheme {
        val navController = rememberNavController()
        AppsScreen(navController = navController)
    }
}