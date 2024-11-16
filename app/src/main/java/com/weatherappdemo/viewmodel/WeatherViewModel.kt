package com.weatherappdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.model.CityWeatherModel
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.data.model.WeatherData
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.data.remote.api.RetrofitClient
import com.weatherappdemo.data.remote.repository.WeatherLocalRepository
import com.weatherappdemo.utils.LogUtils
import com.weatherappdemo.utils.Utils
import kotlinx.coroutines.launch

class WeatherViewModel : BaseViewModel() {
    private val apiRepository = RetrofitClient.repository
    private val localRepository = WeatherLocalRepository.getInstance()
    private val application = MyApplication.instance

    //DB
    private val _addSearchedCity = MutableLiveData<DBResponse<String>>()
    val addSearchedCity: LiveData<DBResponse<String>> = _addSearchedCity

    private val _getSearchedCitiesList = MutableLiveData<DBResponse<List<WeatherData>>>()
    val getSearchedCitiesList: LiveData<DBResponse<List<WeatherData>>> = _getSearchedCitiesList

    //API
    private val _getCurrentWeather = MutableLiveData<APIResponse<WeatherData>>()
    val getCurrentWeather: LiveData<APIResponse<WeatherData>> = _getCurrentWeather

    private val _getForecastWeather = MutableLiveData<APIResponse<List<ForecastData>>>()
    val getForecastWeather: LiveData<APIResponse<List<ForecastData>>> = _getForecastWeather


    private val _fiveDaysForecast = MutableLiveData<APIResponse<List<ForecastData>>>()
    val fiveDaysForecast: LiveData<APIResponse<List<ForecastData>>> = _fiveDaysForecast

    private val _getWeatherByCity = MutableLiveData<APIResponse<CityWeatherModel>>()
    val getWeatherByCity: LiveData<APIResponse<CityWeatherModel>> = _getWeatherByCity


    fun getCurrentLocationWeather(lat: Double, long: Double) {
        LogUtils.log(message = "getCurrentLocationWeather called $lat & $long")
        if (Utils.isInternetConnected(application)) {
            LogUtils.log(message = "Internet connected")
            showLoading()
            viewModelScope.launch {
                val result = apiRepository.getCurrentWeather(lat, long)
                _getCurrentWeather.postValue(result)
                hideLoading()
            }
        } else {
            Utils.showToast(
                application,
                application.getString(R.string.no_internet_connection_found)
            )
        }

    }

    fun getWeatherDataByCity(cityName: String) {
        showLoading()
        viewModelScope.launch {
            val result = apiRepository.getWeatherByCityName(cityName)
            _getWeatherByCity.postValue(result)
            hideLoading()
        }
    }

    fun fetchFiveDaysForecast(lat: Double, lon: Double) {
        LogUtils.log("fetchFiveDaysForecast viewmodel ")
        viewModelScope.launch {
            val result = apiRepository.getFiveDaysForecast(lat, lon)
            _fiveDaysForecast.postValue(result)
        }
    }

    fun getAllForecastWeather(lat: Double, long: Double) {
        showLoading()
        viewModelScope.launch {
            val result = apiRepository.getAllWeatherForecast(lat, long)
            _getForecastWeather.postValue(result)
            hideLoading()
        }

    }


    //DB Methods

    fun addSearchedCity(weatherData: WeatherData) {
        showLoading()
        viewModelScope.launch {
            val result = localRepository.addSearchedCity(weatherData)
            _addSearchedCity.postValue(result)
            hideLoading()
        }
    }

    fun getAllSearchedCitiesList() {
        showLoading()
        viewModelScope.launch {
            val result = localRepository.getOfflineWeatherData()
            _getSearchedCitiesList.postValue(result)
            hideLoading()
        }
    }
}
