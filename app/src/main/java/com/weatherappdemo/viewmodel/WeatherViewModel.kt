package com.weatherappdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.data.model.WeatherDataModel
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.data.remote.api.RetrofitClient
import com.weatherappdemo.data.repository.WeatherLocalRepository
import com.weatherappdemo.data.utils.toForecastDataList
import com.weatherappdemo.data.utils.toWeatherData
import com.weatherappdemo.data.utils.toWeatherEntity
import com.weatherappdemo.utils.Utils
import kotlinx.coroutines.launch

class WeatherViewModel : BaseViewModel() {
    private val apiRepository = RetrofitClient.repository
    private val localRepository = WeatherLocalRepository.getInstance()
    private val application = MyApplication.instance

    private val _addSearchedCity = MutableLiveData<DBResponse<String>>()
    val addSearchedCity: LiveData<DBResponse<String>> = _addSearchedCity

    private val _getSearchedCitiesList =
        MutableLiveData<DBResponse<MutableList<WeatherDataModel>>>()
    val getSearchedCitiesList: LiveData<DBResponse<MutableList<WeatherDataModel>>> =
        _getSearchedCitiesList

    private val _getSearchedCityData = MutableLiveData<DBResponse<WeatherDataModel>>()
    val getSearchedCityData: LiveData<DBResponse<WeatherDataModel>> = _getSearchedCityData

    private val _getCurrentWeather = MutableLiveData<APIResponse<WeatherDataModel>>()
    val getCurrentWeather: LiveData<APIResponse<WeatherDataModel>> = _getCurrentWeather

    private val _fiveDaysForecast = MutableLiveData<APIResponse<MutableList<ForecastData>>>()
    val fiveDaysForecast: LiveData<APIResponse<MutableList<ForecastData>>> = _fiveDaysForecast


    private val _searchCityFiveDaysForecast =
        MutableLiveData<APIResponse<MutableList<ForecastData>>>()
    val cityFiveDaysForecast: LiveData<APIResponse<MutableList<ForecastData>>> =
        _searchCityFiveDaysForecast

    private val _getWeatherByCity = MutableLiveData<APIResponse<WeatherDataModel>>()
    val getWeatherByCity: LiveData<APIResponse<WeatherDataModel>> = _getWeatherByCity

    private var isFetchingData: Boolean = false

    fun fetchAndSaveCurrentLocationWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            var shouldFetchFromApi = false // Flag to decide whether to fetch from the API

            // Check if data is already in the database and fresh
            when (val dbResponse = localRepository.getCurrentLocationWeather(lat, lon)) {
                is DBResponse.Success -> {
                    if (dbResponse.data != null) {
                        val lastUpdated = dbResponse.data.lastUpdated
                        if (Utils.isDataFresh(lastUpdated)) {
                            _getCurrentWeather.postValue(APIResponse.Success(dbResponse.data.toWeatherData()))
                            return@launch // Exit early if data is fresh
                        } else {
                            shouldFetchFromApi = true // Data is stale, fetch from API
                        }
                    } else {
                        shouldFetchFromApi = true // No data in the database, fetch from API
                    }
                }

                is DBResponse.Error -> {
                    Utils.showToast(application, dbResponse.message)
                    shouldFetchFromApi = true // Handle DB error and fetch from API
                }
            }

            // Fetch data from API if needed
            if (shouldFetchFromApi) {
                if (!Utils.isInternetConnected(application)) {
                    Utils.showToast(
                        application,
                        application.getString(R.string.no_internet_connection_found)
                    )
                    return@launch
                }

                isFetchingData = true // Prevent multiple simultaneous API calls
                try {
                    val result = apiRepository.getCurrentLocationWeather(lat, lon)
                    if (result is APIResponse.Success) {
                        // Save data to DB
                        localRepository.saveCurrentLocationData(result.data.toWeatherEntity())
                        _getCurrentWeather.postValue(result)
                    } else {
                        _getCurrentWeather.postValue(result) // Pass error to UI
                    }
                } finally {
                    isFetchingData = false // Reset fetching state
                }
            }
        }
    }


    fun fetchAndSaveFiveDaysForecast(lat: Double, lon: Double) {
        showLoading()
        viewModelScope.launch {
            when (val dbResponse = localRepository.get5DaysForecastFromDB(lat, lon)) {
                is DBResponse.Success -> {
                    if (dbResponse.data.isNotEmpty()) {
                        val list = dbResponse.data.toForecastDataList().toMutableList()
                        _fiveDaysForecast.postValue(APIResponse.Success(list))
                    } else {
                        if (!Utils.isInternetConnected(application)) {
                            return@launch Utils.showToast(
                                application,
                                application.getString(R.string.no_internet_connection_found)
                            )
                        }
                        val apiResponse = apiRepository.getFiveDaysForecast(lat, lon)
                        _fiveDaysForecast.postValue(apiResponse)
                    }
                }

                is DBResponse.Error -> {
                    return@launch Utils.showToast(application, dbResponse.message)
                }
            }

            hideLoading()
        }
    }


    fun fetchCityFiveDaysForecast(lat: Double, lon: Double) {
        showLoading()
        viewModelScope.launch {
            if (!Utils.isInternetConnected(application)) {
                return@launch Utils.showToast(
                    application,
                    application.getString(R.string.no_internet_connection_found)
                )
            }
            val apiResponse = apiRepository.getFiveDaysForecast(lat, lon)
            _searchCityFiveDaysForecast.postValue(apiResponse)
            hideLoading()
        }
    }

    fun getWeatherDataByCity(cityName: String) {
        viewModelScope.launch {
            if (Utils.isInternetConnected(application)) {
                showLoading()
                val result = apiRepository.getWeatherByCityName(cityName)
                _getWeatherByCity.postValue(result)
                hideLoading()
            } else {
                return@launch Utils.showToast(
                    application,
                    application.getString(R.string.no_internet_connection_found)
                )
            }
        }
    }

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
