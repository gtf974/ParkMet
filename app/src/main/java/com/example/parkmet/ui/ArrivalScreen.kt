package com.example.parkmet.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmet.data.Parking
import com.example.parkmet.data.ParkingDao
import com.example.parkmet.ui.components.AppScaffold
import com.example.parkmet.ui.viewmodel.ArrivalViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ArrivalScreen(parkingDao: ParkingDao, context: Context) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { ArrivalViewModel(parkingDao, context) }

    var parkings by remember { mutableStateOf<List<Parking>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedParking by remember { mutableStateOf<Parking?>(null) }

    LaunchedEffect(Unit) {
        parkingDao.getAllParkings().collectLatest {
            parkings = it
        }
    }

    AppScaffold(title = "Vehicle Arrival") { modifier ->
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = viewModel.licensePlate,
                onValueChange = { viewModel.licensePlate = it },
                label = { Text("License Plate") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for Parking selection
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedParking?.name ?: "Select Parking")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    parkings.forEach { parking ->
                        DropdownMenuItem(
                            text = { Text(parking.name) },
                            onClick = {
                                selectedParking = parking
                                expanded = false
                                viewModel.parkingName = parking.name
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.onGenerateQrCodeClick()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate QR Code")
            }

            if (viewModel.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(viewModel.message, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
