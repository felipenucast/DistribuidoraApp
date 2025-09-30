package com.example.distribuidoraapp.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseLocationHelper {

    fun uploadLocation(lat: Double, lng: Double) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("usuarios/$userId/ubicacion")

        val locationMap = mapOf(
            "latitud" to lat,
            "longitud" to lng
        )

        databaseRef.setValue(locationMap)
            .addOnSuccessListener {
                // Ubicación subida correctamente
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
