package com.example.parkmet

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.*
import com.example.parkmet.data.Parking
import com.example.parkmet.data.ParkingDatabase
import com.example.parkmet.navigation.Screen
import com.example.parkmet.ui.*
import com.example.parkmet.ui.theme.ParkMetTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParkMetTheme {
                val navController = rememberNavController()
                val database = ParkingDatabase.getInstance(applicationContext)
                val parkingDao = database.parkingDao()


                lifecycleScope.launch(Dispatchers.IO) {
                    val existingParkings = parkingDao.getAllParkings().first()

                    if (existingParkings.isEmpty()) {
                        val sampleParkings = listOf(
                            Parking(name = "Central Parking", totalSlots = 100, availableSlots = 100),
                            Parking(name = "North Parking", totalSlots = 50, availableSlots = 50),
                            Parking(name = "South Parking", totalSlots = 75, availableSlots = 75)
                        )
                        sampleParkings.forEach { parkingDao.insertParking(it) }
                    }
                }

                NavHost(navController = navController, startDestination = Screen.Login.route) {
                    composable(Screen.Login.route) {
                        LoginScreen(parkingDao = parkingDao, onLoginSuccess = {
                            navController.navigate(Screen.MainMenu.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        })
                    }
                    composable(Screen.MainMenu.route) {
                        MainMenuScreen(
                            onArrivalClick = { navController.navigate(Screen.Arrival.route) },
                            onDepartureClick = { navController.navigate(Screen.Departure.route) },
                            onBackupsClick = { navController.navigate(Screen.Backups.route) },
                            onStatusClick = { navController.navigate(Screen.Status.route) }
                        )
                    }
                    composable(Screen.Arrival.route) { ArrivalScreen(parkingDao, context = applicationContext) }
                    composable(Screen.Departure.route) { /* TODO: Departure Screen */ }
                    composable(Screen.Backups.route) { /* TODO: Backups Screen */ }
                    composable(Screen.Status.route) { /* TODO: Status Screen */ }
                }
            }
        }
    }
}
