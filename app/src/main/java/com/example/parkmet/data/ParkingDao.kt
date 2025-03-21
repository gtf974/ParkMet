package com.example.parkmet.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingDao {

    // Insert a new parking lot
    @Insert
    suspend fun insertParking(parking: Parking)

    // Insert a new vehicle entry
    @Insert
    suspend fun insertVehicleEntry(entry: VehicleEntry)

    // Insert a vehicle departure record
    @Insert
    suspend fun insertVehicleExit(exit: VehicleExit)

    // Log an action into the log history
    @Insert
    suspend fun insertLog(log: LogEntry)

    // Retrieve all parking lots
    @Query("SELECT * FROM Parking")
    fun getAllParkings(): Flow<List<Parking>>

    // Retrieve log history ordered by timestamp (most recent first)
    @Query("SELECT * FROM LogEntry ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<LogEntry>>

    // Decrement available slots in the specified parking lot
    @Query("UPDATE Parking SET availableSlots = availableSlots - 1 WHERE id = :parkingId")
    suspend fun decrementSlot(parkingId: Int)

    // Increment available slots when a vehicle leaves
    @Query("UPDATE Parking SET availableSlots = availableSlots + 1 WHERE id = :parkingId")
    suspend fun incrementSlot(parkingId: Int)

    // Retrieve vehicle entry by ID
    @Query("SELECT * FROM VehicleEntry WHERE id = :entryId")
    suspend fun getVehicleEntryById(entryId: Int): VehicleEntry?

    // User management methods:

    // Insert a new user
    @Insert
    suspend fun insertUser(user: User)

    // Login query
    @Query("SELECT * FROM User WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    // Delete parking
    @Delete
    suspend fun deleteParking(parking: Parking)

    // Update parking
    @Update
    suspend fun updateParking(parking: Parking)
}
