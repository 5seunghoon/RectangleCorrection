package com.tistory.deque.rectanglecorrection.util

import android.graphics.Bitmap
import java.util.ArrayList

/**
 * 비트맵을 캔버스의 어느 좌표에 어떤 크기로 그릴지 결정
 * @return (bitmapPosWidth, bitmapPosHeight, bitmapWidth, bitmapHeight), resizingRate
 */
fun Bitmap.getCanvasOnResizingBitmapElements(canvasWidth: Int, canvasHeight: Int): Pair<IntArray, Double> {
    val bitmapRate: Double = width / height.toDouble()
    val canvasRate: Double = canvasWidth.toDouble() / canvasHeight.toDouble()

    var canvasOnWidthPos = 0
    var canvasOnHeightPos = 0
    var canvasOnWidth = 0
    var canvasOnHeight = 0

    var canvasOnResizingRate: Double = 0.0

    EzLogger.d("""
            bitmapRate: $bitmapRate, canvasRate : $canvasRate,
            bitmapWidth : $width, bitmapHeight : $height
            canvasWidth: $canvasWidth, canvasHeight : $canvasHeight
        """.trimIndent())

    EzLogger.d("getResizedBitmapElements canvas w, h : $canvasWidth, $canvasHeight")

    if (bitmapRate >= canvasRate && width >= canvasWidth) { // w > h
        EzLogger.d("getResizedBitmapElements case 1")

        canvasOnWidthPos = 0
        canvasOnHeightPos = (canvasHeight - (canvasWidth * (1 / bitmapRate)).toInt()) / 2
        canvasOnWidth = canvasWidth
        canvasOnHeight = (canvasWidth * (1 / bitmapRate)).toInt()

        canvasOnResizingRate = canvasWidth.toDouble() / width.toDouble()

    } else if (bitmapRate < canvasRate && height >= canvasHeight) { // w < h
        EzLogger.d("getResizedBitmapElements case 2")

        canvasOnWidthPos = (canvasWidth - (canvasHeight * bitmapRate).toInt()) / 2
        canvasOnHeightPos = 0
        canvasOnWidth = (canvasHeight * bitmapRate).toInt()
        canvasOnHeight = canvasHeight

        canvasOnResizingRate = canvasHeight.toDouble() / height.toDouble()

    } else {
        EzLogger.d("getResizedBitmapElements case 3")

        canvasOnWidthPos = (canvasWidth - width) / 2
        canvasOnHeightPos = (canvasHeight - height) / 2
        canvasOnWidth = width
        canvasOnHeight = height
        canvasOnResizingRate = 1.0

    }

    val elements = intArrayOf(canvasOnWidthPos, canvasOnHeightPos, canvasOnWidth, canvasOnHeight)

    return Pair(elements, canvasOnResizingRate)
}