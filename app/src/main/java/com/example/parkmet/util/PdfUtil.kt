package com.example.parkmet.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.example.parkmet.data.Parking
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfUtil {
    fun saveQrCodeToPdf(bitmap: Bitmap, licensePlate: String, parking: Parking): String {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) // Base download file
        val fileName = "QR_${licensePlate}_${System.currentTimeMillis()}.pdf"
        val pdfFile = File(downloadsDir, fileName)

        val outputStream = pdfFile.outputStream()
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Convert Bitmap to byte array
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageData = ImageDataFactory.create(stream.toByteArray())
        val image = Image(imageData).setAutoScale(true)

        // Format current date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateString = dateFormat.format(Date())

        // Add text
        document.add(Paragraph("License Plate: $licensePlate"))
        document.add(Paragraph("Entry Date: $dateString"))
        document.add(Paragraph("Parking: ${parking.name}"))

        // Add QR code image
        document.add(image)

        document.close()
        return pdfFile.absolutePath
    }
}
