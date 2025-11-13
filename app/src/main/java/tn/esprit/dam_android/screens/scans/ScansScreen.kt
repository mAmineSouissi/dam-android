package tn.esprit.dam_android.screens.scans

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import tn.esprit.dam_android.ui.components.*
import tn.esprit.dam_android.ui.theme.*

data class ScannedApp(
    val id: String,
    val name: String,
    val developer: String,
    val icon: String,
    val riskScore: Int,
    val permissionsCount: Int,
    val isRisky: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScansScreen(navController: NavController) {
    val riskyApps = remember {
        listOf(
            ScannedApp(
                id = "1",
                name = "Instagram",
                developer = "Meta",
                icon = "📷",
                riskScore = 72,
                permissionsCount = 4,
                isRisky = true
            ),
            ScannedApp(
                id = "2",
                name = "TikTok",
                developer = "ByteDance",
                icon = "🎵",
                riskScore = 85,
                permissionsCount = 5,
                isRisky = true
            )
        )
    }

    val safeApps = remember {
        listOf(
            ScannedApp(
                id = "3",
                name = "WhatsApp",
                developer = "Meta",
                icon = "💬",
                riskScore = 45,
                permissionsCount = 3,
                isRisky = false
            ),
            ScannedApp(
                id = "4",
                name = "Signal",
                developer = "Signal Foundation",
                icon = "🔒",
                riskScore = 15,
                permissionsCount = 2,
                isRisky = false
            )
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Security Scan",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = { /* TODO: Navigate */ }) {
                        Text(
                            text = "Scans",
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
                Text(
                    text = "Last scanned: 2 hours ago",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            item {
                ScanSummaryCard(
                    totalApps = 4,
                    riskyApps = riskyApps.size,
                    safeApps = safeApps.size
                )
            }

            item {
                SectionHeader(
                    icon = "⚠️",
                    title = "Risky Apps (${riskyApps.size})"
                )
            }

            items(riskyApps) { app ->
                AppItem(
                    app = app,
                    onClick = { /* TODO: Navigate to app details */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.base))
                SectionHeader(
                    icon = "✅",
                    title = "Safe Apps (${safeApps.size})"
                )
            }

            items(safeApps) { app ->
                AppItem(
                    app = app,
                    onClick = { /* TODO: Navigate to app details */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.base))
            }
        }
    }
}

@Composable
private fun ScanSummaryCard(
    totalApps: Int,
    riskyApps: Int,
    safeApps: Int
) {
    SGCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScanStat(
                value = totalApps.toString(),
                label = "Total Apps",
                color = MaterialTheme.colorScheme.onBackground
            )
            ScanStat(
                value = riskyApps.toString(),
                label = "Risky Apps",
                color = DarkColors.Error
            )
            ScanStat(
                value = safeApps.toString(),
                label = "Safe Apps",
                color = DarkColors.Success
            )
        }
    }
}

@Composable
private fun ScanStat(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SectionHeader(icon: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun AppItem(
    app: ScannedApp,
    onClick: () -> Unit
) {
    SGCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.icon,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.width(Spacing.base))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = app.developer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = app.riskScore.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (app.isRisky) DarkColors.Error else DarkColors.Success
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "${app.permissionsCount} perms",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (app.isRisky) DarkColors.Error else DarkColors.Success
                )
            }

            Spacer(modifier = Modifier.width(Spacing.sm))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}

@Preview(
    name = "Scans - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ScansScreenLightPreview() {
    ShadowGuardTheme {
        val navController = rememberNavController()
        ScansScreen(navController = navController)
    }
}

@Preview(
    name = "Scans - Dark",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ScansScreenDarkPreview() {
    ShadowGuardTheme {
        val navController = rememberNavController()
        ScansScreen(navController = navController)
    }
}