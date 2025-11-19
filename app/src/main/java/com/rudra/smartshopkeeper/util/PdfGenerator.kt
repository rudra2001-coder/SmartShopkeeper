
package com.rudra.smartshopkeeper.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    fun generateInvoice(context: Context, sale: Sale, saleItems: List<SaleItem>) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // TODO: Add content to the PDF

        document.finishPage(page)

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "invoice_${sale.invoiceNumber}.pdf")
        try {
            val fos = FileOutputStream(file)
            document.writeTo(fos)
            document.close()
            fos.close()
            // TODO: Show a toast or notification to indicate success
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
