package com.weatherappdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weatherappdemo.MyApplication
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.data.model.WeatherDataModel
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.data.remote.api.RetrofitClient
import com.weatherappdemo.data.repository.WeatherLocalRepository
import com.weatherappdemo.data.utils.toWeatherData
import com.weatherappdemo.data.utils.toWeatherEntity
import com.weatherappdemo.utils.LogUtils
import kotlinx.coroutines.launch

class WeatherViewModel : BaseViewModel() {
    private val apiRepository = RetrofitClient.repository
    private val localRepository = WeatherLocalRepository.getInstance()
    private val application = MyApplication.instance

    //DB
    private val _addSearchedCity = MutableLiveData<DBResponse<String>>()
    val addSearchedCity: LiveData<DBResponse<String>> = _addSearchedCity

    private val _getSearchedCitiesList = MutableLiveData<DBResponse<List<WeatherDataModel>>>()
    val getSearchedCitiesList: LiveData<DBResponse<List<WeatherDataModel>>> = _getSearchedCitiesList

    private val _getSearchedCityData = MutableLiveData<DBResponse<WeatherDataModel>>()
    val getSearchedCityData: LiveData<DBResponse<WeatherDataModel>> = _getSearchedCityData

    //API
    private val _getCurrentWeather = MutableLiveData<APIResponse<WeatherDataModel>>()
    val getCurrentWeather: LiveData<APIResponse<WeatherDataModel>> = _getCurrentWeather

    private val _getForecastWeather = MutableLiveData<APIResponse<List<ForecastData>>>()
    val getForecastWeather: LiveData<APIResponse<List<ForecastData>>> = _getForecastWeather


    private val _fiveDaysForecast = MutableLiveData<APIResponse<List<ForecastData>>>()
    val fiveDaysForecast: LiveData<APIResponse<List<ForecastData>>> = _fiveDaysForecast

    private val _getWeatherByCity = MutableLiveData<APIResponse<WeatherDataModel>>()
    val getWeatherByCity: LiveData<APIResponse<WeatherDataModel>> = _getWeatherByCity

    private var isFetchingData: Boolean = false

    fun fetchCurrentLocationWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            // Check if data is already in the database and fresh
            val existingWeather = localRepository.getWeatherByLatLng(lat, lon)
            if (existingWeather != null && isDataFresh(existingWeather.lastUpdated)) {
                _getCurrentWeather.postValue(APIResponse.Success(existingWeather.toWeatherData()))
                return@launch
            }

            // Fetch data from API if not fresh or not in DB
            if (!isFetchingData) {
                isFetchingData = true
                try {
                    val result = apiRepository.getCurrentWeather(lat, lon)
                    if (result is APIResponse.Success) {
                        // Save data to DB
                        localRepository.saveWeatherData(result.data.toWeatherEntity())
                        _getCurrentWeather.postValue(result)
                    } else {
                        _getCurrentWeather.postValue(result)
                    }
                } finally {
                    isFetchingData = false
                }
            }
        }
    }

    private fun isDataFresh(lastUpdated: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val freshnessThreshold = 30 * 60 * 1000 // 30 minutes in milliseconds
        return currentTime - lastUpdated < freshnessThreshold
    }

    fun getWeatherDataByCity(cityName: String) {
        showLoading()
        viewModelScope.launch {
            val result = apiRepository.getWeatherByCityName(cityName)
            _getWeatherByCity.postValue(result)
            hideLoading()
        }
    }

    fun fetchAndSaveFiveDaysForecast(lat: Double, lon: Double) {
        LogUtils.log("fetchFiveDaysForecast viewmodel ")
        viewModelScope.launch {
            val result = apiRepository.getFiveDaysForecast(lat, lon)
            _fiveDaysForecast.postValue(result)
        }
    }

    //DB Methods

    fun addSearchedCity(weatherData: WeatherDataModel) {
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
            val result = localRepository.getAllSearchedCitiesList()
            _getSearchedCitiesList.postValue(result)
            hideLoading()
        }
    }

}
