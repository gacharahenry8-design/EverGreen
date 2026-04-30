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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.evergreen.data.AuthViewModel
import com.example.evergreen.navigation.Routes
import com.example.evergreen.navigation.navigateToLoginFromRegister
import com.example.evergreen.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

// ─── Register Screen ──────────────────────────────────────────────────────────
@Composable
fun RegisterScreen(
    navController: NavController,
    vm: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // ── State ─────────────────────────────────────────────────────────────────
    var fullName         by remember { mutableStateOf("") }
    var email            by remember { mutableStateOf("") }
    var password         by remember { mutableStateOf("") }
    var confirmPassword  by remember { mutableStateOf("") }
    var showPassword     by remember { mutableStateOf(false) }
    var showConfirm      by remember { mutableStateOf(false) }
    
    val isLoading by vm.isLoading.collectAsState()
    val errorMsg by vm.errorMessage.collectAsState()

    val passwordStrength = getPasswordStrength(password)

    // Handle navigation events from ViewModel
    LaunchedEffect(Unit) {
        vm.navigationEvent.collect { route ->
            navController.navigate(route) {
                popUpTo(Routes.REGISTER) { inclusive = true }
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
                    .padding(bottom = 28.dp)
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
                        color         = CarbonGrayLight,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // ── Page heading ──────────────────────────────────────────────────
            Text(
                text       = "Create account",
                fontSize   = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color      = EverGreenDark,
                modifier   = Modifier.fillMaxWidth()
            )
            Text(
                text     = "Start your eco journey today",
                fontSize = 14.sp,
                color    = CarbonGrayLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 20.dp)
            )

            // ── Error banner ──────────────────────────────────────────────────
            AnimatedVisibility(
                visible = errorMsg != null,
                enter   = fadeIn(),
                exit    = fadeOut()
            ) {
                errorMsg?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(10.dp)
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

            // ── Full name field ───────────────────────────────────────────────
            OutlinedTextField(
                value         = fullName,
                onValueChange = { fullName = it; vm.clearError() },
                label         = { Text("Full name") },
                leadingIcon   = {
                    Icon(Icons.Filled.Person, contentDescription = "Name", tint = EverGreenAccent)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
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

            // ── Email field ───────────────────────────────────────────────────
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it; vm.clearError() },
                label         = { Text("Email address") },
                leadingIcon   = {
                    Icon(Icons.Filled.Email, contentDescription = "Email", tint = EverGreenAccent)
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

            // ── Password field + strength bar ─────────────────────────────────
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it; vm.clearError() },
                label         = { Text("Password") },
                leadingIcon   = {
                    Icon(Icons.Filled.Lock, contentDescription = "Password", tint = EverGreenAccent)
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Hide" else "Show",
                            tint = CarbonGrayLight
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier   = Modifier.fillMaxWidth(),
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = EverGreenPrimary,
                    unfocusedBorderColor = EverGreenLight,
                    focusedLabelColor    = EverGreenPrimary,
                    cursorColor          = EverGreenPrimary
                )
            )

            // Password strength indicator
            if (password.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val segments = 4
                        val filledCount = when (passwordStrength) {
                            PasswordStrength.WEAK   -> 1
                            PasswordStrength.FAIR   -> 2
                            PasswordStrength.STRONG -> 4
                            PasswordStrength.NONE   -> 0
                        }
                        val barColor = when (passwordStrength) {
                            PasswordStrength.WEAK   -> StatusDanger
                            PasswordStrength.FAIR   -> LeafGold
                            PasswordStrength.STRONG -> EverGreenPrimary
                            PasswordStrength.NONE   -> EverGreenLight
                        }
                        repeat(segments) { index ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (index < filledCount) barColor else EverGreenLight
                                    )
                            )
                        }
                    }
                    Text(
                        text     = when (passwordStrength) {
                            PasswordStrength.WEAK   -> "Weak password"
                            PasswordStrength.FAIR   -> "Fair password"
                            PasswordStrength.STRONG -> "Strong password"
                            PasswordStrength.NONE   -> ""
                        },
                        fontSize = 11.sp,
                        color    = when (passwordStrength) {
                            PasswordStrength.WEAK   -> StatusDanger
                            PasswordStrength.FAIR   -> LeafGold
                            PasswordStrength.STRONG -> EverGreenPrimary
                            PasswordStrength.NONE   -> CarbonGrayLight
                        },
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            } else {
                Spacer(Modifier.height(12.dp))
            }

            // ── Confirm password field ────────────────────────────────────────
            OutlinedTextField(
                value         = confirmPassword,
                onValueChange = { confirmPassword = it; vm.clearError() },
                label         = { Text("Confirm password") },
                leadingIcon   = {
                    Icon(Icons.Filled.Lock, contentDescription = "Confirm", tint = EverGreenAccent)
                },
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                            contentDescription = if (showConfirm) "Hide" else "Show",
                            tint = CarbonGrayLight
                        )
                    }
                },
                visualTransformation = if (showConfirm) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { 
                        if (password == confirmPassword) {
                            vm.signUpUser(email, password, fullName)
                        }
                    }
                ),
                singleLine  = true,
                isError     = confirmPassword.isNotEmpty() && confirmPassword != password,
                supportingText = {
                    if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                        Text("Passwords do not match", color = StatusDanger, fontSize = 11.sp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = EverGreenPrimary,
                    unfocusedBorderColor = EverGreenLight,
                    focusedLabelColor    = EverGreenPrimary,
                    cursorColor          = EverGreenPrimary,
                    errorBorderColor     = StatusDanger
                )
            )

            // ── Create account button ─────────────────────────────────────────
            Button(
                onClick  = { 
                    if (password == confirmPassword) {
                        vm.signUpUser(email, password, fullName)
                    }
                },
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
                        color       = Color.White,
                        modifier    = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Create account",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ── Divider ───────────────────────────────────────────────────────
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier  = Modifier.weight(1f),
                    thickness = 0.5.dp,
                    color     = EverGreenLight
                )
                Text(
                    text     = "  or sign up with  ",
                    fontSize = 12.sp,
                    color    = CarbonGrayLight
                )
                HorizontalDivider(
                    modifier  = Modifier.weight(1f),
                    thickness = 0.5.dp,
                    color     = EverGreenLight
                )
            }

            // ── Google sign-up button ─────────────────────────────────────────
            OutlinedButton(
                onClick  = { vm.signInWithGoogle(context) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 0.5.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CarbonGray)
            ) {
                Text("G", fontSize = 16.sp, color = Color(0xFFEA4335), fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(10.dp))
                Text("Continue with Google", fontSize = 14.sp, color = CarbonGray)
            }

            // ── Terms ─────────────────────────────────────────────────────────
            Text(
                text     = "By creating an account you agree to our Terms of Service and Privacy Policy",
                fontSize = 11.sp,
                color    = CarbonGrayLight,
                modifier = Modifier.padding(top = 16.dp),
                lineHeight = 16.sp
            )

            // ── Sign in link ──────────────────────────────────────────────────
            Row(
                modifier              = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text     = "Already have an account? ",
                    fontSize = 13.sp,
                    color    = CarbonGrayLight
                )
                Text(
                    text       = "Sign in",
                    fontSize   = 13.sp,
                    color      = EverGreenPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.clickable {
                        navController.navigateToLoginFromRegister()
                    }
                )
            }
        }
    }
}

// ─── Utilities ──────────────────────────────────────────────────────────────
enum class PasswordStrength {
    NONE, WEAK, FAIR, STRONG
}

fun getPasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.NONE
    if (password.length < 6) return PasswordStrength.WEAK

    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return when {
        score >= 3 -> PasswordStrength.STRONG
        score >= 1 -> PasswordStrength.FAIR
        else -> PasswordStrength.WEAK
    }
}

@Preview(showBackground = true, name = "Register Light Mode")
@Composable
fun RegisterScreenPreview() {
    EverGreenTheme(darkTheme = false) {
        val navController = rememberNavController()
        RegisterScreen(navController = navController)
    }
}

@Preview(showBackground = true, name = "Register Dark Mode")
@Composable
fun RegisterScreenDarkPreview() {
    EverGreenTheme(darkTheme = true) {
        val navController = rememberNavController()
        RegisterScreen(navController = navController)
    }
}