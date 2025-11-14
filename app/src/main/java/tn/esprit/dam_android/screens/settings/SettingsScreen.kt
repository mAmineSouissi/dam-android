package tn.esprit.dam_android.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import tn.esprit.dam_android.navigation.Screen
import tn.esprit.dam_android.ui.components.*
import tn.esprit.dam_android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onLogout: () -> Unit
) {
    var realtimeMonitoring by remember { mutableStateOf(true) }
    var backgroundScanning by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = { /* TODO: Navigate */ }) {
                        Text(
                            text = "Settings",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.base),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
                SectionHeader(title = "MONITORING")
            }

            item {
                SGCard {
                    Column {
                        NavigationSettingItem(
                            title = "Change Password",
                            onClick = {
                                // TODO: Add Change Password screen
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.xs),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        NavigationSettingItem(
                            title = "Device Management",
                            onClick = {
                                navController.navigate(Screen.DeviceRegistration.route)
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.xs),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        NavigationSettingItem(
                            title = "Export My Data",
                            onClick = {
                                // TODO: Add Export screen
                            }
                        )
                    }
                }
            }

            item {
                SectionHeader(title = "ACCOUNT")
            }

            item {
                SGCard {
                    Column {
                        NavigationSettingItem(
                            title = "Change Password",
                            onClick = {
                                navController.navigate(Screen.DeviceRegistration.route)
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.xs),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        NavigationSettingItem(
                            title = "Device Management",
                            onClick = { /* TODO: Navigate to device management */ }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.xs),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        NavigationSettingItem(
                            title = "Export My Data",
                            onClick = { /* TODO: Navigate to export data */ }
                        )
                    }
                }
            }

            item {
                SectionHeader(title = "ABOUT")
            }

            item {
                SGCard {
                    Column {
                        NavigationSettingItem(
                            title = "Privacy Policy",
                            onClick = { /* TODO: Navigate to privacy policy */ }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.xs),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        NavigationSettingItem(
                            title = "Terms of Service",
                            onClick = { /* TODO: Navigate to terms */ }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.xs),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        VersionSettingItem(version = "1.0.0")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
                SGButton(
                    text = "Sign Out",
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.SECONDARY
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.base))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        modifier = Modifier.padding(start = Spacing.xs)
    )
}

@Composable
private fun ToggleSettingItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                uncheckedTrackColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Composable
private fun NavigationSettingItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        IconButton(
            onClick = onClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun VersionSettingItem(version: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Version",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = version,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Preview(
    name = "Settings - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SettingsScreenLightPreview() {
    ShadowGuardTheme {
        val navController = rememberNavController()
        SettingsScreen(
            navController = navController,
            onLogout = {

            }
        )
    }
}
