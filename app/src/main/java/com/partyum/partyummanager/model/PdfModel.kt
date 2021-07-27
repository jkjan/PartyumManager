package com.partyum.partyummanager.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class PdfModel {
    companion object {
        fun viewToBitmap(view: View): Bitmap {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            return bitmap
        }

        fun addBitmapToPdf(bitmap: Bitmap, savePath: String): File? {
            return try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val img: Image = Image.getInstance(stream.toByteArray())
                img.setAlignment(Image.ALIGN_CENTER or Image.ALIGN_TOP)
                val document = Document(img)
                val file = File(savePath)

                file.parentFile?.mkdirs()
                val d = file.createNewFile()
                Log.i("pdf model", "$d")

                PdfWriter.getInstance(
                    document,
                    FileOutputStream(savePath)
                )

                document.open()
                img.setAbsolutePosition(0F, 0F)
                document.add(img)
                document.close()

                Log.i("pdf model", "PDF 파일을 생성하였습니다. 경로: ${savePath}")
                file
            } catch (e: Exception) {
                Log.e("pdf model", "PDF 이미지 추가에 실패하였습니다. 경로: ${savePath}")
                e.printStackTrace()
                null
            }
        }

        fun generatePdf(view: View, savePath: String): File? {
            val bitmap = viewToBitmap(view)
            return addBitmapToPdf(bitmap, savePath)
        }
    }
}