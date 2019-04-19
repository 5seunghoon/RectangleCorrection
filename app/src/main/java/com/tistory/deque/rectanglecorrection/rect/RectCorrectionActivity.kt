package com.tistory.deque.rectanglecorrection.rect

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.base.BaseActivity
import com.tistory.deque.rectanglecorrection.model.BaseImage
import com.tistory.deque.rectanglecorrection.util.EzLogger
import com.tistory.deque.rectanglecorrection.util.RequestCodeConstant
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

        var bitmapMaxSize:Int = 3000
    }

    override val layoutResourceId: Int get() = R.layout.activity_rect_correction
    override val viewModel: RectCorrectViewModel
        get() = ViewModelProviders.of(this).get(RectCorrectViewModel::class.java)

    private external fun loadImage(imageFileName: String, img: Long)
    private external fun imageProcessing(inputImage: Long, outputImage: Long)

    private var originalBaseImage:BaseImage? = null

    override fun initStartView() {
        getImageFromIntent(intent)
    }

    override fun initDataBinding() {
    }

    override fun initAfterBinding() {
    }

    private fun getImageFromIntent(intent: Intent?) {
        intent?.data?.let {imageUri ->
            EzLogger.d("uri : $imageUri" )
            val imagePath = imageUri.getRealPath(this.contentResolver)
            EzLogger.d("path : $imagePath")

            originalBaseImage = BaseImage(imageUri)

            rect_canvas_custom_view?.baseImage = originalBaseImage
            rect_canvas_custom_view.invalidate()

            //TODO : originalBaseImage를 이용해서 사각형(사다리꼴) OpenCV를 이용해서 감지. 그 후 해당 사다리꼴의 모서리를 list로 받기
            //TODO : 연산 완료된 모서리 포지션들을 이용해서 캔버스에 사다리꼴 그리기
            //TODO : (완료)버튼을 눌리면 해당 사다리꼴 모양으로 사진을 자르고 저장

            // 사진을 흑백으로 바꾸는 코드
            val imageInput = Mat()
            val imageOutput = Mat()

            imagePath?.let {
                loadImage(it, imageInput.nativeObjAddr)
                imageProcessing(imageInput.nativeObjAddr, imageOutput.nativeObjAddr)
                val outputBitmap = Bitmap.createBitmap(imageOutput.cols(), imageOutput.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(imageOutput, outputBitmap)
            }

        }

        super.onNewIntent(intent)
    }
}
