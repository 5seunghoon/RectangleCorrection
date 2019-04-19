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
    }

    override val layoutResourceId: Int get() = R.layout.activity_rect_correction
    override val viewModel: RectCorrectViewModel
        get() = ViewModelProviders.of(this).get(RectCorrectViewModel::class.java)

    private external fun loadImage(imageFileName: String, img: Long)
    private external fun imageProcessing(inputImage: Long, outputImage: Long)

    override fun initStartView() {
    }

    override fun initDataBinding() {
        viewModel.clickSelectPictureButtonEvent.observe(this, Observer {
            startActivityForResult(Intent.createChooser(Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }, "Get Album"), RequestCodeConstant.RectPictureSelectRequsetCode)
        })
    }

    override fun initAfterBinding() {
        rect_button_select_picture.setOnClickListener {
            viewModel.addDisposable(
                TedRx2Permission.with(this)
                    .setRationaleTitle("저장공간 접근 권한")
                    .setRationaleMessage("사진을 가져오기 위해선 권한이 필요합니다.")
                    .setPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    .request()
                    .subscribe({
                        if (!it.isGranted) {
                            Snackbar.make(findViewById(android.R.id.content), "권한 허용 부탁합니다", Snackbar.LENGTH_LONG)
                                .show()
                        } else {
                            viewModel.clickSelectPictureButton()
                        }
                    }, {})
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodeConstant.RectPictureSelectRequsetCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        EzLogger.d("uri : ${data.data}" )
                        val imagePath = data.data?.getRealPath(this.contentResolver)
                        EzLogger.d("path : ${imagePath}")

                        val imageInput = Mat()
                        val imageOutput = Mat()

                        imagePath?.let {
                            loadImage(it, imageInput.nativeObjAddr)
                            imageProcessing(imageInput.nativeObjAddr, imageOutput.nativeObjAddr)
                            val outputBitmap = Bitmap.createBitmap(imageOutput.cols(), imageOutput.rows(), Bitmap.Config.ARGB_8888)
                            Utils.matToBitmap(imageOutput, outputBitmap)
                            rect_image_view.setImageBitmap(outputBitmap)
                        }

                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
