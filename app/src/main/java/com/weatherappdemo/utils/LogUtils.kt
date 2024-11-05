package com.weatherappdemo.utils

import android.util.Log

object LogUtils {
    private const val isDebug = true // Change this to false in production
    private const val tag = "AppLog"
    private val level = LogLevel.DEBUG
    fun log(message: String) {
        if (isDebug) {
            when (level) {
                LogLevel.DEBUG -> Log.d(tag, message)
                LogLevel.ERROR -> Log.e(tag, message)
                LogLevel.INFO -> Log.i(tag, message)
                LogLevel.WARN -> Log.w(tag, message)
                LogLevel.VERBOSE -> Log.v(tag, message)
            }
        }
    }

    enum class LogLevel {
        DEBUG,
        ERROR,
        INFO,
        WARN,
        VERBOSE
    }
}
