package com.tistory.deque.rectanglecorrection.rect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tistory.deque.rectanglecorrection.base.BaseViewModel
import com.tistory.deque.rectanglecorrection.util.SingleLiveEvent

class RectCorrectViewModel: BaseViewModel() {
    private val _clickRectCorrectionOkButtonEvent = SingleLiveEvent<Any>()
    val clickRectCorrectionOkButtonEvent: LiveData<Any> get() = _clickRectCorrectionOkButtonEvent

    fun clickRectCorrectionOkButton() {
        _clickRectCorrectionOkButtonEvent.call()
    }
}