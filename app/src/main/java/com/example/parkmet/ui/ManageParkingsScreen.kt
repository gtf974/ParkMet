package com.example.parkmet.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmet.data.Parking
import com.example.parkmet.data.ParkingDao
import com.example.parkmet.ui.components.AppScaffold
import com.example.parkmet.ui.components.IconTextButton
import com.example.parkmet.ui.theme.Background
import com.example.parkmet.util.ToastUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// View & Controller for Manage Parkings page

@Composable
fun ManageParkingsScreen(parkingDao: ParkingDao, context: Context) {
    var parkings by remember { mutableStateOf<List<Parking>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newSlots by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedParking by remember { mutableStateOf<Parking?>(null) }
    var editError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(editError) {
        editError?.let {
            ToastUtil.showToast(context, it)
            editError = null
        }
    }


    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            parkingDao.getAllParkings().collectLatest {
                parkings = it
            }
        }
    }

    AppScaffold(title = "Manage Parkings", icon= Icons.Filled.LocalParking) { modifier ->
        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconTextButton(
                text= "Add Parking",
                isTakingFullWith = false,
                icon= Icons.Filled.LocalParking,
                onClick = { showDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(parkings) { parking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.elevatedCardElevation()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Name: ${parking.name}")
                            Text("Total Slots: ${parking.totalSlots}")
                            Text("Available: ${parking.availableSlots}")
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom= 8.dp, end= 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,

                        ) {
                            Button(
                                onClick = {
                                    // trigger edit dialog
                                    selectedParking = parking
                                    newName = parking.name
                                    newSlots = parking.totalSlots.toString()
                                    showEditDialog = true
                                }
                            ) {
                                Text("Edit", color= Background)
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        parkingDao.deleteParking(parking)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    // Pop-up for adding parking
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Parking") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Parking Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newSlots,
                        onValueChange = { newSlots = it },
                        label = { Text("Total Slots") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val total = newSlots.toIntOrNull()
                    if (!newName.isBlank() && total != null) {
                        coroutineScope.launch {
                            parkingDao.insertParking(
                                Parking(name = newName, totalSlots = total, availableSlots = total)
                            )
                            newName = ""
                            newSlots = ""
                            showDialog = false
                        }
                    }
                }) {
                    Text("Add", color= Background)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Pop-up for editing parking
    if (showEditDialog && selectedParking != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                editError = null
            },
            title = { Text("Edit Parking") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Parking Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newSlots,
                        onValueChange = { newSlots = it },
                        label = { Text("Total Slots") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val total = newSlots.toIntOrNull()
                    val parking = selectedParking

                    if (total == null || parking == null || newName.isBlank()) {
                        editError = "Please enter valid values."
                        return@Button
                    }

                    val occupied = parking.totalSlots - parking.availableSlots

                    if (total < occupied) {
                        editError = "Cannot set total slots to $total. $occupied slot(s) is(are) currently in use."
                    } else {
                        coroutineScope.launch {
                            parkingDao.updateParking(
                                parking.copy(
                                    name = newName,
                                    totalSlots = total,
                                    availableSlots = total - occupied
                                )
                            )
                            showEditDialog = false
                            selectedParking = null
                            newName = ""
                            newSlots = ""
                            editError = null
                        }
                    }
                }) {
                    Text("Save", color = Background)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


}
