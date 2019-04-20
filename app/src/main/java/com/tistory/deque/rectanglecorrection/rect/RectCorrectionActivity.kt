package com.tistory.deque.rectanglecorrection.rect

import android.content.Intent
import android.graphics.Bitmap
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
import org.opencv.core.Mat

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

    private var lowThreshold = 0
    private var highThreshold = 255

    private var originalImageMat: Mat = Mat()
    private var originalBaseImage: BaseImage? = null
    override fun initStartView() {
        getImageFromIntent(intent)
    }

    override fun initDataBinding() {
    }

    override fun initAfterBinding() {
    }

    private fun changeImage(){
        EzLogger.d("high : $highThreshold low : $lowThreshold ")
        originalBaseImage?.let {
            val imageOutput = Mat()

            imageProcessing(originalImageMat.nativeObjAddr, imageOutput.nativeObjAddr, lowThreshold, highThreshold)
            val outputBitmap = Bitmap.createBitmap(imageOutput.cols(), imageOutput.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(imageOutput, outputBitmap)
            rect_canvas_custom_view?.convertedImage = ConvertedImage(outputBitmap)
            rect_canvas_custom_view?.invalidate()
        }
    }

    private fun getImageFromIntent(intent: Intent?) {
        intent?.data?.let { imageUri ->
            EzLogger.d("uri : $imageUri")
            val imagePath = imageUri.getRealPath(this.contentResolver)
            EzLogger.d("path : $imagePath")

            originalBaseImage = BaseImage(imageUri) //uri로부터 model 생성
            //originalImage의 mat 생성
            Utils.bitmapToMat(originalBaseImage?.getBitmap(this) ?: return@let, originalImageMat)
            rect_canvas_custom_view?.baseImage = originalBaseImage ?: return

            //TODO : originalBaseImage를 이용해서 사각형(사다리꼴) OpenCV를 이용해서 감지. 그 후 해당 사다리꼴의 모서리를 list로 받기
            //TODO : 연산 완료된 모서리 포지션들을 이용해서 캔버스에 사다리꼴 그리기
            //TODO : (완료)버튼을 눌리면 해당 사다리꼴 모양으로 사진을 자르고 저장

            //changeImage()
            rect_canvas_custom_view?.invalidate()
        }

        super.onNewIntent(intent)
    }
}
