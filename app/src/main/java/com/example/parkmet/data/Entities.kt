package com.example.parkmet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Parking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val totalSlots: Int,
    val availableSlots: Int
)

@Entity
data class VehicleEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val licensePlate: String,
    val parkingId: Int,
    val entryTime: Long,  // when the vehicle entered
    val exitTime: Long? = null,  // when it left (null = still parked)
    val durationMinutes: Long? = null,  // calculated on departure
    val cost: Double? = null,  // calculated on departure
    val qrCodePath: String
)
