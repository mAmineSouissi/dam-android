package tn.esprit.dam_android.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tn.esprit.dam_android.api.local.TokenManager
import tn.esprit.dam_android.models.auth.repositories.ApiResult
import tn.esprit.dam_android.models.auth.repositories.AuthRepository
import tn.esprit.dam_android.ui.components.*
import tn.esprit.dam_android.ui.theme.ShadowGuardTheme
import tn.esprit.dam_android.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    onBack: () -> Unit,
    onUpdateSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val tokenManager = remember { TokenManager(context) }
    val authRepository = remember { AuthRepository(tokenManager) }

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isFetching by rememberSaveable { mutableStateOf(true) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    // Form fields
    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var userId by rememberSaveable { mutableStateOf<String?>(null) }

    // Field errors
    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var surnameError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneError by rememberSaveable { mutableStateOf<String?>(null) }

    // Fetch user profile on screen load
    LaunchedEffect(Unit) {
        val result = authRepository.getUserProfile()
        when (result) {
            is ApiResult.Success -> {
                val user = result.data
                userId = user.uid
                name = user.name ?: ""
                surname = user.surname ?: ""
                phone = user.phone ?: ""
                email = user.email
                isFetching = false
            }
            is ApiResult.Error -> {
                errorMessage = result.message
                isFetching = false
            }
            else -> {
                isFetching = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Update Profile", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (isFetching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(Spacing.base),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.base)
            ) {
                // Profile Avatar Section
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(Spacing.base),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(50.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = "Profile Information",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                // Global Error Message
                errorMessage?.let {
                    SGCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Email (Read-only)
                SGTextField(
                    value = email,
                    onValueChange = { },
                    label = "Email",
                    placeholder = "Email address",
                    keyboardType = KeyboardType.Email,
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    enabled = false
                )

                // Name
                SGTextField(
                    value = name,
                    onValueChange = {
                        name = it.trim()
                        nameError = null
                        errorMessage = null
                    },
                    label = "First Name",
                    placeholder = "Enter your first name",
                    isError = nameError != null,
                    errorMessage = nameError,
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )

                // Surname
                SGTextField(
                    value = surname,
                    onValueChange = {
                        surname = it.trim()
                        surnameError = null
                        errorMessage = null
                    },
                    label = "Last Name",
                    placeholder = "Enter your last name",
                    isError = surnameError != null,
                    errorMessage = surnameError,
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )

                // Phone
                SGTextField(
                    value = phone,
                    onValueChange = {
                        phone = it.trim()
                        phoneError = null
                        errorMessage = null
                    },
                    label = "Phone Number",
                    placeholder = "Enter your phone number",
                    keyboardType = KeyboardType.Phone,
                    isError = phoneError != null,
                    errorMessage = phoneError,
                    leadingIcon = { Icon(Icons.Default.Phone, null) }
                )

                Spacer(modifier = Modifier.height(Spacing.base))

                // Update Button
                SGButton(
                    text = if (isLoading) "Updating..." else "Update Profile",
                    onClick = {
                        // Validation
                        var hasError = false
                        if (name.isBlank()) {
                            nameError = "First name is required"
                            hasError = true
                        }
                        if (surname.isBlank()) {
                            surnameError = "Last name is required"
                            hasError = true
                        }
                        if (phone.isBlank()) {
                            phoneError = "Phone number is required"
                            hasError = true
                        }

                        if (hasError || userId == null) {
                            return@SGButton
                        }

                        isLoading = true
                        errorMessage = null

                        scope.launch {
                            val result = authRepository.updateProfile(
                                userId = userId!!,
                                name = name,
                                surname = surname,
                                phone = phone
                            )

                            when (result) {
                                is ApiResult.Success -> {
                                    snackbarHostState.showSnackbar("Profile updated successfully")
                                    onUpdateSuccess()
                                    onBack()
                                }
                                is ApiResult.Error -> {
                                    errorMessage = result.message
                                    snackbarHostState.showSnackbar("Update failed: ${result.message}")
                                }
                                else -> {}
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && !isFetching
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun UpdateProfileScreenPreview() {
    ShadowGuardTheme {
        UpdateProfileScreen(onBack = {})
    }
}
