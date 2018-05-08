package dev.agenda.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*


fun loadImage(directory: File?, id: String?, photoByte: ByteArray?): String {
    /*val bitmap = photoByte?.size?.let {
        BitmapFactory.decodeByteArray(photoByte, 0, it)
    }*/
    val bitmap = shrinkBitmap(photoByte,256,256)
    val tmpFile = File((directory?.path) +
            "/tmp" + id + ".png")
    try {
        val fOutStream = FileOutputStream(tmpFile)

        // Writing the bitmap to the temporary
        // file as png file
        bitmap?.compress(Bitmap.CompressFormat.PNG, 10, fOutStream)

        // Flush the FileOutputStream
        fOutStream.flush()

        // Close the FileOutputStream
        fOutStream.close()

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return tmpFile.path
}

fun shrinkBitmap(file: ByteArray?, width: Int, height: Int): Bitmap? {

    val bmpFactoryOptions = BitmapFactory.Options()
    bmpFactoryOptions.inJustDecodeBounds = true
    var bitmap = file?.size?.let { BitmapFactory.decodeByteArray(file,0, it, bmpFactoryOptions) }

    val heightRatio = Math.ceil((bmpFactoryOptions.outHeight / height.toFloat()).toDouble()).toInt()
    val widthRatio = Math.ceil((bmpFactoryOptions.outWidth / width.toFloat()).toDouble()).toInt()

    if (heightRatio > 1 || widthRatio > 1) {
        if (heightRatio > widthRatio) {
            bmpFactoryOptions.inSampleSize = heightRatio
        } else {
            bmpFactoryOptions.inSampleSize = widthRatio
        }
    }

    bmpFactoryOptions.inJustDecodeBounds = false
    bitmap = file?.size?.let { BitmapFactory.decodeByteArray(file,0, it, bmpFactoryOptions) }
    return bitmap
}