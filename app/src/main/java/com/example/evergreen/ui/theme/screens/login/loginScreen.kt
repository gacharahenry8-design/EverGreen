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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.evergreen.data.AuthViewModel
import com.example.evergreen.ui.theme.*
import com.example.evergreen.navigation.Routes

@Composable
fun LoginScreen(
    navController: NavController,
    vm: AuthViewModel = viewModel()
) {
    val isLoading by vm.isLoading.collectAsState()
    val errorMsg by vm.errorMessage.collectAsState()

    // Handle navigation events
    LaunchedEffect(Unit) {
        vm.navigationEvent.collect { route ->
            navController.navigate(route) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    LoginScreenContent(
        isLoading = isLoading,
        errorMsg = errorMsg,
        onLogin = { email, password -> vm.loginUser(email, password) },
        onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
        onClearError = { vm.clearError() }
    )
}

@Composable
fun LoginScreenContent(
    isLoading: Boolean,
    errorMsg: String?,
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onClearError: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    // ── State ─────────────────────────────────────────────────────────────────
    var email         by remember { mutableStateOf("") }
    var password      by remember { mutableStateOf("") }
    var showPassword  by remember { mutableStateOf(false) }

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
                        color      = Color.Black
                    )
                    Text(
                        text          = "TRACK. REDUCE. SUSTAIN.",
                        fontSize      = 9.sp,
                        color         = Color.Black,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // ── Page heading ──────────────────────────────────────────────────
            Text(
                text       = "Welcome back",
                fontSize   = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color.Black,
                modifier   = Modifier.fillMaxWidth()
            )
            Text(
                text     = "log in to your account",
                fontSize = 14.sp,
                color    = Color.Black,
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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
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

            // ── Email field ───────────────────────────────────────────────────
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it; onClearError() },
                label         = { Text("Email address", color = Color.Black) },
                leadingIcon   = { Icon(Icons.Filled.Email, "Email", tint = EverGreenAccent) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                singleLine = true,
                modifier   = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = EverGreenPrimary,
                    unfocusedBorderColor = EverGreenLight,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            // ── Password field ────────────────────────────────────────────────
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it; onClearError() },
                label         = { Text("Password", color = Color.Black) },
                leadingIcon   = { Icon(Icons.Filled.Lock, "Password", tint = EverGreenAccent) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Hide" else "Show",
                            tint = CarbonGrayLight
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onLogin(email, password) }),
                singleLine = true,
                modifier   = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = EverGreenPrimary,
                    unfocusedBorderColor = EverGreenLight,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            // ── Forgot password ───────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text     = "Forgot password?",
                    fontSize = 13.sp,
                    color    = EverGreenPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            // ── Sign in button ────────────────────────────────────────────────
            Button(
                onClick  = { onLogin(email, password) },
                enabled  = !isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EverGreenPrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("log in", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
            }



            // ── Sign up link ──────────────────────────────────────────────────
            Row(modifier = Modifier.padding(top = 28.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account? ", fontSize = 13.sp, color = Color.Black)
                Text(
                    text      = "Sign up",
                    fontSize  = 13.sp,
                    color     = EverGreenPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier  = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Light Mode")
@Composable
fun LoginScreenPreview() {
    EverGreenTheme(darkTheme = false) {
        LoginScreenContent(
            isLoading = false,
            errorMsg = null,
            onLogin = { _, _ -> },
            onNavigateToRegister = {},
            onClearError = {}
        )
    }
}

@Preview(showBackground = true, name = "Login Dark Mode")
@Composable
fun LoginScreenDarkPreview() {
    EverGreenTheme(darkTheme = true) {
        LoginScreenContent(
            isLoading = false,
            errorMsg = null,
            onLogin = { _, _ -> },
            onNavigateToRegister = {},
            onClearError = {}
        )
    }
}
