// ui/theme/Theme.kt
package tn.esprit.dam_android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlinx.coroutines.flow.first

private val DarkColorScheme = darkColorScheme(
    primary = DarkColors.Primary,
    onPrimary = DarkColors.PrimaryForeground,
    secondary = DarkColors.Secondary,
    onSecondary = DarkColors.SecondaryForeground,
    background = DarkColors.Background,
    onBackground = DarkColors.SecondaryForeground,
    surface = DarkColors.Surface,
    onSurface = DarkColors.SecondaryForeground,
    error = DarkColors.Error,
    outline = DarkColors.Border
)

private val LightColorScheme = lightColorScheme(
    primary = LightColors.Primary,
    onPrimary = LightColors.PrimaryForeground,
    secondary = LightColors.Secondary,
    onSecondary = LightColors.SecondaryForeground,
    background = LightColors.Background,
    onBackground = LightColors.SecondaryForeground,
    surface = LightColors.Surface,
    onSurface = LightColors.SecondaryForeground,
    error = LightColors.Error,
    outline = LightColors.Border
)

@Composable
fun ShadowGuardTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { ThemeRepository(context) }

    // THIS LINE IS THE FINAL FIX
    val isDark by repo.isDarkFlow.collectAsState(initial = false)

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ShadowGuardTypography,
        content = {
            CompositionLocalProvider(LocalThemeRepository provides repo) {
                content()
            }
        }
    )
}