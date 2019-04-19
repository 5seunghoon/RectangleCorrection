package com.tistory.deque.rectanglecorrection.rect

import androidx.lifecycle.LiveData
import com.tistory.deque.rectanglecorrection.base.BaseViewModel
import com.tistory.deque.rectanglecorrection.util.SingleLiveEvent

class RectCorrectViewModel: BaseViewModel() {

    private val _clickSelectPictureButtonEvent = SingleLiveEvent<Any>()
    val clickSelectPictureButtonEvent: LiveData<Any> get() = _clickSelectPictureButtonEvent

    fun clickSelectPictureButton() {
        _clickSelectPictureButtonEvent.call()
    }
}