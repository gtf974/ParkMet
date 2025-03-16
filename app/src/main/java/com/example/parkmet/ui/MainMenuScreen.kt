package com.example.parkmet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmet.ui.components.AppScaffold

@Composable
fun MainMenuScreen(
    modifier: Modifier = Modifier,
    onArrivalClick: () -> Unit,
    onDepartureClick: () -> Unit,
    onBackupsClick: () -> Unit,
    onStatusClick: () -> Unit
) {
    AppScaffold(title = "ParkMet Main Menu") { contentModifier ->
        Column(
            modifier = contentModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onArrivalClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text("Arrival")
            }

            Button(
                onClick = onDepartureClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text("Departure")
            }

            Button(
                onClick = onBackupsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text("Backups")
            }

            Button(
                onClick = onStatusClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text("Status")
            }
        }
    }
}
