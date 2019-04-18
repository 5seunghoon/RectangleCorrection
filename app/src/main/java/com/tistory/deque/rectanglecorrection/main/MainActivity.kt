package com.tistory.deque.rectanglecorrection.main

import android.Manifest
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.base.BaseActivity
import com.tistory.deque.rectanglecorrection.base.BaseViewModel
import com.tistory.deque.rectanglecorrection.util.EzLogger
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat

class MainActivity : BaseActivity<BaseViewModel>(), CameraBridgeViewBase.CvCameraViewListener2 {

    override val layoutResourceId: Int get() = R.layout.activity_main
    override val viewModel: BaseViewModel
        get() = ViewModelProviders.of(this).get(MainViewModel::class.java)

    private val OPENCV_TAG = "OPENCV_TAG"

    private var matInput: Mat? = null
    private var matResult: Mat? = null

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> activity_main_surface_view?.enableView()
                else -> super.onManagerConnected(status)
            }
        }
    }

    override fun initStartView() {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        viewModel.addDisposable(
            TedRx2Permission.with(this)
                .setRationaleTitle("사진 접근 권한")
                .setRationaleMessage("사진을 찍기 위해선 권한이 필요합니다.")
                .setPermissions(Manifest.permission.CAMERA)
                .request()
                .subscribe({
                    if (!it.isGranted) {
                        Snackbar.make(findViewById(android.R.id.content), "권한 허용 부탁합니다", Snackbar.LENGTH_LONG).show();
                    }
                }, {})
        )

        activity_main_surface_view.run {
            visibility = View.VISIBLE
            setCvCameraViewListener(this@MainActivity)
            setCameraIndex(0)
        }
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
    }

    override fun initDataBinding() {}

    override fun initAfterBinding() {}

    override fun onCameraViewStarted(width: Int, height: Int) {}

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        matInput = inputFrame?.rgba()

        inputFrame?.rgba()?.let {
            matInput = it
            if (matResult == null) matResult = Mat(it.rows(), it.cols(), it.type())
            convertRGBtoGray(matInput?.nativeObjAddr ?: return@let, matResult?.nativeObjAddr ?: return@let)
        }
        return matResult ?: Mat()
    }

    override fun onPause() {
        super.onPause()
        activity_main_surface_view?.disableView()
    }

    override fun onResume() {
        super.onResume()
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
    }

    private external fun convertRGBtoGray(matAddrInput: Long, matAddrResult: Long)

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("opencv_java4")
        }
    }
}
