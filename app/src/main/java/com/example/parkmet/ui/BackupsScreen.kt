package com.example.parkmet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.parkmet.data.Parking
import com.example.parkmet.data.ParkingDao
import com.example.parkmet.data.VehicleEntry
import com.example.parkmet.ui.components.AppScaffold
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BackupsScreen(parkingDao: ParkingDao) {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault()) }

    var parkings by remember { mutableStateOf<List<Parking>>(emptyList()) }
    var selectedParking by remember { mutableStateOf<Parking?>(null) }
    var entries by remember { mutableStateOf<List<VehicleEntry>>(emptyList()) }

    // Load parkings from Flow
    LaunchedEffect(Unit) {
        parkingDao.getAllParkings().collectLatest {
            parkings = it
            if (selectedParking == null && it.isNotEmpty()) {
                selectedParking = it.first()
            }
        }
    }

    // Observe entries when selected parking changes
    LaunchedEffect(selectedParking) {
        selectedParking?.let { parking ->
            parkingDao.getVehicleEntriesForParking(parking.id).collectLatest {
                entries = it
            }
        }
    }

    AppScaffold(title = "Backups", icon= Icons.AutoMirrored.Filled.List) { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (parkings.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedParking?.name ?: "Select Parking")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        parkings.forEach { parking ->
                            DropdownMenuItem(
                                text = { Text(parking.name) },
                                onClick = {
                                    selectedParking = parking
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries) { entry ->
                    val isExited = entry.exitTime != null
                    val duration = if (isExited) "${entry.durationMinutes} min" else "In progress"
                    val price = if (isExited) "${"%.2f".format(entry.cost)} €" else "Pending"
                    val entryDate = sdf.format(Date(entry.entryTime))
                    val exitDate = entry.exitTime?.let { sdf.format(Date(it)) } ?: "—"

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text("Plate: ${entry.licensePlate}", style = MaterialTheme.typography.titleMedium)
                            Text("Entry: $entryDate")
                            Text("Exit: $exitDate")
                            Text("Duration: $duration")
                            Text("Price: $price")
                        }
                    }
                }
            }
        }
    }
}
