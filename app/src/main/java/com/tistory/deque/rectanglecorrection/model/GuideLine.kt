package com.tistory.deque.rectanglecorrection.model

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class GuideLine {

    var guideLeftTopPair = Pair(0.0, 0.0)
    var guideLeftBottomPair = Pair(0.0, 0.0)
    var guideRightTopPair = Pair(0.0, 0.0)
    var guideRightBottomPair = Pair(0.0, 0.0)

    val clickRadius = 50f

    var guideLineRect: Rect? = null
    var guideLineColor: Int = Color.RED

    val guideRectWidth = 5f
    val guideLineWidth = 2f
    val guideCircleRadius = 15f

    val guideRectPaint = Paint().apply {
        strokeWidth = guideRectWidth
        color = guideLineColor
        style = Paint.Style.STROKE
    }

    val guideLinePaint = Paint().apply {
        strokeWidth = guideLineWidth
        color = guideLineColor
    }
    val guideCirclePaint = Paint().apply {
        color = guideLineColor
    }

    fun initPointPair(element: IntArray) {
        if (guideLeftTopPair.isAllZero() && guideLeftBottomPair.isAllZero()
            && guideRightTopPair.isAllZero() && guideRightBottomPair.isAllZero()
        ) {
            val bitmapOnCanvasWidth = element[2]
            val bitmapOnCanvasHeight = element[3]
            val guideLeft = element[0] + bitmapOnCanvasWidth * 0.25
            val guideRight = element[0] + bitmapOnCanvasWidth * 0.75
            val guideTop = element[1] + bitmapOnCanvasHeight * 0.25
            val guideBottom = element[1] + bitmapOnCanvasHeight * 0.75

            guideLeftTopPair = Pair(guideLeft, guideTop)
            guideLeftBottomPair = Pair(guideLeft, guideBottom)
            guideRightTopPair = Pair(guideRight, guideTop)
            guideRightBottomPair = Pair(guideRight, guideBottom)
        }
    }

    fun whatClicked(x: Int, y: Int): GuidePoint {
        Pair(x, y).let {
            return when {
                guideLeftTopPair.isClicked(it) -> GuidePoint.LEFT_TOP
                guideLeftBottomPair.isClicked(it) -> GuidePoint.LEFT_BOTTOM
                guideRightTopPair.isClicked(it) -> GuidePoint.RIGHT_TOP
                guideRightBottomPair.isClicked(it) -> GuidePoint.RIGHT_BOTTOM
                else -> GuidePoint.NONE
            }
        }
    }

    private fun Pair<Double, Double>.isAllZero() = (first == 0.0 && second == 0.0)

    private fun Pair<Double, Double>.isClicked(clicked: Pair<Int, Int>): Boolean {
        return (clicked.first <= this.first + clickRadius && clicked.first >= this.first - clickRadius &&
                clicked.second <= this.second + clickRadius && clicked.second >= this.second - clickRadius)
    }

}