package com.example.evergreen.data

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evergreen.models.UserModel
import com.example.evergreen.navigation.Routes
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference
    private val context get() = getApplication<Application>().applicationContext

    // ── Loading state ─────────────────────────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── One-shot navigation events ────────────────────────────────────────────
    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent.asSharedFlow()

    // ── Error messages ────────────────────────────────────────────────────────
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ─────────────────────────────────────────────────────────────────────────
    // Google Sign In
    // ─────────────────────────────────────────────────────────────────────────
    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val credentialManager = CredentialManager.create(context)
                
                // You'll need to replace this with your actual Web Client ID from Firebase Console
                // usually found in google-services.json under oauth_client with client_type 3
                val webClientId = "460590420938-your-web-client-id.apps.googleusercontent.com" 

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                handleGoogleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.e("AuthViewModel", "Google sign-in error: ${e.message}")
                _errorMessage.value = "Google sign-in failed: ${e.message}"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unexpected error occurred."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun handleGoogleSignIn(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is GoogleIdTokenCredential) {
            val googleIdToken = credential.idToken
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            val user = authResult.user
            
            if (user != null) {
                // Check if user exists in database, if not, save them
                val userSnapshot = db.child("Users").child(user.uid).get().await()
                if (!userSnapshot.exists()) {
                    saveUserToDatabase(
                        userId = user.uid,
                        name = user.displayName ?: "Google User",
                        email = user.email ?: ""
                    )
                }
                _navigationEvent.emit(Routes.DASHBOARD)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sign Up
    // ─────────────────────────────────────────────────────────────────────────
    fun signUpUser(email: String, pass: String, name: String) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            _errorMessage.value = "Please fill all fields"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val userId = result.user?.uid
                if (userId != null) {
                    saveUserToDatabase(userId, name, email)
                }
                Toast.makeText(context, "Signup successful!", Toast.LENGTH_SHORT).show()
                _navigationEvent.emit(Routes.DASHBOARD)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Signup failed. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun saveUserToDatabase(userId: String, name: String, email: String) {
        val user = UserModel(
            id = userId,
            username = name,
            email = email,
            totalPoints = 0,
            level = "Seedling 🌱"
        )
        db.child("Users").child(userId).setValue(user).await()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Login
    // ─────────────────────────────────────────────────────────────────────────
    fun loginUser(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Please fill all fields"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                _navigationEvent.emit(Routes.DASHBOARD)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Logout
    // ─────────────────────────────────────────────────────────────────────────
    fun logoutUser() {
        auth.signOut()
        viewModelScope.launch {
            _navigationEvent.emit(Routes.LOGIN)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Call after the error is shown so it doesn't re-trigger on recomposition
    // ─────────────────────────────────────────────────────────────────────────
    fun clearError() {
        _errorMessage.value = null
    }
}