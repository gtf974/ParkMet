package com.example.parkmet.util

import android.content.Context
import android.graphics.Bitmap
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import java.io.ByteArrayOutputStream
import java.io.File

object PdfUtil {
    // Save bitmap as PDF file and return the file path
    fun saveQrCodeToPdf(bitmap: Bitmap, context: Context, filename: String): String {
        val pdfDir = File(context.filesDir, "qrcodes")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val pdfFile = File(pdfDir, "$filename.pdf")
        val pdfWriter = PdfWriter(pdfFile)
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Convert Bitmap to byte array
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bitmapData = stream.toByteArray()

        // Create iText Image from Bitmap data
        val imageData = ImageDataFactory.create(stream.toByteArray())
        val image = Image(imageData).setAutoScale(true)

        document.add(image)
        document.close()

        return pdfFile.absolutePath
    }
}
