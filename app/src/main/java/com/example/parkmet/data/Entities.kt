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
    val entryTime: Long,  // timestamp in milliseconds
    val qrCodePath: String
)

@Entity
data class VehicleExit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val entryId: Int,
    val exitTime: Long,  // timestamp in milliseconds
    val durationMinutes: Long,
    val cost: Double
)

@Entity
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String,
    val timestamp: Long,  // timestamp in milliseconds
    val details: String
)
