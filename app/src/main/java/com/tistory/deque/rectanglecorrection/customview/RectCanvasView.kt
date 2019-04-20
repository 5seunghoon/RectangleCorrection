package com.tistory.deque.rectanglecorrection.customview

import android.content.Context
import android.graphics.*
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

    var baseImage: BaseImage? = null
    var convertedBitmap: Bitmap? = null
    var convertSuccess: Boolean = false
    val guideLine = GuideLine()
    private var selectedGuidePoint = GuidePoint.NONE

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            setBackgroundColor(ContextCompat.getColor(context, R.color.canvasBackgroundColor))
            if(convertSuccess) {
                drawConvertedBitmap(it)
            } else {
                drawBaseImage(it)
                drawGuideLine(it)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
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
            selectedGuidePoint = guideLine.whatClicked(x, y)
        }
    }

    private fun touchMove(event: MotionEvent) {
        val x = event.x.toDouble()
        val y = event.y.toDouble()

        val newPair = Pair(x, y)

        when (selectedGuidePoint) {
            GuidePoint.LEFT_TOP -> guideLine.guideLeftTopPair = newPair
            GuidePoint.LEFT_BOTTOM -> guideLine.guideLeftBottomPair = newPair
            GuidePoint.RIGHT_TOP -> guideLine.guideRightTopPair = newPair
            GuidePoint.RIGHT_BOTTOM -> guideLine.guideRightBottomPair = newPair
            GuidePoint.NONE -> return
        }

        invalidate()
    }

    private fun touchUp(event: MotionEvent) {
        selectedGuidePoint = GuidePoint.NONE
        invalidate()
    }

    private fun drawConvertedBitmap(canvas:Canvas) {
        val convertedResizingElement = convertedBitmap?.getCanvasOnResizingBitmapElements(this.width, this.height) ?: return

        convertedResizingElement.first.let { element ->
            val bitmapRect = Rect(element[0], element[1], element[0] + element[2], element[1] + element[3])
            canvas.drawBitmap(convertedBitmap ?: return, null, bitmapRect, null)
            EzLogger.d("draw converted bitmap, rect : $bitmapRect")
        }
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
                    it.guideRightTopPair.first.toFloat(), it.guideRightTopPair.second.toFloat(),
                    it.guideRectPaint
                )
                drawLine(
                    it.guideRightTopPair.first.toFloat(), it.guideRightTopPair.second.toFloat(),
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
}