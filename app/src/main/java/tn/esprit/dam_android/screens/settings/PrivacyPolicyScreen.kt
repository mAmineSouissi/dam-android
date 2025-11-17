package tn.esprit.dam_android.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tn.esprit.dam_android.ui.theme.ShadowGuardTheme
import tn.esprit.dam_android.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.base)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.base))

            Text(
                text = "Last Updated: October 15, 2024",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            SectionTitle("1. Introduction")
            SectionContent(
                "Welcome to ShadowGuard. We are committed to protecting your privacy and ensuring the security of your personal information. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("2. Information We Collect")
            SectionContent("We collect the following types of information:")
            BulletPoint("Device Information: Device model, operating system version, unique device identifiers")
            BulletPoint("App Information: Installed applications, app versions, and package names")
            BulletPoint("Account Information: Email address, name, and profile information you provide")
            BulletPoint("Usage Data: Scan results, security alerts, and app usage patterns")
            BulletPoint("Technical Data: IP address, device identifiers, and diagnostic information")

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("3. How We Use Your Information")
            SectionContent("We use the collected information for the following purposes:")
            BulletPoint("To provide and maintain our security scanning services")
            BulletPoint("To detect and analyze potential security threats in installed applications")
            BulletPoint("To send security alerts and notifications")
            BulletPoint("To improve our services and develop new features")
            BulletPoint("To communicate with you about your account and our services")
            BulletPoint("To comply with legal obligations and protect our rights")

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("4. Data Security")
            SectionContent(
                "We implement appropriate technical and organizational security measures to protect your personal information against unauthorized access, alteration, disclosure, or destruction. However, no method of transmission over the internet or electronic storage is 100% secure."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("5. Data Sharing and Disclosure")
            SectionContent("We do not sell your personal information. We may share your information only in the following circumstances:")
            BulletPoint("With your explicit consent")
            BulletPoint("To comply with legal obligations or respond to lawful requests")
            BulletPoint("To protect our rights, privacy, safety, or property")
            BulletPoint("In connection with a business transfer or merger")

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("6. Your Rights")
            SectionContent("You have the right to:")
            BulletPoint("Access and receive a copy of your personal data")
            BulletPoint("Rectify inaccurate or incomplete information")
            BulletPoint("Request deletion of your personal data")
            BulletPoint("Object to processing of your personal data")
            BulletPoint("Request restriction of processing")
            BulletPoint("Data portability")

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("7. Data Retention")
            SectionContent(
                "We retain your personal information only for as long as necessary to fulfill the purposes outlined in this Privacy Policy, unless a longer retention period is required or permitted by law."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("8. Children's Privacy")
            SectionContent(
                "Our services are not intended for individuals under the age of 13. We do not knowingly collect personal information from children under 13. If you become aware that a child has provided us with personal information, please contact us immediately."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("9. Changes to This Privacy Policy")
            SectionContent(
                "We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page and updating the \"Last Updated\" date."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("10. Contact Us")
            SectionContent(
                "If you have any questions about this Privacy Policy or our data practices, please contact us at:\n\n" +
                        "Email: privacy@shadowguard.com\n" +
                        "Address: [Your Company Address]"
            )

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = Spacing.sm)
    )
}

@Composable
private fun SectionContent(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = Spacing.xs)
    )
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.base, bottom = Spacing.xs)
    ) {
        Text(
            text = "• ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun PrivacyPolicyPreview(){
    ShadowGuardTheme {
        PrivacyPolicyScreen(onBack = {})
    }
}
