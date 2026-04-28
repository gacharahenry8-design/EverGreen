package com.example.evergreen.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.evergreen.ui.theme.*
import com.example.evergreen.navigation.Routes

@Composable
fun LoginScreen(navController: NavController) {

    val focusManager = LocalFocusManager.current

    // ── State ─────────────────────────────────────────────────────────────────
    var email         by remember { mutableStateOf("") }
    var password      by remember { mutableStateOf("") }
    var showPassword  by remember { mutableStateOf(false) }
    var isLoading     by remember { mutableStateOf(false) }
    var errorMessage  by remember { mutableStateOf<String?>(null) }

    // ── Sign-in logic ─────────────────────────────────────────────────────────
    fun signIn() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill in all fields"
            return
        }
        isLoading    = true
        errorMessage = null
        focusManager.clearFocus()

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                } else {
                    val exception = task.exception
                    errorMessage = when (exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException -> {
                            "No account found with this email"
                        }
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
                            "Incorrect email or password"
                        }
                        is com.google.firebase.FirebaseNetworkException -> {
                            "Network error. Please check your internet connection"
                        }
                        else -> {
                            exception?.localizedMessage ?: "Sign in failed. Please try again"
                        }
                    }
                }
            }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EverGreenSurface)
    ) {
        // Decorative corner leaves
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset((-20).dp, (-20).dp)
                .clip(RoundedCornerShape(50, 0, 50, 0))
                .background(EverGreenPrimary.copy(alpha = 0.12f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(60.dp)
                .offset(15.dp, 20.dp)
                .clip(RoundedCornerShape(0, 50, 0, 50))
                .background(EverGreenPrimary.copy(alpha = 0.08f))
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo & branding ───────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EverGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌿", fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text       = "EverGreen",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = EverGreenDark
                    )
                    Text(
                        text          = "TRACK. REDUCE. SUSTAIN.",
                        fontSize      = 9.sp,
                        fontWeight    = FontWeight.Normal,
                        color         = CarbonGrayLight,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // ── Page heading ──────────────────────────────────────────────────
            Text(
                text       = "Welcome back",
                fontSize   = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color      = EverGreenDark,
                modifier   = Modifier.fillMaxWidth()
            )
            Text(
                text     = "Sign in to your account",
                fontSize = 14.sp,
                color    = CarbonGrayLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 20.dp)
            )

            // ── Error banner ──────────────────────────────────────────────────
            AnimatedVisibility(
                visible = errorMessage != null,
                enter   = fadeIn(),
                exit    = fadeOut()
            ) {
                errorMessage?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors   = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape    = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text     = msg,
                            fontSize = 13.sp,
                            color    = Color(0xFFC62828),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // ── Email field ───────────────────────────────────────────────────
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it; errorMessage = null },
                label         = { Text("Email address") },
                leadingIcon   = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email",
                        tint = EverGreenAccent
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction    = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = EverGreenPrimary,
                    unfocusedBorderColor = EverGreenLight,
                    focusedLabelColor    = EverGreenPrimary,
                    cursorColor          = EverGreenPrimary
                )
            )

            // ── Password field ────────────────────────────────────────────────
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it; errorMessage = null },
                label         = { Text("Password") },
                leadingIcon   = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password",
                        tint = EverGreenAccent
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword)
                                Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Hide" else "Show",
                            tint = CarbonGrayLight
                        )
                    }
                },
                visualTransformation = if (showPassword)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { signIn() }
                ),
                singleLine = true,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = EverGreenPrimary,
                    unfocusedBorderColor = EverGreenLight,
                    focusedLabelColor    = EverGreenPrimary,
                    cursorColor          = EverGreenPrimary
                )
            )

            // ── Forgot password ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text     = "Forgot password?",
                    fontSize = 13.sp,
                    color    = EverGreenPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        // TODO: navigate to ForgotPasswordScreen
                    }
                )
            }

            // ── Sign in button ────────────────────────────────────────────────
            Button(
                onClick  = { signIn() },
                enabled  = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EverGreenPrimary,
                    contentColor   = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color     = Color.White,
                        modifier  = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Sign in",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ── Divider ───────────────────────────────────────────────────────
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier  = Modifier.weight(1f),
                    thickness = 0.5.dp,
                    color     = EverGreenLight
                )
                Text(
                    text     = "  or continue with  ",
                    fontSize = 12.sp,
                    color    = CarbonGrayLight
                )
                HorizontalDivider(
                    modifier  = Modifier.weight(1f),
                    thickness = 0.5.dp,
                    color     = EverGreenLight
                )
            }

            // ── Google sign-in button ─────────────────────────────────────────
            OutlinedButton(
                onClick  = { /* TODO: implement Google Sign-In */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 0.5.dp
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CarbonGray
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("G", fontSize = 16.sp, color = Color(0xFFEA4335), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text     = "Continue with Google",
                        fontSize = 14.sp,
                        color    = CarbonGray
                    )
                }
            }

            // ── Sign up link ──────────────────────────────────────────────────
            Row(
                modifier          = Modifier.padding(top = 28.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text     = "Don't have an account? ",
                    fontSize = 13.sp,
                    color    = CarbonGrayLight
                )
                Text(
                    text      = "Sign up",
                    fontSize  = 13.sp,
                    color     = EverGreenPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier  = Modifier.clickable {
                        navController.navigate(Routes.REGISTER)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Light Mode")
@Composable
fun LoginScreenPreview() {
    EverGreenTheme(darkTheme = false) {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}

@Preview(showBackground = true, name = "Login Dark Mode")
@Composable
fun LoginScreenDarkPreview() {
    EverGreenTheme(darkTheme = true) {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}
