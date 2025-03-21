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

    // Retrieve all parking lots
    @Query("SELECT * FROM Parking ORDER BY name")
    fun getAllParkings(): Flow<List<Parking>>


    // Decrement available slots in the specified parking lot
    @Query("UPDATE Parking SET availableSlots = availableSlots - 1 WHERE id = :parkingId")
    suspend fun decrementSlot(parkingId: Int)

    // Increment available slots when a vehicle leaves
    @Query("UPDATE Parking SET availableSlots = availableSlots + 1 WHERE id = :parkingId")
    suspend fun incrementSlot(parkingId: Int)

    // Retrieve vehicle entry by ID
    @Query("SELECT * FROM VehicleEntry WHERE id = :entryId")
    suspend fun getVehicleEntryById(entryId: Int): VehicleEntry?

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

    // Retrieve all Vehicle logs
    @Query("SELECT * FROM VehicleEntry")
    fun getAllVehicleEntries(): Flow<List<VehicleEntry>>

    @Query("SELECT * FROM VehicleEntry WHERE parkingId = :parkingId ORDER BY entryTime DESC")
    fun getVehicleEntriesForParking(parkingId: Int): Flow<List<VehicleEntry>>

    // Update VehicleEntry log
    @Update
    suspend fun updateVehicleEntry(vehicleEntry: VehicleEntry)

    // Count every parked car with given plate (to check if the car is parked or not)
    @Query("SELECT COUNT(*) FROM VehicleEntry WHERE licensePlate = :plate AND exitTime IS NULL")
    suspend fun countByPlateActive(plate: String): Int

    // Actual check if the vehicle is parked
    suspend fun isVehicleParked(plate: String): Boolean {
        return countByPlateActive(plate) > 0
    }
}
