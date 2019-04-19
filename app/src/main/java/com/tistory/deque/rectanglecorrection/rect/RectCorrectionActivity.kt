package com.tistory.deque.rectanglecorrection.rect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.base.BaseActivity

class RectCorrectionActivity : BaseActivity<RectCorrectViewModel>() {
    override val layoutResourceId: Int get() = R.layout.activity_rect_correction
    override val viewModel: RectCorrectViewModel
        get() = ViewModelProviders.of(this).get(RectCorrectViewModel::class.java)

    override fun initStartView() {
    }

    override fun initDataBinding() {    
    }

    override fun initAfterBinding() {
    }
}
