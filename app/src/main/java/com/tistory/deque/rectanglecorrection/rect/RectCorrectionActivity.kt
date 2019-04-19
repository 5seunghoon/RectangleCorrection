package com.tistory.deque.rectanglecorrection.rect

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.TedPermission
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.base.BaseActivity
import com.tistory.deque.rectanglecorrection.util.EzLogger
import com.tistory.deque.rectanglecorrection.util.RequestCodeConstant
import kotlinx.android.synthetic.main.activity_rect_correction.*

class RectCorrectionActivity : BaseActivity<RectCorrectViewModel>() {
    override val layoutResourceId: Int get() = R.layout.activity_rect_correction
    override val viewModel: RectCorrectViewModel
        get() = ViewModelProviders.of(this).get(RectCorrectViewModel::class.java)

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
                        EzLogger.d("data : ${data.data}" )
                        rect_image_view.setImageURI(data.data)
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
