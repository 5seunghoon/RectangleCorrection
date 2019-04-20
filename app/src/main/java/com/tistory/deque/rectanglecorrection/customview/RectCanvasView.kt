package com.tistory.deque.rectanglecorrection.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.model.BaseImage
import com.tistory.deque.rectanglecorrection.model.ConvertedImage
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

    private var imageCanvas: Canvas? = null
    var baseImage: BaseImage? = null
    var convertedImage: ConvertedImage? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        imageCanvas = canvas

        canvas?.let {
            setBackgroundColor(ContextCompat.getColor(context, R.color.canvasBackgroundColor))
            drawConvertedImage(it)
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