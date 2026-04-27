package com.example.evergreen.data

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.evergreen.navigation.Routes
import com.example.evergreen.navigation.navigateToDashboard
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel(var navController: NavController, var context: Context) {

    private val auth = FirebaseAuth.getInstance()

    fun signUpUser(email: String, pass: String, name: String) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Signup successful!", Toast.LENGTH_SHORT).show()
                    navController.navigateToDashboard()
                } else {
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun loginUser(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    navController.navigateToDashboard()
                } else {
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun logoutUser() {
        auth.signOut()
        navController.navigate(Routes.LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }
}
