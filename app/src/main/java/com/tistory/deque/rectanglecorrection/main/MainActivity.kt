package com.tistory.deque.rectanglecorrection.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.base.BaseActivity
import com.tistory.deque.rectanglecorrection.camera.CameraActivity
import com.tistory.deque.rectanglecorrection.rect.RectCorrectionActivity
import com.tistory.deque.rectanglecorrection.util.EzLogger
import com.tistory.deque.rectanglecorrection.util.RequestCodeConstant
import com.tistory.deque.rectanglecorrection.util.getRealPath
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_rect_correction.*
import org.opencv.android.Utils
import org.opencv.core.Mat

class MainActivity : BaseActivity<MainViewModel>() {
    override val layoutResourceId: Int get() = R.layout.activity_main
    override val viewModel: MainViewModel
        get() = ViewModelProviders.of(this).get(MainViewModel::class.java)

    override fun initStartView() {
    }

    override fun initDataBinding() {
        viewModel.clickCameraButtonEvent.observe(this, Observer {
            startActivity(Intent(this@MainActivity, CameraActivity::class.java))
        })
        viewModel.clickRectButtonEvent.observe(this, Observer {
            startActivityForResult(Intent.createChooser(Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }, "Get Album"), RequestCodeConstant.RectPictureSelectRequsetCode)
        })
    }

    override fun initAfterBinding() {
        main_start_camera_button.setOnClickListener {
            viewModel.clickCameraButton()
        }
        main_start_rectangle_button.setOnClickListener {
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
                            viewModel.clickRectButton()
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
                        startActivity(Intent(this@MainActivity, RectCorrectionActivity::class.java).apply {
                            setData(it.data)
                        })
                    }
                }
            }
        }




        super.onActivityResult(requestCode, resultCode, data)
    }
}
