package com.example.parkmet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parkmet.data.ParkingDatabase
import com.example.parkmet.data.User
import com.example.parkmet.session.Session
import com.example.parkmet.ui.*
import com.example.parkmet.ui.theme.ParkMetTheme
import com.example.parkmet.util.HashUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParkMetTheme (dynamicColor = false) {
                val context = LocalContext.current
                val db = ParkingDatabase.getInstance(context)
                val parkingDao = db.parkingDao()

                LaunchedEffect(Unit) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        if(!parkingDao.doesAtLeastOneUserExist()){
                            parkingDao.insertUser(User(username = "admin", passwordHash = HashUtil.sha256("admin")))
                        }
                    }
                }


                // Track authentication state
                var isAuthenticated by remember { mutableStateOf(Session.currentUser != null) }

                if (isAuthenticated) {
                    val navController = rememberNavController()

                    // Authenticated navigation graph
                    NavHost(navController = navController, startDestination = "main_menu") {
                        composable("main_menu") {
                            MainMenuScreen(
                                navController = navController,
                                onLogout = {
                                    Session.currentUser = null
                                    isAuthenticated = false
                                }
                            )
                        }
                        composable("arrival") {
                            ArrivalScreen(parkingDao = parkingDao, context = context)
                        }
                        composable("departure") {
                            DepartureScreen(parkingDao = parkingDao, applicationContext)
                        }
                        composable("manage_parkings") {
                            ManageParkingsScreen(parkingDao = parkingDao, applicationContext)
                        }
                        composable("backups") {
                            BackupsScreen(parkingDao = parkingDao)
                        }
                        composable("manage_user") {
                            ManageUserScreen(parkingDao = parkingDao, applicationContext)
                        }
                    }
                } else {
                    // Login screen shown when not authenticated
                    LoginScreen(
                        parkingDao = parkingDao,
                        context = applicationContext,
                        onLoginSuccess = {
                            isAuthenticated = true
                        }
                    )
                }
            }
        }
    }
}
