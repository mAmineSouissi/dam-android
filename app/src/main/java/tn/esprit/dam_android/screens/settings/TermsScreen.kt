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
fun TermsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Terms of Service",
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

            SectionTitle("1. Acceptance of Terms")
            SectionContent(
                "By accessing and using the ShadowGuard mobile application, you accept and agree to be bound by the terms and provision of this agreement. If you do not agree to these Terms of Service, please do not use our application."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("2. Description of Service")
            SectionContent(
                "ShadowGuard is a mobile security application that provides device and application scanning services to help identify potential security threats, malware, and vulnerabilities in your installed applications."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("3. User Accounts")
            SectionContent("To use certain features of our service, you must register for an account. You agree to:")
            BulletPoint("Provide accurate, current, and complete information during registration")
            BulletPoint("Maintain and promptly update your account information")
            BulletPoint("Maintain the security of your account credentials")
            BulletPoint("Accept responsibility for all activities that occur under your account")
            BulletPoint("Notify us immediately of any unauthorized use of your account")

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("4. Acceptable Use")
            SectionContent("You agree not to:")
            BulletPoint("Use the service for any illegal or unauthorized purpose")
            BulletPoint("Violate any laws in your jurisdiction")
            BulletPoint("Interfere with or disrupt the service or servers")
            BulletPoint("Attempt to gain unauthorized access to any portion of the service")
            BulletPoint("Use automated systems to access the service without permission")
            BulletPoint("Reproduce, duplicate, copy, or exploit any portion of the service")

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("5. Service Availability")
            SectionContent(
                "We strive to provide reliable service but do not guarantee that the service will be available at all times. We reserve the right to modify, suspend, or discontinue any part of the service at any time with or without notice."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("6. Disclaimer of Warranties")
            SectionContent(
                "The service is provided \"as is\" and \"as available\" without warranties of any kind, either express or implied. We do not warrant that the service will be uninterrupted, secure, or error-free, or that defects will be corrected."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("7. Limitation of Liability")
            SectionContent(
                "To the fullest extent permitted by law, ShadowGuard shall not be liable for any indirect, incidental, special, consequential, or punitive damages, or any loss of profits or revenues, whether incurred directly or indirectly, or any loss of data, use, goodwill, or other intangible losses resulting from your use of the service."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("8. Indemnification")
            SectionContent(
                "You agree to indemnify and hold harmless ShadowGuard, its officers, directors, employees, and agents from any claims, damages, losses, liabilities, and expenses (including legal fees) arising out of or relating to your use of the service or violation of these Terms."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("9. Intellectual Property")
            SectionContent(
                "All content, features, and functionality of the service, including but not limited to text, graphics, logos, and software, are the exclusive property of ShadowGuard and are protected by international copyright, trademark, and other intellectual property laws."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("10. Termination")
            SectionContent(
                "We may terminate or suspend your account and access to the service immediately, without prior notice or liability, for any reason, including if you breach these Terms of Service. Upon termination, your right to use the service will immediately cease."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("11. Changes to Terms")
            SectionContent(
                "We reserve the right to modify or replace these Terms at any time. If a revision is material, we will provide at least 30 days notice prior to any new terms taking effect. What constitutes a material change will be determined at our sole discretion."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("12. Governing Law")
            SectionContent(
                "These Terms shall be governed by and construed in accordance with the laws of [Your Jurisdiction], without regard to its conflict of law provisions. Any disputes arising under these Terms shall be subject to the exclusive jurisdiction of the courts in [Your Jurisdiction]."
            )

            Spacer(modifier = Modifier.height(Spacing.base))

            SectionTitle("13. Contact Information")
            SectionContent(
                "If you have any questions about these Terms of Service, please contact us at:\n\n" +
                        "Email: legal@shadowguard.com\n" +
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
fun TermsPrivew(){
    ShadowGuardTheme {
        TermsScreen(onBack = {})
    }
}
