package com.example.evergreen.data

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evergreen.models.UserModel
import com.example.evergreen.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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