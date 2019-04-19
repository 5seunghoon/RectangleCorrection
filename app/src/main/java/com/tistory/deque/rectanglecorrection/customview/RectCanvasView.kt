package com.tistory.deque.rectanglecorrection.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.model.BaseImage
import com.tistory.deque.rectanglecorrection.util.EzLogger

class RectCanvasView : View {
    init {
        initView()
    }

    constructor(context: Context) : super(context)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

    private fun initView() {}

    private var imageCanvas: Canvas? = null
    var baseImage: BaseImage? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        imageCanvas = canvas

        canvas?.let {
            setBackgroundColor(ContextCompat.getColor(context, R.color.canvasBackgroundColor))
            drawBaseImage(it)
        }
    }

    private fun drawBaseImage(canvas: Canvas) {
        val elements = baseImage?.getResizedBitmapElements(this.width, this.height) ?: return
        val bitmapRect = Rect(elements[0], elements[1], elements[0] + elements[2], elements[1] + elements[3])
        EzLogger.d("draw base image, rect : $bitmapRect")
        EzLogger.d("base image canvas on rate : ${baseImage?.canvasOnResizingRate}")
        canvas.drawBitmap(baseImage?.getBitmap(this.context) ?: return, null, bitmapRect, null)
    }

}