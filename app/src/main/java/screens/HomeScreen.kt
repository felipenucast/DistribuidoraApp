package com.example.distribuidoraapp.screens

//importaciones para permisos y ubicación
import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

////importaciones de Compose para UI
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
//utilidad personalizada para Firebase
import com.example.distribuidoraapp.utils.FirebaseLocationHelper
//servicios de localización de Google
import com.google.android.gms.location.*

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    onLogout: () -> Unit,               //navegación para cerrar sesión
    onOpenCalculadora: () -> Unit       //navegación para calculadora de costos
) {
    val context = LocalContext.current
    //cliente de google para servicios de ubicación
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    //almacenamiento de ubicación de usuario
    var lat by remember { mutableStateOf<Double?>(null) }  //latitud actual
    var lng by remember { mutableStateOf<Double?>(null) }   //longitud actual
    var hasPermission by remember { mutableStateOf(false) }  //estado permisos de ubicación

    //solicitar permisos de ubicación al usuario
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (!hasPermission) {
                Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,     //ubicación GPS
                Manifest.permission.ACCESS_COARSE_LOCATION    //ubicación red
            )
        )
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lng = location.longitude
                    FirebaseLocationHelper.uploadLocation(lat!!, lng!!)
                } else {
                    val locationRequest = LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY, 5000L
                    ).build()
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                val loc = result.lastLocation
                                if (loc != null) {
                                    //sube nueva ubicación a Firebase
                                    lat = loc.latitude
                                    lng = loc.longitude
                                    FirebaseLocationHelper.uploadLocation(lat!!, lng!!)
                                    fusedLocationClient.removeLocationUpdates(this)
                                }
                            }
                        },
                        Looper.getMainLooper()
                    )
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¡Bienvenido!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (lat != null && lng != null) {
                Text("Tu ubicación actual:")
                Text("Latitud: $lat")
                Text("Longitud: $lng")
            } else {
                Text("Obteniendo ubicación...") //mensaje a la espera de ubicación
            }

            Spacer(modifier = Modifier.height(32.dp))

            //botón abrir calculadora de costos
            Button(onClick = onOpenCalculadora, modifier = Modifier.fillMaxWidth()) {
                Text("Calculadora de costos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            //botón cerrar sesión
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
        }
    }
}
