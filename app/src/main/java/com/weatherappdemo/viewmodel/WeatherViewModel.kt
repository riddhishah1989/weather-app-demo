package com.weatherappdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weatherappdemo.MyApplication
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.model.WeatherData
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.data.remote.api.RetrofitClient
import com.weatherappdemo.data.remote.repository.WeatherLocalRepository
import com.weatherappdemo.data.remote.webResponse.ForecastResponse
import com.weatherappdemo.data.remote.webResponse.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel(application: MyApplication) : BaseViewModel() {
    private val apiRepository = RetrofitClient.repository
    private val localRepository = WeatherLocalRepository.getInstance()

    private val _favCitiesList = MutableLiveData<DBResponse<List<WeatherData>>>()
    val favCitiesList: LiveData<DBResponse<List<WeatherData>>> = _favCitiesList

    private val _addFavCity = MutableLiveData<DBResponse<String>>()
    val addFavCity: LiveData<DBResponse<String>> = _addFavCity

    private val _addSearchedCity = MutableLiveData<DBResponse<String>>()
    val addSearchedCity: LiveData<DBResponse<String>> = _addSearchedCity

    private val _offlineDataList = MutableLiveData<DBResponse<List<WeatherData>>>()
    val offlineDataList: LiveData<DBResponse<List<WeatherData>>> = _offlineDataList

    private val _getCurrentWeather = MutableLiveData<APIResponse<WeatherResponse>>()
    val getCurrentWeather: LiveData<APIResponse<WeatherResponse>> = _getCurrentWeather


    private val _getForecastWeather = MutableLiveData<APIResponse<ForecastResponse>>()
    val getForecastWeather: LiveData<APIResponse<ForecastResponse>> = _getForecastWeather


    fun getCurrentLocationWeather(lat: Double, long: Double) {
        showLoading()
        viewModelScope.launch {
            val result = apiRepository.getCurrentWeather(lat, long)
            _getCurrentWeather.postValue(result)
            hideLoading()
        }
    }

    fun getForecastWeather(cityName: String) {
        showLoading()
        viewModelScope.launch {
            val result = apiRepository.getWeatherForecast(cityName)
            _getForecastWeather.postValue(result)
            hideLoading()
        }

    }


    //DB Methods

    fun getFavCitiesData() {
        showLoading()
        viewModelScope.launch {
            val result = localRepository.getAllFavouriteCities()
            _favCitiesList.postValue(result)
            hideLoading()
        }
    }

    fun addFavCity(weatherData: WeatherData) {
        showLoading()
        viewModelScope.launch {
            val result = localRepository.addFavourite(weatherData)
            _addFavCity.postValue(result)
            hideLoading()
        }
    }

    fun addSearchedCity(weatherData: WeatherData) {
        showLoading()
        viewModelScope.launch {
            val result = localRepository.addSearchedCity(weatherData)
            _addSearchedCity.postValue(result)
            hideLoading()
        }
    }

    fun getAllLastSyncWeatherData() {
        showLoading()
        viewModelScope.launch {
            val result = localRepository.getOfflineWeatherData()
            _offlineDataList.postValue(result)
            hideLoading()
        }
    }
}
