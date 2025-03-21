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
import com.example.parkmet.data.User
import com.example.parkmet.navigation.Screen
import com.example.parkmet.ui.*
import com.example.parkmet.ui.theme.ParkMetTheme
import com.example.parkmet.util.HashUtil
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


                //lifecycleScope.launch(Dispatchers.IO) {
                    //database.clearAllTables()
                    //parkingDao.insertParking(Parking(id = 1, name = "Parking Nord", availableSlots = 70, totalSlots = 70))
                    //parkingDao.insertParking(Parking(id = 2, name = "Parking Sud", availableSlots = 70, totalSlots = 70))
                    //parkingDao.insertParking(Parking(id = 3, name = "Parking Est", availableSlots = 70, totalSlots = 70))
                    //parkingDao.insertParking(Parking(id = 4, name = "Parking Ouest", availableSlots = 70, totalSlots = 70))
                    //parkingDao.insertUser(User(username = "gaetan", passwordHash = HashUtil.sha256("changemePLS")))
                //}

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
                            onManageParkingsClick = { navController.navigate(Screen.ManageParkings.route) }
                        )
                    }
                    composable(Screen.Arrival.route) { ArrivalScreen(parkingDao, context = applicationContext) }
                    composable(Screen.Departure.route) { /* TODO: Departure Screen */ }
                    composable(Screen.Backups.route) { /* TODO: Backups Screen */ }
                    composable(Screen.ManageParkings.route) { ManageParkingsScreen(parkingDao) }
                }
            }
        }
    }
}
