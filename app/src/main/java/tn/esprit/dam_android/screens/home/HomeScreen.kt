package tn.esprit.dam_android.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import tn.esprit.dam_android.ui.components.*
import tn.esprit.dam_android.ui.theme.*

data class PrivacyAlert(
    val id: String,
    val title: String,
    val appName: String,
    val timeAgo: String,
    val severity: BadgeSeverity,
    val icon: ImageVector
)

@Composable
fun HomeScreen(navController: NavController) {

    val sampleAlerts = remember {
        listOf(
            PrivacyAlert(
                id = "1",
                title = "Excessive location tracking",
                appName = "Instagram",
                timeAgo = "2h ago",
                severity = BadgeSeverity.ERROR,
                icon = Icons.Outlined.LocationOn
            ),
            PrivacyAlert(
                id = "2",
                title = "Camera accessed while inactive",
                appName = "TikTok",
                timeAgo = "5h ago",
                severity = BadgeSeverity.WARNING,
                icon = Icons.Outlined.Videocam
            ),
            PrivacyAlert(
                id = "3",
                title = "Contacts accessed",
                appName = "Facebook",
                timeAgo = "1d ago",
                severity = BadgeSeverity.INFO,
                icon = Icons.Outlined.People
            )
        )
    }
    ShadowGuardTheme{

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopBar()
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
                WelcomeSection()
            }

            item {
                PrivacyScoreCard()
            }

            item {
                StatsGrid()
            }

            item {
                RecentAlertsSection(
                    alerts = sampleAlerts,
                    onViewAllClick = {
                        // Navigate to alerts screen
                        navController.navigate("alerts")
                    }
                )
            }

            item {
                ActionButtons(
                    onRunScanClick = {
                        // Navigate to scans screen or trigger scan
                        navController.navigate("scans")
                    },
                    onViewAppsClick = {
                        // Navigate to apps screen
                        navController.navigate("apps")
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.base))
            }
        }
    }
    }
}


@Composable
private fun WelcomeSection() {
    Column {
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Your privacy dashboard",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun PrivacyScoreCard() {
    SGCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Progress Score
            CircularPrivacyScore(score = 65)

            Spacer(modifier = Modifier.width(Spacing.base))

            // Score Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Privacy Score",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Last scanned 2h ago",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                SGBadge(
                    text = "Needs Attention",
                    severity = BadgeSeverity.WARNING
                )
            }
        }
    }
}

@Composable
private fun CircularPrivacyScore(score: Int) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "score"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(100.dp)
    ) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val strokeWidth = 12.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            // Background arc
            drawArc(
                color = Color(0xFF3F3F3F),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2)
            )

            // Progress arc
            val sweepAngle = (animatedScore / 100f) * 360f
            drawArc(
                color = Color(0xFF10B981),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2)
            )
        }

        Text(
            text = animatedScore.toInt().toString(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun StatsGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.PhoneAndroid,
            value = "47",
            label = "Apps Scanned",
            iconTint = DarkColors.Success
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Warning,
            value = "8",
            label = "Risky Apps",
            iconTint = DarkColors.Warning
        )
    }
    Spacer(modifier = Modifier.height(Spacing.md))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Notifications,
            value = "12",
            label = "Alerts",
            iconTint = DarkColors.Error
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.BarChart,
            value = "2.4GB",
            label = "Data Tracked",
            iconTint = DarkColors.Info
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    SGCard(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
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
private fun RecentAlertsSection(
    alerts: List<PrivacyAlert>,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Alerts",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            alerts.forEach { alert ->
                AlertItem(alert = alert)
            }
        }
    }
}

@Composable
private fun AlertItem(alert: PrivacyAlert) {
    SGCard {
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
                Icon(
                    imageVector = alert.icon,
                    contentDescription = alert.title,
                    tint = when (alert.severity) {
                        BadgeSeverity.ERROR -> DarkColors.Error
                        BadgeSeverity.WARNING -> DarkColors.Warning
                        BadgeSeverity.INFO -> DarkColors.Info
                        else -> DarkColors.Success
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.base))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "${alert.appName} • ${alert.timeAgo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
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
    }
}

@Composable
private fun ActionButtons(
    onRunScanClick: () -> Unit,
    onViewAppsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        SGButton(
            text = "Run Scan",
            onClick = onRunScanClick,
            modifier = Modifier.weight(1f),
            variant = ButtonVariant.PRIMARY,
            icon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        SGButton(
            text = "View Apps",
            onClick = onViewAppsClick,
            modifier = Modifier.weight(1f),
            variant = ButtonVariant.SECONDARY,
            icon = {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
    }
}

@Preview(name = "Home - Light", showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenLightPreview() {
        ShadowGuardTheme {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }
}
