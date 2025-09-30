package com.example.distribuidoraapp.utils

object CostCalculator {
    fun calcularCosto(totalCompra: Int, distanciaKm: Double): Int {
        return when {
            totalCompra >= 50000 -> 0 // Despacho gratis dentro de 20km
            totalCompra in 25000..49999 -> (distanciaKm * 150).toInt()
            else -> (distanciaKm * 300).toInt()
        }
    }
}
