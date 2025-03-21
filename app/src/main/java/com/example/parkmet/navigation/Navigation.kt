package com.example.parkmet.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object MainMenu : Screen("main_menu")
    object Arrival : Screen("arrival")
    object Departure : Screen("departure")
    object Backups : Screen("backups")
    object ManageParkings : Screen("manage_parkings")
}