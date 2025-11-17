package tn.esprit.dam_android.screens.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tn.esprit.dam_android.api.local.RetrofitClient
import tn.esprit.dam_android.api.local.TokenManager
import tn.esprit.dam_android.models.scan.AppInfo
import tn.esprit.dam_android.ui.components.SGCard
import tn.esprit.dam_android.ui.theme.Spacing
import tn.esprit.dam_android.utils.SharedPrefs
import java.util.*

data class ScannedApp(
    val packageName: String,
    val appName: String,
    val riskLevel: RiskLevel,
    val issues: List<String> = emptyList()
)

enum class RiskLevel {
    SAFE, SUSPICIOUS, MALICIOUS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val tokenManager = remember { TokenManager(context) }

    var isScanning by rememberSaveable { mutableStateOf(false) }
    var scanProgress by rememberSaveable { mutableStateOf(0f) }
    var scanResult by rememberSaveable { mutableStateOf<List<ScannedApp>?>(null) }
    var selectedApp by rememberSaveable { mutableStateOf<ScannedApp?>(null) }
    var scanId by rememberSaveable { mutableStateOf<String?>(null) }
    var threatsFound by rememberSaveable { mutableStateOf<Int?>(null) }
    var riskScore by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Installed Apps", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (scanResult == null) {
                FloatingActionButton(
                    onClick = {
                        isScanning = true
                        scanProgress = 0f
                        scanResult = null
                        scanId = null
                        threatsFound = null
                        riskScore = null

                        scope.launch {
                            val pm = context.packageManager
                            val installedApps = getInstalledApps(pm)
                            val total = installedApps.size
                            val appInfoList = mutableListOf<AppInfo>()

                            // Simulate local scan + build AppInfo list
                            installedApps.forEachIndexed { index, appInfo ->
                                delay(50)
                                scanProgress = (index + 1).toFloat() / total

                                val risk = simulateRiskAnalysis(appInfo.packageName)
                                val issues = generateIssues(risk, appInfo.packageName)

                                appInfoList.add(
                                    AppInfo(
                                        packageName = appInfo.packageName,
                                        versionCode = try {
                                            pm.getPackageInfo(appInfo.packageName, 0).longVersionCode
                                        } catch (e: Exception) { 0 },
                                        versionName = try {
                                            pm.getPackageInfo(appInfo.packageName, 0).versionName ?: ""
                                        } catch (e: Exception) { "" }
                                    )
                                )
                            }

                            // Upload scan
                            val token = tokenManager.getTokenSync() ?: ""
                            if (token.isNotEmpty()) {
                                // Use deviceIdentifier for scan endpoint (primary identifier)
                                val deviceIdentifier = SharedPrefs.getDeviceIdentifier(context)
                                
                                if (deviceIdentifier == null || deviceIdentifier.isEmpty()) {
                                    snackbarHostState.showSnackbar("Device not registered. Please register first.")
                                    isScanning = false
                                    return@launch
                                }
                                
                                try {
                                    val response = RetrofitClient.scanService.uploadScan(
                                        deviceId = deviceIdentifier,
                                        authorization = "Bearer $token",
                                        request = tn.esprit.dam_android.models.scan.ScanRequest(apps = appInfoList)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val result = response.body()!!
                                        scanId = result.scanId
                                        threatsFound = result.threatsFound
                                        riskScore = result.riskScore

                                        // Generate local UI results (optional)
                                        scanResult = appInfoList.map { info ->
                                            val risk = when {
                                                info.packageName.contains("malware", true) -> RiskLevel.MALICIOUS
                                                info.packageName.contains("suspicious", true) -> RiskLevel.SUSPICIOUS
                                                Random().nextInt(20) == 0 -> RiskLevel.SUSPICIOUS
                                                else -> RiskLevel.SAFE
                                            }
                                            ScannedApp(
                                                packageName = info.packageName,
                                                appName = try { pm.getApplicationLabel(pm.getApplicationInfo(info.packageName, 0)).toString() } catch (e: Exception) { info.packageName },
                                                riskLevel = risk,
                                                issues = generateIssues(risk, info.packageName)
                                            )
                                        }

                                        snackbarHostState.showSnackbar("Scan uploaded: ${result.threatsFound} threats")
                                    } else {
                                        throw Exception("HTTP ${response.code()}")
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Upload failed, using local scan")
                                    // Fallback: use local simulation
                                    scanResult = appInfoList.map { info ->
                                        val risk = simulateRiskAnalysis(info.packageName)
                                        ScannedApp(
                                            packageName = info.packageName,
                                            appName = try { pm.getApplicationLabel(pm.getApplicationInfo(info.packageName, 0)).toString() } catch (e: Exception) { info.packageName },
                                            riskLevel = risk,
                                            issues = generateIssues(risk, info.packageName)
                                        )
                                    }
                                }
                            } else {
                                // No token → local only
                                scanResult = appInfoList.map { info ->
                                    val risk = simulateRiskAnalysis(info.packageName)
                                    ScannedApp(
                                        packageName = info.packageName,
                                        appName = try { pm.getApplicationLabel(pm.getApplicationInfo(info.packageName, 0)).toString() } catch (e: Exception) { info.packageName },
                                        riskLevel = risk,
                                        issues = generateIssues(risk, info.packageName)
                                    )
                                }
                                snackbarHostState.showSnackbar("Offline: local scan only")
                            }

                            isScanning = false
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Security, contentDescription = "Scan Now")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing.base)
        ) {
            if (isScanning) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { scanProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = "Scanning ${installedAppsCount(context)} apps... ${(scanProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else if (scanResult != null) {
                // Show scan summary if uploaded
                scanId?.let {
                    SGCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.sm)) {
                            Text("Scan ID: $it", style = MaterialTheme.typography.labelMedium)
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Threats: $threatsFound", color = MaterialTheme.colorScheme.error)
                                Text(
                                    "Risk Score: $riskScore/100",
                                    color = if (riskScore!! > 50) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                "Apps Scanned: ${scanResult?.size ?: 0}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.base))
                }
                
                // Rescan button
                Button(
                    onClick = {
                        isScanning = true
                        scanProgress = 0f
                        scanResult = null
                        scanId = null
                        threatsFound = null
                        riskScore = null

                        scope.launch {
                            val pm = context.packageManager
                            val installedApps = getInstalledApps(pm)
                            val total = installedApps.size
                            val appInfoList = mutableListOf<AppInfo>()

                            // Simulate local scan + build AppInfo list
                            installedApps.forEachIndexed { index, appInfo ->
                                delay(50)
                                scanProgress = (index + 1).toFloat() / total

                                val risk = simulateRiskAnalysis(appInfo.packageName)
                                val issues = generateIssues(risk, appInfo.packageName)

                                appInfoList.add(
                                    AppInfo(
                                        packageName = appInfo.packageName,
                                        versionCode = try {
                                            pm.getPackageInfo(appInfo.packageName, 0).longVersionCode
                                        } catch (e: Exception) { 0 },
                                        versionName = try {
                                            pm.getPackageInfo(appInfo.packageName, 0).versionName ?: ""
                                        } catch (e: Exception) { "" }
                                    )
                                )
                            }

                            // Upload scan
                            val token = tokenManager.getTokenSync() ?: ""
                            if (token.isNotEmpty()) {
                                // Use deviceIdentifier for scan endpoint (primary identifier)
                                val deviceIdentifier = SharedPrefs.getDeviceIdentifier(context)
                                
                                if (deviceIdentifier == null || deviceIdentifier.isEmpty()) {
                                    snackbarHostState.showSnackbar("Device not registered. Please register first.")
                                    isScanning = false
                                    return@launch
                                }
                                
                                try {
                                    val response = RetrofitClient.scanService.uploadScan(
                                        deviceId = deviceIdentifier,
                                        authorization = "Bearer $token",
                                        request = tn.esprit.dam_android.models.scan.ScanRequest(apps = appInfoList)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val result = response.body()!!
                                        scanId = result.scanId
                                        threatsFound = result.threatsFound
                                        riskScore = result.riskScore

                                        // Generate local UI results (optional)
                                        scanResult = appInfoList.map { info ->
                                            val risk = when {
                                                info.packageName.contains("malware", true) -> RiskLevel.MALICIOUS
                                                info.packageName.contains("suspicious", true) -> RiskLevel.SUSPICIOUS
                                                Random().nextInt(20) == 0 -> RiskLevel.SUSPICIOUS
                                                else -> RiskLevel.SAFE
                                            }
                                            ScannedApp(
                                                packageName = info.packageName,
                                                appName = try { pm.getApplicationLabel(pm.getApplicationInfo(info.packageName, 0)).toString() } catch (e: Exception) { info.packageName },
                                                riskLevel = risk,
                                                issues = generateIssues(risk, info.packageName)
                                            )
                                        }

                                        snackbarHostState.showSnackbar("Scan uploaded: ${result.threatsFound} threats")
                                    } else {
                                        throw Exception("HTTP ${response.code()}")
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Upload failed, using local scan")
                                    // Fallback: use local simulation
                                    scanResult = appInfoList.map { info ->
                                        val risk = simulateRiskAnalysis(info.packageName)
                                        ScannedApp(
                                            packageName = info.packageName,
                                            appName = try { pm.getApplicationLabel(pm.getApplicationInfo(info.packageName, 0)).toString() } catch (e: Exception) { info.packageName },
                                            riskLevel = risk,
                                            issues = generateIssues(risk, info.packageName)
                                        )
                                    }
                                }
                            } else {
                                // No token → local only
                                scanResult = appInfoList.map { info ->
                                    val risk = simulateRiskAnalysis(info.packageName)
                                    ScannedApp(
                                        packageName = info.packageName,
                                        appName = try { pm.getApplicationLabel(pm.getApplicationInfo(info.packageName, 0)).toString() } catch (e: Exception) { info.packageName },
                                        riskLevel = risk,
                                        issues = generateIssues(risk, info.packageName)
                                    )
                                }
                                snackbarHostState.showSnackbar("Offline: local scan only")
                            }

                            isScanning = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("Rescan All Apps")
                }
                Spacer(modifier = Modifier.height(Spacing.base))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(scanResult!!) { app ->
                        AppResultCard(
                            app = app,
                            onClick = { selectedApp = app },
                            onRemediate = {
                                scope.launch {
                                    uninstallApp(context, app.packageName)
                                    snackbarHostState.showSnackbar("${app.appName} uninstalled")
                                    scanResult = scanResult?.filter { it.packageName != app.packageName }
                                }
                            }
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(Spacing.base))
                    Text(
                        "No scan yet",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        "Tap the scan button to check your apps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // App Detail Bottom Sheet
        selectedApp?.let { app ->
            AppDetailSheet(
                app = app,
                onDismiss = { selectedApp = null },
                onRemediate = {
                    scope.launch {
                        uninstallApp(context, app.packageName)
                        snackbarHostState.showSnackbar("${app.appName} removed")
                        scanResult = scanResult?.filter { it.packageName != app.packageName }
                        selectedApp = null
                    }
                }
            )
        }
    }
}

// === REST OF YOUR ORIGINAL CODE (AppResultCard, AppDetailSheet, helpers) ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDetailSheet(
    app: ScannedApp,
    onDismiss: () -> Unit,
    onRemediate: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.base)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Icon(
                    imageVector = when (app.riskLevel) {
                        RiskLevel.SAFE -> Icons.Default.CheckCircle
                        RiskLevel.SUSPICIOUS -> Icons.Default.Warning
                        RiskLevel.MALICIOUS -> Icons.Default.Dangerous
                    },
                    contentDescription = null,
                    tint = when (app.riskLevel) {
                        RiskLevel.SAFE -> MaterialTheme.colorScheme.primary
                        RiskLevel.SUSPICIOUS -> MaterialTheme.colorScheme.tertiary
                        RiskLevel.MALICIOUS -> MaterialTheme.colorScheme.error
                    }
                )
                Column {
                    Text(app.appName, fontWeight = FontWeight.SemiBold)
                    Text(
                        app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.base))

            if (app.issues.isNotEmpty()) {
                Text("Issues Found:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(Spacing.xs))
                app.issues.forEach { issue ->
                    Row(
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(issue, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.base))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Close")
                }

                if (app.riskLevel != RiskLevel.SAFE) {
                    Button(
                        onClick = onRemediate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Uninstall")
                    }
                }
            }
        }
    }
}

@Composable
private fun AppResultCard(
    app: ScannedApp,
    onClick: () -> Unit,
    onRemediate: () -> Unit
) {
    SGCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.base),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Icon(
                    imageVector = when (app.riskLevel) {
                        RiskLevel.SAFE -> Icons.Default.CheckCircle
                        RiskLevel.SUSPICIOUS -> Icons.Default.Warning
                        RiskLevel.MALICIOUS -> Icons.Default.Dangerous
                    },
                    contentDescription = null,
                    tint = when (app.riskLevel) {
                        RiskLevel.SAFE -> MaterialTheme.colorScheme.primary
                        RiskLevel.SUSPICIOUS -> MaterialTheme.colorScheme.tertiary
                        RiskLevel.MALICIOUS -> MaterialTheme.colorScheme.error
                    }
                )
                Column {
                    Text(app.appName, fontWeight = FontWeight.Medium)
                    Text(
                        app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (app.riskLevel != RiskLevel.SAFE) {
                IconButton(onClick = onRemediate) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Uninstall",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// === HELPER FUNCTIONS ===
private fun getInstalledApps(pm: PackageManager): List<ApplicationInfo> {
    // Get ALL installed applications (including system apps, services, etc.)
    // Filter out only the current app itself to avoid scanning our own app
    val allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
    return allApps.filter { 
        // Exclude our own app from the scan
        it.packageName != "tn.esprit.dam_android"
    }
}

private fun installedAppsCount(context: android.content.Context): Int {
    return getInstalledApps(context.packageManager).size
}

private fun simulateRiskAnalysis(packageName: String): RiskLevel {
    return when {
        packageName.contains("malware", ignoreCase = true) -> RiskLevel.MALICIOUS
        packageName.contains("suspicious", ignoreCase = true) -> RiskLevel.SUSPICIOUS
        Random().nextInt(20) == 0 -> RiskLevel.SUSPICIOUS
        else -> RiskLevel.SAFE
    }
}

private fun generateIssues(risk: RiskLevel, packageName: String): List<String> {
    return when (risk) {
        RiskLevel.MALICIOUS -> listOf("Known malware", "Data theft", "Root exploit")
        RiskLevel.SUSPICIOUS -> listOf("Excessive permissions", "Unusual network")
        else -> emptyList()
    }
}

private fun uninstallApp(context: android.content.Context, packageName: String) {
    val intent = android.content.Intent(android.content.Intent.ACTION_DELETE).apply {
        data = android.net.Uri.parse("package:$packageName")
    }
    context.startActivity(intent)
}