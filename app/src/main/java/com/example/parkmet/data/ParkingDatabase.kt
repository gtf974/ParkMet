package com.example.parkmet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Defines entities included in the database and version
@Database(
    entities = [Parking::class, VehicleEntry::class, User::class],
    version = 2,
    exportSchema = false
)
abstract class ParkingDatabase : RoomDatabase() {

    // Abstract method to access DAO
    abstract fun parkingDao(): ParkingDao

    companion object {
        // Singleton instance of the database
        @Volatile
        private var INSTANCE: ParkingDatabase? = null

        // Method to obtain or create the database instance
        fun getInstance(context: Context): ParkingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParkingDatabase::class.java,
                    "parkmet_db" // Name of the SQLite database file
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
