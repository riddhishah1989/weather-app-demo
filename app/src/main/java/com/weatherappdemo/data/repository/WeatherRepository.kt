package com.weatherappdemo.data.repository

import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.data.model.WeatherDataModel
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.data.remote.api.APIServices
import com.weatherappdemo.data.utils.toForecastEntityList
import com.weatherappdemo.data.utils.toWeatherData
import com.weatherappdemo.data.utils.toWeeklyForecastDataModelList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(private val apiServices: APIServices) {

    private val application = MyApplication.instance
    private val localRepository = WeatherLocalRepository.getInstance()

    suspend fun getCurrentLocationWeather(lat: Double, lon: Double): APIResponse<WeatherDataModel> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiServices.getCurrentLocationWeather(lat, lon)
                if (response.isSuccessful) {
                    response.body()?.let {
                        APIResponse.Success(it.toWeatherData()) // Map to WeatherData
                    } ?: APIResponse.Error(application.getString(R.string.no_data_available))
                } else {
                    APIResponse.Error(application.getString(R.string.failed_to_fetch_weather))
                }
            } catch (e: Exception) {
                APIResponse.Error(application.getString(R.string.network_error, e.message))
            }
        }


    /**
     * Returns 5 days data
     */

    suspend fun getFiveDaysForecast(lat: Double, lon: Double): APIResponse<MutableList<ForecastData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiServices.get5DayForecast(lat, lon)
                if (response.isSuccessful) {
                    response.body()?.let {
                        //adding a 5 days forecast to the table
                        localRepository.add5DayForecastData(it.toForecastEntityList(lat, lon))
                        APIResponse.Success(it.toWeeklyForecastDataModelList().toMutableList())
                    } ?: APIResponse.Error(application.getString(R.string.no_data_available))
                } else {
                    APIResponse.Error(application.getString(R.string.failed_to_fetch_weather))
                }
            } catch (e: Exception) {
                APIResponse.Error(application.getString(R.string.network_error, e.message))
            }
        }
    }


    /**
     * Returns weather info by city details
     */

    suspend fun getWeatherByCityName(cityName: String): APIResponse<WeatherDataModel> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiServices.getWeatherForecastByCity(cityName)
                if (response.isSuccessful) {
                    response.body()?.let {
                        APIResponse.Success(it.toWeatherData())
                    } ?: APIResponse.Error(application.getString(R.string.no_data_available))
                } else {
                    APIResponse.Error(application.getString(R.string.failed_to_fetch_weather))
                }
            } catch (e: Exception) {
                APIResponse.Error(application.getString(R.string.network_error, e.message))
            }
        }
    }

}
