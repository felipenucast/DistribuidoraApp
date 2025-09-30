package com.example.distribuidoraapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import com.example.distribuidoraapp.utils.AppConstants
import kotlin.math.*

/**
 * Pantalla de calculadora de costos de envío
 *
 * Calcula la distancia entre la ubicación del usuario y la distribuidora
 * usando la fórmula Haversine y estima el costo basado en la distancia

 */
@Composable
fun CalculadoraScreen(
    userLat: Double?,    // Latitud actual del usuario desde GPS
    userLng: Double?,    // Longitud actual del usuario desde GPS
    onBack: () -> Unit   // Navegación para regresar al home
) {
    // Estado para almacenar la distancia calculada
    var distance by remember { mutableStateOf<Double?>(null) }

    // Estados para capturar coordenadas manuales usando TextFieldValue
    var latInput by remember { mutableStateOf(TextFieldValue("")) }  // Input de latitud
    var lngInput by remember { mutableStateOf(TextFieldValue("")) }  // Input de longitud

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula Haversine
     * @param lat1 Latitud del punto 1
     * @param lon1 Longitud del punto 1
     * @param lat2 Latitud del punto 2 (distribuidora)
     * @param lon2 Longitud del punto 2 (distribuidora)
     * @return Distancia en kilómetros entre los dos puntos
     */
    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radio de la Tierra en kilómetros
        val dLat = Math.toRadians(lat2 - lat1)  // Diferencia de latitud en radianes
        val dLon = Math.toRadians(lon2 - lon1)  // Diferencia de longitud en radianes

        // Fórmula Haversine para cálculo de distancia en esfera
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c  // Distancia final en kilómetros
    }

    /**
     * Efecto que se ejecuta automáticamente cuando las coordenadas del GPS están disponibles
     * Calcula la distancia entre la ubicación del usuario y la distribuidora (Fija almacenadas en AppConstants)
     */
    LaunchedEffect(userLat, userLng) {
        if (userLat != null && userLng != null) {
            // Calcula distancia usando coordenadas del GPS
            distance = haversine(
                userLat, userLng,  // Coordenadas del usuario
                AppConstants.DISTRIBUIDORA_LAT,  // Latitud de la distribuidora desde constantes
                AppConstants.DISTRIBUIDORA_LNG   // Longitud de la distribuidora desde constantes
            )
        }
    }

    // Diseño principal de la pantalla
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,  // Contenido alineado arriba
            horizontalAlignment = Alignment.CenterHorizontally  // Centrado horizontal
        ) {
            // Título de la pantalla
            Text("Calculadora de Costos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // --- SECCIÓN DE RESULTADOS AUTOMÁTICOS ---
            if (distance != null) {
                // Muestra distancia formateada a 2 decimales
                Text("Distancia desde distribuidora: ${"%.2f".format(distance)} km")

                // Calcula y muestra costo estimado (distancia * costo por km)
                Text("Costo estimado: \$${"%.0f".format(distance!! * AppConstants.COSTO_POR_KM)}")
            } else {
                // Mensaje mientras se calcula la distancia
                Text("Obteniendo distancia...")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SECCIÓN PARA INGRESO MANUAL DE COORDENADAS ---
            Text("Ingresar coordenadas manualmente:")
            Spacer(modifier = Modifier.height(8.dp))

            // Campo para ingresar latitud manualmente
            OutlinedTextField(
                value = latInput,
                onValueChange = { latInput = it },  // Actualiza estado al escribir
                label = { Text("Latitud") }  // Placeholder del campo
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Campo para ingresar longitud manualmente
            OutlinedTextField(
                value = lngInput,
                onValueChange = { lngInput = it },  // Actualiza estado al escribir
                label = { Text("Longitud") }  // Placeholder del campo
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para calcular con coordenadas manuales
            Button(onClick = {
                // Convierte texto a números, maneja valores inválidos
                val manualLat = latInput.text.toDoubleOrNull()
                val manualLng = lngInput.text.toDoubleOrNull()

                // Solo calcula si ambos valores son válidos
                if (manualLat != null && manualLng != null) {
                    distance = haversine(
                        manualLat, manualLng,  // Coordenadas manuales
                        AppConstants.DISTRIBUIDORA_LAT,  // Distribuidora fija
                        AppConstants.DISTRIBUIDORA_LNG
                    )
                }
            }) {
                Text("Calcular con coordenadas ingresadas")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para volver a la pantalla anterior
            Button(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}