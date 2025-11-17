package tn.esprit.dam_android.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import tn.esprit.dam_android.ui.theme.LocalThemeRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    val repo = LocalThemeRepository.current
    val isDark by repo.isDarkFlow.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    TopAppBar(
        title = { Text("Home") },
        actions = {
            IconButton(onClick = {
                scope.launch {
                    repo.saveTheme(!isDark)
                }
            }) {
                Icon(
                    imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Toggle theme",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}