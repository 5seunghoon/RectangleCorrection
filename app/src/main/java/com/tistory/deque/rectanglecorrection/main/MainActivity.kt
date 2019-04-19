package com.tistory.deque.rectanglecorrection.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.base.BaseActivity
import com.tistory.deque.rectanglecorrection.camera.CameraActivity
import com.tistory.deque.rectanglecorrection.rect.RectCorrectionActivity
import kotlinx.android.synthetic.main.activity_main.*

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
            startActivity(Intent(this@MainActivity, RectCorrectionActivity::class.java))
        })
    }

    override fun initAfterBinding() {
        main_start_camera_button.setOnClickListener {
            viewModel.clickCameraButton()
        }
        main_start_rectangle_button.setOnClickListener {
            viewModel.clickRectButton()
        }
    }
}
