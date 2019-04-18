package com.tistory.deque.rectanglecorrection.util

import android.util.Log

object EzLogger {
    val EzLoggerTag = "[EZ_LOGGER]"
    fun d(log: String) {
        Log.d(EzLoggerTag, log)
    }

    fun d(tag: String, log: String) {
        Log.d("$EzLoggerTag $tag", log)
    }
}