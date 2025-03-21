package com.example.parkmet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmet.data.*
import com.example.parkmet.util.PdfUtil
import com.example.parkmet.util.QrCodeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArrivalViewModel(
    private val parkingDao: ParkingDao,
    private val context: Context
) : ViewModel() {

    var licensePlate by mutableStateOf("")
    var parkingName by mutableStateOf("")
    var message by mutableStateOf("")

    fun onGenerateQrCodeClick() {
        if (licensePlate.isBlank() || parkingName.isBlank()) {
            message = "Please fill all fields"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val parkings = parkingDao.getAllParkings().first() // get data once
            val parking = parkings.find { it.name == parkingName }

            if (parking != null && parking.availableSlots > 0) {
                parkingDao.decrementSlot(parking.id)

                val timestamp = System.currentTimeMillis()
                val qrContent = "$licensePlate;$timestamp;${parking.id}"
                val qrBitmap = QrCodeUtil.generateQrCode(qrContent)

                val pdfPath = PdfUtil.saveQrCodeToPdf(qrBitmap, context, licensePlate, parking)

                val entry = VehicleEntry(
                    licensePlate = licensePlate,
                    parkingId = parking.id,
                    entryTime = timestamp,
                    qrCodePath = pdfPath
                )
                parkingDao.insertVehicleEntry(entry)

                parkingDao.insertLog(
                    LogEntry(
                        action = "Arrival",
                        timestamp = timestamp,
                        details = "Vehicle $licensePlate entered ${parking.name}"
                    )
                )

                withContext(Dispatchers.Main) {
                    message = "QR Code generated and saved!"
                }
            } else {
                withContext(Dispatchers.Main) {
                    message = "Parking not found or full."
                }
            }
        }
    }
}
