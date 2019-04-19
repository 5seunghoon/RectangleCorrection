package com.tistory.deque.rectanglecorrection.main

import androidx.lifecycle.LiveData
import com.tistory.deque.rectanglecorrection.base.BaseViewModel
import com.tistory.deque.rectanglecorrection.util.SingleLiveEvent

class MainViewModel : BaseViewModel() {
    private val _clickCameraButtonEvent = SingleLiveEvent<Any>()
    val clickCameraButtonEvent: LiveData<Any> get() = _clickCameraButtonEvent

    private val _clickRectButtonEvent = SingleLiveEvent<Any>()
    val clickRectButtonEvent: LiveData<Any> get() = _clickRectButtonEvent

    fun clickCameraButton() {
        _clickCameraButtonEvent.call()
    }
    fun clickRectButton() {
        _clickRectButtonEvent.call()
    }


}