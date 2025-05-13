package com.example.parkmet.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.TimeToLeave
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.parkmet.data.ParkingDao
import com.example.parkmet.ui.components.AppScaffold
import com.example.parkmet.ui.components.IconTextButton
import com.example.parkmet.util.PriceUtil.calculateParkingPrice
import com.example.parkmet.util.ToastUtil.showToast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// View & Controller for Departures

@Composable
fun DepartureScreen(parkingDao: ParkingDao, context: Context) {
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            showToast(context, it)
            toastMessage = null
        }
    }

    // What to do when the QR code scan is completed
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result: ScanIntentResult ->
        result.contents?.let { scannedContent ->
            coroutineScope.launch {
                processDepartureQr(scannedContent, parkingDao) { message ->
                    toastMessage = message
                }
            }
        }
    }

    AppScaffold(title = "Vehicle Departure", icon= Icons.Filled.TimeToLeave) { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            IconTextButton(
                text= "Scan QR Code",
                icon= Icons.Filled.QrCodeScanner,
                isTakingFullWith = false,
                // launches the qr code scanner
                onClick = {
                scannerLauncher.launch(ScanOptions().apply {
                    setPrompt("Scan vehicle QR code")
                    setBeepEnabled(true)
                    setOrientationLocked(false)
                })
            })
        }
    }
}

// Process the scanned QRCode, generates a price
suspend fun processDepartureQr(
    qrContent: String,
    parkingDao: ParkingDao,
    onResult: (String) -> Unit
) {
    val parts = qrContent.split(";")
    if (parts.size != 3) {
        onResult("Invalid QR code format.")
        return
    }

    val (plate, timestampStr, parkingIdStr) = parts
    val entryTime = timestampStr.toLongOrNull()
    val parkingId = parkingIdStr.toIntOrNull()

    if (entryTime == null || parkingId == null) {
        onResult("Invalid data in QR code.")
        return
    }

    val vehicleEntry = parkingDao.getAllVehicleEntries().first()
        .find {
            it.licensePlate == plate &&
                    it.entryTime == entryTime &&
                    it.parkingId == parkingId &&
                    it.exitTime == null
        }

    if (vehicleEntry == null) {
        onResult("No matching active vehicle entry found.")
        return
    }

    val now = System.currentTimeMillis()
    val durationMillis = now - vehicleEntry.entryTime
    val durationMinutes = durationMillis / (1000 * 60)
    val price = calculateParkingPrice(durationMillis)

    val updated = vehicleEntry.copy(
        exitTime = now,
        durationMinutes = durationMinutes,
        cost = price
    )
    parkingDao.updateVehicleEntry(updated)
    parkingDao.incrementSlot(parkingId)

    onResult("Departure processed. Duration: ${durationMinutes} min, Price: ${"%.2f".format(price)}€")
}

