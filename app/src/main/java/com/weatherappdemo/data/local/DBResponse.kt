package com.weatherappdemo.data.local

sealed class DBResponse<T> {
    class Success<T>(val data: T) : DBResponse<T>()
    class Error<T>(val message: String) : DBResponse<T>()
}