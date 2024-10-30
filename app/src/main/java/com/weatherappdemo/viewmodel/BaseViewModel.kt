package com.weatherappdemo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    private val isLoading = MutableLiveData<Boolean>()
    private val errorMessage = MutableLiveData<String?>()

    fun showLoading() {
        isLoading.value = true
    }

    fun hideLoading() {
        isLoading.value = false
    }

    fun showErrorMsg() : MutableLiveData<String?>{
        return errorMessage
    }
}