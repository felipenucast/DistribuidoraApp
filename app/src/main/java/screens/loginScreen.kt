package com.example.distribuidoraapp.screens

//agregamos las librerías  a usar
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


//screen de login usando compose
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier, //modificador para personalizar la ui

    //callbacks crear cuenta e ingresar
    onSignUpClick: () -> Unit,
    onLoginClick: (email: String, password: String) -> Unit
) {
    //Almacenar usuario y contraseña
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

     //ajuste para que el contenedor ocupe todo el espacio disponible
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            //titulo del screen
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            //Campo texto para correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            //campo texto para contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth    ()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onLoginClick(email.trim(), password.trim()) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            //botón para crear cuenta
            TextButton(onClick = onSignUpClick) {
                Text("Crear cuenta")
            }
        }
    }
}
