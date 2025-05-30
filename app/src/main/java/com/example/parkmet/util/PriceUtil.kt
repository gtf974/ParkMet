package com.example.parkmet.util

object PriceUtil {
    // Calculates the price based on the duration (1.95 par tranche de 2 heures)
    fun calculateParkingPrice(durationMillis: Long): Double {
        val durationHours = durationMillis / (1000 * 60 * 60).toDouble()
        val stepSize = 2.0
        val basePrice = 1.95
        val extraSteps = kotlin.math.ceil((durationHours - stepSize) / stepSize).coerceAtLeast(0.0)
        return basePrice + (extraSteps * basePrice)
    }
}