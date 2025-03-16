package com.example.parkmet.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder


object QrCodeUtil {
    // Generate QR code bitmap from string content
    fun generateQrCode(content: String, size: Int = 512): Bitmap {
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, size, size)
    }
}
