package com.example.parkmet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Definition of entities (Models)

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
    val entryTime: Long,
    val exitTime: Long? = null,
    val durationMinutes: Long? = null,
    val cost: Double? = null, // price
    val qrCodePath: String
)

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String
)
