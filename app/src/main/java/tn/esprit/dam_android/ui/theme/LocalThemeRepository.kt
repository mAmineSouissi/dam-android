// ui/theme/LocalThemeRepository.kt
package tn.esprit.dam_android.ui.theme

import androidx.compose.runtime.compositionLocalOf

val LocalThemeRepository = compositionLocalOf<ThemeRepository> { error("No ThemeRepository provided") }