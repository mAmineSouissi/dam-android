package tn.esprit.dam_android.screens.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import tn.esprit.dam_android.ui.components.*
import tn.esprit.dam_android.ui.theme.*

data class AlertDetail(
    val id: String,
    val title: String,
    val description: String,
    val appName: String,
    val timeAgo: String,
    val severity: BadgeSeverity,
    val icon: ImageVector,
    val borderColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(navController: NavController) {
    val alerts = remember {
        listOf(
            AlertDetail(
                id = "1",
                title = "Excessive location tracking",
                description = "Instagram accessed your location 47 times in the background today",
                appName = "Instagram",
                timeAgo = "2h ago",
                severity = BadgeSeverity.ERROR,
                icon = Icons.Outlined.LocationOn,
                borderColor = DarkColors.Error
            ),
            AlertDetail(
                id = "2",
                title = "Camera accessed while inactive",
                description = "TikTok accessed camera 3 times while app was in background",
                appName = "TikTok",
                timeAgo = "5h ago",
                severity = BadgeSeverity.WARNING,
                icon = Icons.Outlined.Videocam,
                borderColor = DarkColors.Warning
            ),
            AlertDetail(
                id = "3",
                title = "Contacts accessed",
                description = "Facebook read your contact list",
                appName = "Facebook",
                timeAgo = "1d ago",
                severity = BadgeSeverity.INFO,
                icon = Icons.Outlined.People,
                borderColor = DarkColors.Info
            )
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Alerts",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = { /* TODO: Navigate */ }) {
                        Text(
                            text = "Alerts",
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
            }

            items(alerts) { alert ->
                AlertCard(alert = alert)
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.base))
            }
        }
    }
}

@Composable
private fun AlertCard(alert: AlertDetail) {
    SGCard {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left border indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(100.dp)
                    .background(
                        color = alert.borderColor,
                        shape = MaterialTheme.shapes.small
                    )
            )

            Spacer(modifier = Modifier.width(Spacing.base))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = alert.icon,
                                contentDescription = alert.title,
                                tint = alert.borderColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(Spacing.md))

                        Column {
                            Text(
                                text = alert.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    SGBadge(
                        text = when (alert.severity) {
                            BadgeSeverity.ERROR -> "critical"
                            BadgeSeverity.WARNING -> "important"
                            BadgeSeverity.INFO -> "info"
                            else -> "success"
                        },
                        severity = alert.severity
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                Text(
                    text = alert.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                Text(
                    text = "${alert.appName} • ${alert.timeAgo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    TextButton(
                        onClick = { /* TODO: View details */ },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = DarkColors.Success
                        )
                    ) {
                        Text(
                            text = "View Details",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    TextButton(
                        onClick = { /* TODO: Dismiss alert */ },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    ) {
                        Text(
                            text = "Dismiss",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Alerts - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AlertsScreenLightPreview() {
    ShadowGuardTheme {
        val navController = rememberNavController()
        AlertsScreen(navController = navController)
    }
}
