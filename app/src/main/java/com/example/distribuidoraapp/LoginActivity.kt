package com.example.distribuidoraapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.distribuidoraapp.screens.CalculadoraScreen
import com.example.distribuidoraapp.screens.HomeScreen
import com.example.distribuidoraapp.screens.LoginScreen
import com.example.distribuidoraapp.screens.SignUpScreen
import com.example.distribuidoraapp.utils.FirebaseAuthHelper

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showSignUp by remember { mutableStateOf(false) }
            var showHome by remember { mutableStateOf(FirebaseAuthHelper.getCurrentUser() != null) }
            var showCalculadora by remember { mutableStateOf(false) }

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                when {
                    showCalculadora -> CalculadoraScreen(
                        userLat = null,   // Aquí podrías pasar coordenadas si las tienes
                        userLng = null,
                        onBack = { showCalculadora = false }
                    )
                    showHome -> HomeScreen(
                        onLogout = { showHome = false },
                        onOpenCalculadora = { showCalculadora = true }
                    )
                    showSignUp -> SignUpScreen(
                        onSignUpSuccess = { showSignUp = false }
                    )
                    else -> LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSignUpClick = { showSignUp = true },
                        onLoginClick = { email, password ->
                            FirebaseAuthHelper.login(
                                email,
                                password,
                                onSuccess = { showHome = true },
                                onError = { msg ->
                                    Toast.makeText(
                                        this@LoginActivity,
                                        msg,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
