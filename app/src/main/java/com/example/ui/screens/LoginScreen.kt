package com.example.ui.screens

import android.app.Activity
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.theme.SamityGold
import com.example.ui.theme.SamityNavyDark
import com.example.ui.theme.SamityNavyPrimary
import com.example.ui.theme.SlateBackground
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Modern Material3 Login & Authentication Screen powered by Firebase Auth.
 * Features:
 * - Email and Password authentication with real-time validation and error handling
 * - Mode toggle between Sign In and Create Account (Sign Up)
 * - Password visibility toggle and accessible keyboard actions
 * - Google Sign-In via Credential Manager integration
 * - Password reset dialog flow via Firebase Auth
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (FirebaseUser) -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val auth = remember { Firebase.auth }

    // Form State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }

    // Validation State (touched flags)
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    // Async UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Forgot Password Dialog State
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Validation helpers
    val isEmailValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val isPasswordValid = password.length >= 6

    val emailErrorText = when {
        !emailTouched -> null
        email.isBlank() -> "Email address is required"
        !isEmailValid -> "Enter a valid email address (e.g. user@example.com)"
        else -> null
    }

    val passwordErrorText = when {
        !passwordTouched -> null
        password.isBlank() -> "Password is required"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isSignUpMode) "Create Account" else "Sign In",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SamityNavyPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Hero Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(SamityNavyDark, SamityNavyPrimary)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SamityGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSignUpMode) Icons.Default.PersonAdd else Icons.Default.Lock,
                                contentDescription = null,
                                tint = SamityNavyDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isSignUpMode) "Register with Firebase" else "Welcome Back",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            text = if (isSignUpMode)
                                "Create a new account with your email and password"
                            else
                                "Sign in to access your cloud-synchronized dashboard",
                            fontSize = 13.sp,
                            color = Color(0xFFD0E4FF),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Authentication Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Feedback messages
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        errorMessage?.let { error ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontSize = 13.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { errorMessage = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Dismiss",
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = successMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        successMessage?.let { success ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFECFDF5),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = success,
                                        color = Color(0xFF065F46),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Email Input Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailTouched = true
                            errorMessage = null
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("user@example.com") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = if (emailErrorText != null) MaterialTheme.colorScheme.error else SamityNavyPrimary
                            )
                        },
                        trailingIcon = {
                            if (email.isNotEmpty()) {
                                IconButton(onClick = { email = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear email",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        isError = emailErrorText != null,
                        supportingText = {
                            if (emailErrorText != null) {
                                Text(
                                    text = emailErrorText,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Input Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordTouched = true
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        placeholder = { Text("At least 6 characters") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (passwordErrorText != null) MaterialTheme.colorScheme.error else SamityNavyPrimary
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.testTag("password_visibility_toggle")
                            ) {
                                val visibilityIcon = if (passwordVisible) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                }
                                val description = if (passwordVisible) "Hide password" else "Show password"
                                Icon(
                                    imageVector = visibilityIcon,
                                    contentDescription = description,
                                    tint = if (passwordVisible) SamityNavyPrimary else Color(0xFF64748B)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordErrorText != null,
                        supportingText = {
                            if (passwordErrorText != null) {
                                Text(
                                    text = passwordErrorText,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            } else {
                                Text(
                                    text = "Minimum 6 characters",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    // Forgot Password link (in Sign In mode)
                    if (!isSignUpMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showForgotPasswordDialog = true },
                                modifier = Modifier.testTag("forgot_password_button")
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    fontSize = 13.sp,
                                    color = SamityNavyPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Submit Button (Sign In / Sign Up)
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            emailTouched = true
                            passwordTouched = true

                            if (!isEmailValid) {
                                errorMessage = "Please enter a valid email address."
                                return@Button
                            }
                            if (!isPasswordValid) {
                                errorMessage = "Password must be at least 6 characters."
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null
                            successMessage = null

                            scope.launch {
                                try {
                                    val trimmedEmail = email.trim()
                                    val user = if (isSignUpMode) {
                                        val result = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
                                        result.user
                                    } else {
                                        val result = auth.signInWithEmailAndPassword(trimmedEmail, password).await()
                                        result.user
                                    }

                                    if (user != null) {
                                        successMessage = if (isSignUpMode)
                                            "Account created successfully for ${user.email}!"
                                        else
                                            "Welcome back, ${user.email}!"
                                        onLoginSuccess(user)
                                    } else {
                                        errorMessage = "Authentication failed. Please try again."
                                    }
                                } catch (e: FirebaseAuthInvalidUserException) {
                                    errorMessage = "No account found with this email. Please check your email or sign up."
                                } catch (e: FirebaseAuthInvalidCredentialsException) {
                                    errorMessage = "Invalid credentials. Please verify your email and password."
                                } catch (e: FirebaseAuthUserCollisionException) {
                                    errorMessage = "An account already exists with this email address."
                                } catch (e: FirebaseAuthWeakPasswordException) {
                                    errorMessage = "Password is too weak. Please use a stronger password."
                                } catch (e: Exception) {
                                    Log.e("LoginScreen", "Firebase Auth error", e)
                                    errorMessage = e.localizedMessage ?: "Authentication failed."
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isSignUpMode) "Creating Account..." else "Signing In...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = if (isSignUpMode) Icons.Default.PersonAdd else Icons.AutoMirrored.Filled.Login,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSignUpMode) "Create Account" else "Sign In",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mode Toggle (Sign In <-> Sign Up)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUpMode) "Already have an account?" else "Don't have an account?",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                        TextButton(
                            onClick = {
                                isSignUpMode = !isSignUpMode
                                errorMessage = null
                                successMessage = null
                            },
                            modifier = Modifier.testTag("toggle_auth_mode_button")
                        ) {
                            Text(
                                text = if (isSignUpMode) "Sign In" else "Sign Up",
                                fontWeight = FontWeight.Bold,
                                color = SamityNavyPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Divider with "OR"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "OR",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Sign-In with Credential Manager
                    OutlinedButton(
                        onClick = {
                            val clientId = try {
                                context.getString(R.string.default_web_client_id)
                            } catch (e: Exception) {
                                errorMessage = "Google Client ID not configured in google-services.json"
                                return@OutlinedButton
                            }

                            val credentialManager = CredentialManager.create(context)
                            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(signInOption)
                                .build()

                            isLoading = true
                            errorMessage = null

                            scope.launch {
                                try {
                                    val result = credentialManager.getCredential(context as Activity, request)
                                    val credential = result.credential
                                    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                                        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                                        val authResult = auth.signInWithCredential(authCredential).await()
                                        val user = authResult.user
                                        if (user != null) {
                                            successMessage = "Signed in with Google as ${user.email}"
                                            onLoginSuccess(user)
                                        }
                                    } else {
                                        errorMessage = "Unexpected Google credential type"
                                    }
                                } catch (e: GetCredentialCancellationException) {
                                    Log.w("LoginScreen", "Google Sign-In dismissed: ${e.message}")
                                } catch (e: Exception) {
                                    Log.e("LoginScreen", "Google Sign-In failed", e)
                                    errorMessage = e.localizedMessage ?: "Google Sign-In failed"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_sign_in_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Google",
                            tint = Color(0xFFEA4335),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            color = SamityNavyDark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            initialEmail = email.trim(),
            onDismiss = { showForgotPasswordDialog = false },
            onResetSent = { sentEmail ->
                successMessage = "Password reset link sent to $sentEmail. Check your inbox."
            }
        )
    }
}

/**
 * Material3 'Forgot Password' Dialog that allows users to trigger a Firebase password reset email.
 * Includes email validation, real-time error handling, loading states, and test tags.
 */
@Composable
fun ForgotPasswordDialog(
    initialEmail: String = "",
    onDismiss: () -> Unit,
    onResetSent: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val auth = remember { Firebase.auth }

    var email by remember { mutableStateOf(initialEmail) }
    var emailTouched by remember { mutableStateOf(initialEmail.isNotBlank()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    val isEmailValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val emailErrorText = when {
        !emailTouched -> null
        email.isBlank() -> "Email address is required"
        !isEmailValid -> "Enter a valid email address (e.g. user@example.com)"
        else -> null
    }

    AlertDialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        },
        modifier = Modifier.testTag("forgot_password_dialog"),
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SamityNavyPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = SamityNavyPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Text(
                text = "Forgot Password?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = SamityNavyDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enter your registered account email address. Firebase will send a secure password reset link to your inbox.",
                    fontSize = 13.sp,
                    color = Color(0xFF475569),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailTouched = true
                        errorMessage = null
                    },
                    label = { Text("Registered Email") },
                    placeholder = { Text("user@example.com") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = if (emailErrorText != null || errorMessage != null) {
                                MaterialTheme.colorScheme.error
                            } else {
                                SamityNavyPrimary
                            }
                        )
                    },
                    trailingIcon = {
                        if (email.isNotEmpty()) {
                            IconButton(onClick = { email = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear email",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    singleLine = true,
                    isError = emailErrorText != null || errorMessage != null,
                    supportingText = {
                        val text = emailErrorText ?: errorMessage
                        if (text != null) {
                            Text(
                                text = text,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forgot_password_email_input")
                )

                if (isSuccess) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFECFDF5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Password reset email sent! Check your inbox and spam folder to set a new password.",
                                color = Color(0xFF065F46),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isSuccess) {
                Button(
                    onClick = {
                        emailTouched = true
                        val trimmed = email.trim()
                        if (!isEmailValid) {
                            errorMessage = "Please enter a valid email address."
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        scope.launch {
                            try {
                                auth.sendPasswordResetEmail(trimmed).await()
                                isSuccess = true
                                onResetSent(trimmed)
                            } catch (e: FirebaseAuthInvalidUserException) {
                                errorMessage = "No account found with this email. Please check your email or sign up."
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                errorMessage = "Invalid email format. Please check for errors."
                            } catch (e: Exception) {
                                errorMessage = e.localizedMessage ?: "Failed to send password reset email."
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                    modifier = Modifier.testTag("send_password_reset_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sending...", fontSize = 14.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Reset Link", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            } else {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    modifier = Modifier.testTag("done_password_reset_button")
                ) {
                    Text("Done", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        },
        dismissButton = {
            if (!isSuccess) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoading,
                    modifier = Modifier.testTag("cancel_password_reset_button")
                ) {
                    Text("Cancel", color = Color(0xFF64748B), fontSize = 14.sp)
                }
            }
        }
    )
}
