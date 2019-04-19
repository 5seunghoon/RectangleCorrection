package com.tistory.deque.rectanglecorrection.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import com.tistory.deque.rectanglecorrection.rect.RectCorrectionActivity
import com.tistory.deque.rectanglecorrection.util.EzLogger
import com.tistory.deque.rectanglecorrection.util.getRealPath
import java.io.FileNotFoundException
import java.io.IOException
import java.util.ArrayList

data class BaseImage(var originalImageUri: Uri) {

    val width: Int
        get() = bitmap?.width ?: 0

    val height: Int
        get() = bitmap?.height ?: 0

    var canvasOnWidth: Int? = null
    var canvasOnHeight: Int? = null
    var canvasOnWidthPos: Int? = null
    var canvasOnHeightPos: Int? = null

    var canvasOnResizingRate: Double? = null

    private var bitmap: Bitmap? = null
    fun getBitmap(context: Context): Bitmap? {
        return if (bitmap != null) bitmap
        else {
            val path = originalImageUri.getRealPath(context.contentResolver)
            EzLogger.d("Preview.kt, getBitmap(), originalImageUri : $originalImageUri, path : $path")
            val rotation = ExifInterface(path)
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            bitmap = imageUriToBitmap(RectCorrectionActivity.bitmapMaxSize, this.originalImageUri, context, rotation)
            return bitmap
        }

    }
    /**
     * 비트맵을 어느 좌표에 어떤 크기로 그릴지 결정
     * @return bitmapPosWidth, bitmapPosHeight, bitmapWidth, bitmapHeight
     */
    fun getResizedBitmapElements(canvasWidth: Int, canvasHeight: Int): ArrayList<Int> {
        val bitmapRate: Double = width / height.toDouble()
        val canvasRate: Double = canvasWidth.toDouble() / canvasHeight.toDouble()

        EzLogger.d("""
            bitmapRate: $bitmapRate, canvasRate : $canvasRate,
            bitmapWidth : $width, bitmapHeight : $height
            canvasWidth: $canvasWidth, canvasHeight : $canvasHeight
        """.trimIndent())

        val elements = ArrayList<Int>()

        EzLogger.d("getResizedBitmapElements canvas w, h : $canvasWidth, $canvasHeight")

        if (bitmapRate >= canvasRate && width >= canvasWidth) { // w > h
            EzLogger.d("getResizedBitmapElements case 1")

            canvasOnWidthPos = 0
            canvasOnHeightPos = (canvasHeight - (canvasWidth * (1 / bitmapRate)).toInt()) / 2
            canvasOnWidth = canvasWidth
            canvasOnHeight = (canvasWidth * (1 / bitmapRate)).toInt()

            canvasOnResizingRate = width.toDouble() / canvasWidth.toDouble()

        } else if (bitmapRate < canvasRate && height >= canvasHeight) { // w < h
            EzLogger.d("getResizedBitmapElements case 2")

            canvasOnWidthPos = (canvasWidth - (canvasHeight * bitmapRate).toInt()) / 2
            canvasOnHeightPos = 0
            canvasOnWidth = (canvasHeight * bitmapRate).toInt()
            canvasOnHeight = canvasHeight

            canvasOnResizingRate = height.toDouble() / canvasHeight.toDouble()

        } else {
            EzLogger.d("getResizedBitmapElements case 3")

            canvasOnWidthPos = (canvasWidth - width) / 2
            canvasOnHeightPos = (canvasHeight - height) / 2
            canvasOnWidth = width
            canvasOnHeight = height
            canvasOnResizingRate = 1.0

        }

        elements.add(canvasOnWidthPos ?: 0)
        elements.add(canvasOnHeightPos ?: 0)
        elements.add(canvasOnWidth ?: 0)
        elements.add(canvasOnHeight ?: 0)

        return elements
    }

    private fun imageUriToBitmap(maxSize: Int, imageUri: Uri, context: Context, rotation: Int?): Bitmap? {
        EzLogger.d("rotation : $rotation")
        var bitmap: Bitmap? = null
        var resizedBitmap: Bitmap? = null
        val width: Int
        val height: Int
        val rate: Double

        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                ?: return null
            width = bitmap.width
            height = bitmap.height
            rate = width.toDouble() / height.toDouble()

            resizedBitmap = if (rate > 1 && width > maxSize) { // w > h
                EzLogger.d("RATE : $rate , W : $maxSize , H : ${(maxSize * (1 / rate)).toInt()}")
                Bitmap.createScaledBitmap(bitmap, maxSize, (maxSize * (1 / rate)).toInt(), true)
            } else if (rate <= 1 && height > maxSize) { // h > w
                EzLogger.d("RATE : $rate , W : ${(maxSize * rate).toInt()} , H : $maxSize")
                Bitmap.createScaledBitmap(bitmap, (maxSize * rate).toInt(), maxSize, true)
            } else {
                bitmap
            }
            EzLogger.d("URI -> Bitmap success : URI : $imageUri")
        } catch (e: FileNotFoundException) {
            EzLogger.d("URI -> Bitmap : URI File not found$imageUri")
            e.printStackTrace()
        } catch (e: IOException) {
            EzLogger.d("URI -> Bitmap : IOException$imageUri")
            e.printStackTrace()
        }

        if (rotation != null && resizedBitmap != null) {
            try {
                val matrix = Matrix()
                when (rotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        matrix.postRotate(90f)
                        resizedBitmap = Bitmap.createBitmap(
                            resizedBitmap,
                            0,
                            0,
                            resizedBitmap.width,
                            resizedBitmap.height,
                            matrix,
                            true
                        )
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        matrix.postRotate(180f)
                        resizedBitmap = Bitmap.createBitmap(
                            resizedBitmap,
                            0,
                            0,
                            resizedBitmap.width,
                            resizedBitmap.height,
                            matrix,
                            true
                        )
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        matrix.postRotate(270f)
                        resizedBitmap = Bitmap.createBitmap(
                            resizedBitmap,
                            0,
                            0,
                            resizedBitmap.width,
                            resizedBitmap.height,
                            matrix,
                            true
                        )
                    }
                }
            } catch (e: OutOfMemoryError) {
                EzLogger.d("out of memory error ...")
                e.printStackTrace()
            }
        }

        return resizedBitmap
    }

}