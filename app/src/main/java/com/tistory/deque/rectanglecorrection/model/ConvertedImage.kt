package com.tistory.deque.rectanglecorrection.model

import android.graphics.Bitmap

data class ConvertedImage(var bitmap:Bitmap) {
    var convertedBitmapResizingRate: Double = 0.0
    var convertedBitmapOnCanvasElements = IntArray(0)
}