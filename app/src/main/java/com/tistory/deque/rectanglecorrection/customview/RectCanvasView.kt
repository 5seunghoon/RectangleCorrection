package com.tistory.deque.rectanglecorrection.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.model.BaseImage
import com.tistory.deque.rectanglecorrection.model.ConvertedImage
import com.tistory.deque.rectanglecorrection.model.GuideLine
import com.tistory.deque.rectanglecorrection.model.GuidePoint
import com.tistory.deque.rectanglecorrection.util.EzLogger
import com.tistory.deque.rectanglecorrection.util.getCanvasOnResizingBitmapElements

class RectCanvasView : View {
    init {
        initView()
    }

    constructor(context: Context) : super(context)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

    private fun initView() {}

    private val guideRectWidth = 5f
    private val guideLineWidth = 2f
    private val guideCircleRadius = 15f

    private var imageCanvas: Canvas? = null
    var baseImage: BaseImage? = null
    var convertedImage: ConvertedImage? = null

    private val guideLine = GuideLine()

    private var selectedGuidePoint = GuidePoint.NONE

    private var movePrevX: Int = 0
    private var movePrevY: Int = 0

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        imageCanvas = canvas

        canvas?.let {
            setBackgroundColor(ContextCompat.getColor(context, R.color.canvasBackgroundColor))
            //drawConvertedImage(it)
            drawBaseImage(it)
            drawGuideLine(it)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (guideLeft == 0.0 || guideRight == 0.0 || guideBottom == 0.0 || guideTop == 0.0) return
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> touchDown(event)
            MotionEvent.ACTION_MOVE -> touchMove(event)
            MotionEvent.ACTION_UP -> touchUp(event)
        }
        return true
    }

    private fun touchDown(event: MotionEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()
        Pair(x, y).let {
            selectedGuidePoint = when {
                guideLine.guideLeftTopPair.

                it.isClicked(guideLeft.toInt(), guideTop.toInt()) -> GuidePoint.LEFT_TOP
                it.isClicked(guideLeft.toInt(), guideBottom.toInt()) -> GuidePoint.LEFT_BOTTOM
                it.isClicked(guideRight.toInt(), guideTop.toInt()) -> GuidePoint.RIGHT_TOP
                it.isClicked(guideRight.toInt(), guideBottom.toInt()) -> GuidePoint.RIGHT_BOTTOM
                else -> GuidePoint.NONE
            }
        }
        if (selectedGuidePoint != GuidePoint.NONE) {
            movePrevX = x
            movePrevY = y
        }
    }

    private fun touchMove(event: MotionEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (selectedGuidePoint) {
            GuidePoint.LEFT_TOP -> TODO()
            GuidePoint.LEFT_BOTTOM -> TODO()
            GuidePoint.RIGHT_TOP -> TODO()
            GuidePoint.RIGHT_BOTTOM -> TODO()
            GuidePoint.NONE -> return
        }

        movePrevX = x
        movePrevY = y
    }

    private fun drawBaseImage(canvas: Canvas) {
        baseImage?.let {
            val resizingElementsPair =
                it.getBitmap(this.context)?.getCanvasOnResizingBitmapElements(this.width, this.height) ?: return
            it.convertedBitmapOnCanvasElements = IntArray(4) { i -> resizingElementsPair.first[i] }
            it.convertedBitmapResizingRate = resizingElementsPair.second
            resizingElementsPair.first.let { element ->
                val bitmapRect = Rect(element[0], element[1], element[0] + element[2], element[1] + element[3])
                EzLogger.d("draw converted bitmap, rect : $bitmapRect")
                EzLogger.d("base converted bitmap canvas on rate : ${resizingElementsPair.second}")
                canvas.drawBitmap(it.getBitmap(this.context) ?: return, null, bitmapRect, null)

                guideLine.initPointPair(it.convertedBitmapOnCanvasElements)
            }
        }
    }

    private fun drawGuideLine(canvas: Canvas) {
        guideLine.let {

            // 이하는 가이드라인을 그리는 코드

            canvas.run {
                // 사각형 먼저 그리기
                drawLine(
                    it.guideLeftTopPair.first.toFloat(), it.guideLeftTopPair.second.toFloat(),
                    it.guideLeftBottomPair.first.toFloat(), it.guideLeftBottomPair.second.toFloat(),
                    it.guideRectPaint
                )
                drawLine(
                    it.guideLeftBottomPair.first.toFloat(), it.guideLeftBottomPair.second.toFloat(),
                    it.guideRightBottomPair.first.toFloat(), it.guideRightBottomPair.second.toFloat(),
                    it.guideRectPaint
                )
                drawLine(
                    it.guideRightBottomPair.first.toFloat(), it.guideRightBottomPair.second.toFloat(),
                    it.guideLeftBottomPair.first.toFloat(), it.guideLeftBottomPair.second.toFloat(),
                    it.guideRectPaint
                )
                drawLine(
                    it.guideLeftBottomPair.first.toFloat(), it.guideLeftBottomPair.second.toFloat(),
                    it.guideLeftTopPair.first.toFloat(), it.guideLeftTopPair.second.toFloat(),
                    it.guideRectPaint
                )

                //꼭지점 그리기
                drawCircle(
                    it.guideLeftTopPair.first.toFloat(), it.guideLeftTopPair.second.toFloat(),
                    it.guideCircleRadius, it.guideCirclePaint
                )
                drawCircle(
                    it.guideLeftBottomPair.first.toFloat(), it.guideLeftBottomPair.second.toFloat(),
                    it.guideCircleRadius, it.guideCirclePaint
                )
                drawCircle(
                    it.guideRightTopPair.first.toFloat(), it.guideRightTopPair.second.toFloat(),
                    it.guideCircleRadius, it.guideCirclePaint
                )
                drawCircle(
                    it.guideRightBottomPair.first.toFloat(), it.guideRightBottomPair.second.toFloat(),
                    it.guideCircleRadius, it.guideCirclePaint
                )
            }
        }
    }

    private fun drawConvertedImage(canvas: Canvas) {
        val resizingElementsPair =
            convertedImage?.bitmap?.getCanvasOnResizingBitmapElements(this.width, this.height) ?: return
        convertedImage?.convertedBitmapOnCanvasElements = IntArray(4) { resizingElementsPair.first[it] }
        convertedImage?.convertedBitmapResizingRate = resizingElementsPair.second
        resizingElementsPair.first.let {
            val bitmapRect = Rect(it[0], it[1], it[0] + it[2], it[1] + it[3])
            EzLogger.d("draw converted bitmap, rect : $bitmapRect")
            EzLogger.d("base converted bitmap canvas on rate : ${convertedImage?.convertedBitmapResizingRate}")
            canvas.drawBitmap(convertedImage?.bitmap ?: return, null, bitmapRect, null)
        }
    }
}