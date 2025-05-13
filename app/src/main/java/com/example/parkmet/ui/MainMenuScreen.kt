package com.example.parkmet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TimeToLeave
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkmet.session.Session
import com.example.parkmet.ui.components.AppScaffold
import com.example.parkmet.ui.components.IconTextButton
import com.example.parkmet.ui.theme.PrimaryDark

// View & Controller for the main menu page

@Composable
fun MainMenuScreen(navController: NavController, onLogout: () -> Unit) {
    AppScaffold(title = "Main Menu", icon= Icons.Filled.FolderCopy) { modifier ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Main vertical menu
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconTextButton("Arrival", Icons.Filled.QrCode) {
                    navController.navigate("arrival") // Goes to arrival screen
                }

                IconTextButton("Departure", Icons.Filled.TimeToLeave) {
                    navController.navigate("departure") // Goes to departure screen
                }

                IconTextButton("Manage Parkings", Icons.Filled.LocalParking) {
                    navController.navigate("manage_parkings") // Goes to manage_parking screen
                }

                IconTextButton("Backups", Icons.AutoMirrored.Filled.List) {
                    navController.navigate("backups") // Goes to backups screen
                }
            }

            // Settings + Logout in bottom right
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                IconButton(onClick = {
                    navController.navigate("manage_user")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Manage User",
                        tint = PrimaryDark
                    )
                }

                IconButton(onClick = {
                    onLogout()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = PrimaryDark
                    )
                }
            }
        }
    }
}
