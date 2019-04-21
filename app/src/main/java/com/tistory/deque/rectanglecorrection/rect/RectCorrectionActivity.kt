package com.tistory.deque.rectanglecorrection.rect

import android.content.Intent
import android.graphics.Bitmap
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.base.BaseActivity
import com.tistory.deque.rectanglecorrection.model.BaseImage
import com.tistory.deque.rectanglecorrection.model.ConvertedImage
import com.tistory.deque.rectanglecorrection.util.EzLogger
import com.tistory.deque.rectanglecorrection.util.getRealPath
import kotlinx.android.synthetic.main.activity_rect_correction.*
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.utils.Converters
import org.opencv.imgproc.Imgproc


class RectCorrectionActivity : BaseActivity<RectCorrectViewModel>() {
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("opencv_java4")
        }

        var bitmapMaxSize: Int = 3000
    }

    override val layoutResourceId: Int get() = R.layout.activity_rect_correction
    override val viewModel: RectCorrectViewModel
        get() = ViewModelProviders.of(this).get(RectCorrectViewModel::class.java)

    private external fun loadImage(imageFileName: String, img: Long)
    private external fun imageProcessing(inputImage: Long, outputImage: Long, lowThreshold: Int, highThreshold: Int)

    private var originalImageMat: Mat = Mat()
    private var originalBaseImage: BaseImage? = null

    override fun initStartView() {
        getImageFromIntent(intent)
    }

    override fun initDataBinding() {
        viewModel.clickRectCorrectionOkButtonEvent.observe(this, Observer {
            perspectiveConvertImage()
        })
    }

    override fun initAfterBinding() {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.rect_correction_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.rect_correction_ok -> {
                viewModel.clickRectCorrectionOkButton()
            }
        }
        return false
    }

    private fun perspectiveConvertImage() {
        // http://answers.opencv.org/question/102464/android-opencv-perspective-transformation-not-working-as-expected/

        val rate = 1 / (originalBaseImage?.convertedBitmapResizingRate ?: return)
        val bitmapPosX = originalBaseImage?.convertedBitmapOnCanvasElements?.get(0) ?: return
        val bitmapPosY = originalBaseImage?.convertedBitmapOnCanvasElements?.get(1) ?: return
        // 캔버스에 그려진 guideLine의 좌표를 실제 비트맵의 어느 좌표인지 계산
        fun calcRealPoint(x:Double, y:Double): Point{
            return Point((x - bitmapPosX.toDouble()) * rate, (y - bitmapPosY.toDouble()) * rate)
        }

        val guideLine = rect_canvas_custom_view?.guideLine ?: return

        val srcPoints = ArrayList<Point>()
        guideLine.let {
            srcPoints.run {
                add(calcRealPoint(it.guideLeftTopPair.first, it.guideLeftTopPair.second))
                add(calcRealPoint(it.guideLeftBottomPair.first, it.guideLeftBottomPair.second))
                add(calcRealPoint(it.guideRightTopPair.first, it.guideRightTopPair.second))
                add(calcRealPoint(it.guideRightBottomPair.first, it.guideRightBottomPair.second))
            }
        }

        val dstPoints = ArrayList<Point>()
        originalBaseImage?.let {
            dstPoints.run {
                add(Point(0.0, 0.0))
                add(Point(0.0, it.height.toDouble()))
                add(Point(it.width.toDouble(), 0.0))
                add(Point(it.width.toDouble(), it.height.toDouble()))
            }
        }

        val srcMat: Mat = Converters.vector_Point2f_to_Mat(srcPoints)
        val dstMat: Mat = Converters.vector_Point2f_to_Mat(dstPoints)

        val perspectiveTransformation = Imgproc.getPerspectiveTransform(srcMat, dstMat)
        val inputMat: Mat = Mat(originalBaseImage?.width ?: return, originalBaseImage?.height ?: return, CvType.CV_8UC4)
        val outputMat: Mat =
            Mat(originalBaseImage?.width ?: return, originalBaseImage?.height ?: return, CvType.CV_8UC4)
        Utils.bitmapToMat(originalBaseImage?.getBitmap(this) ?: return, inputMat)

        Imgproc.warpPerspective(
            inputMat,
            outputMat,
            perspectiveTransformation,
            Size(originalBaseImage?.width?.toDouble() ?: return, originalBaseImage?.height?.toDouble() ?: return)
        )

        val outputBitmap: Bitmap = Bitmap.createBitmap(
            originalBaseImage?.width ?: return,
            originalBaseImage?.height ?: return,
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(outputMat, outputBitmap)

        rect_canvas_custom_view?.convertedBitmap = outputBitmap
        rect_canvas_custom_view?.convertSuccess = true
        rect_canvas_custom_view?.invalidate()
    }

    private fun getImageFromIntent(intent: Intent?) {
        intent?.data?.let { imageUri ->
            EzLogger.d("uri : $imageUri")

            originalBaseImage = BaseImage(imageUri) //uri로부터 model 생성
            //originalImage의 mat 생성
            //Utils.bitmapToMat(originalBaseImage?.getBitmap(this) ?: return@let, originalImageMat)
            rect_canvas_custom_view?.baseImage = originalBaseImage ?: return
            rect_canvas_custom_view?.invalidate()
        }

        super.onNewIntent(intent)
    }
}
