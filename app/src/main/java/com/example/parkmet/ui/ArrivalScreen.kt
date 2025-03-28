package com.example.parkmet.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmet.data.Parking
import com.example.parkmet.data.ParkingDao
import com.example.parkmet.ui.components.AppScaffold
import com.example.parkmet.ui.components.IconTextButton
import com.example.parkmet.ui.viewmodel.ArrivalViewModel
import com.example.parkmet.util.ToastUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ArrivalScreen(parkingDao: ParkingDao, context: Context) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { ArrivalViewModel(parkingDao, context) }

    var parkings by remember { mutableStateOf<List<Parking>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedParking by remember { mutableStateOf<Parking?>(null) }

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            ToastUtil.showToast(context, it)
            viewModel.message = null
        }
    }

    LaunchedEffect(Unit) {
        parkingDao.getAllParkings().collectLatest {
            parkings = it
        }
    }

    AppScaffold(title = "Vehicle Arrival", icon= Icons.Filled.QrCode) { modifier ->
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = viewModel.licensePlate,
                onValueChange = { viewModel.licensePlate = it },
                label = { Text("License Plate") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for Parking selection
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true }
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
                                viewModel.parkingName = parking.name
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            IconTextButton(
                text= "Generate QR Code",
                icon= Icons.Filled.QrCode,
                isTakingFullWith = false,
                onClick = {
                    coroutineScope.launch {
                        viewModel.onGenerateQrCodeClick()
                    }
                }
            )
        }
    }
}
