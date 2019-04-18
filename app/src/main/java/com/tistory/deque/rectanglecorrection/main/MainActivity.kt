package com.tistory.deque.rectanglecorrection.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.tistory.deque.rectanglecorrection.R
import com.tistory.deque.rectanglecorrection.base.BaseActivity
import com.tistory.deque.rectanglecorrection.base.BaseViewModel
import com.tistory.deque.rectanglecorrection.util.EzLogger

class MainActivity : BaseActivity<BaseViewModel>() {
    override val layoutResourceId: Int get() = R.layout.activity_main
    override val viewModel: BaseViewModel
        get() = ViewModelProviders.of(this).get(MainViewModel::class.java)

    override fun initStartView() {
    }

    override fun initDataBinding() {
    }

    override fun initAfterBinding() {
        EzLogger.d(stringFromJNI())
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
