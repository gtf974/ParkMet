package com.example.parkmet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.parkmet.data.*
import com.example.parkmet.util.PdfUtil
import com.example.parkmet.util.QrCodeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ArrivalViewModel(private val parkingDao: ParkingDao, private val context: Context) : ViewModel() {
    var licensePlate by mutableStateOf("")
    var parkingName by mutableStateOf("")
    var message by mutableStateOf("")

    fun onGenerateQrCodeClick() {
        if (licensePlate.isBlank() || parkingName.isBlank()) {
            message = "All fields are required!"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val parking = parkingDao.getAllParkings()
                .collect { parkings ->
                    val foundParking = parkings.find { it.name == parkingName }

                    if (foundParking != null && foundParking.availableSlots > 0) {
                        // Decrement available slot
                        parkingDao.decrementSlot(foundParking.id)

                        // Generate QR code content
                        val qrContent = "$licensePlate;${System.currentTimeMillis()}"

                        // Generate QR code image (ZXing)
                        val qrBitmap = QrCodeUtil.generateQrCode(qrContent)

                        // Save QR code to PDF
                        val pdfFilePath = PdfUtil.saveQrCodeToPdf(qrBitmap, context, licensePlate)

                        // Save entry record in database
                        val entry = VehicleEntry(
                            licensePlate = licensePlate,
                            parkingId = foundParking.id,
                            entryTime = System.currentTimeMillis(),
                            qrCodePath = pdfFilePath
                        )
                        parkingDao.insertVehicleEntry(entry)

                        // Log this operation
                        parkingDao.insertLog(
                            LogEntry(
                                action = "Arrival",
                                timestamp = System.currentTimeMillis(),
                                details = "Vehicle $licensePlate entered parking ${foundParking.name}"
                            )
                        )

                        message = "QR Code generated successfully!"
                    } else {
                        message = "Parking not found or full."
                    }
                }
        }
    }

    class Factory(
        private val parkingDao: ParkingDao,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ArrivalViewModel(parkingDao, context) as T
        }
    }
}
