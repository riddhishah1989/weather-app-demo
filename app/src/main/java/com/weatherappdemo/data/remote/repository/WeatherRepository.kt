package com.weatherappdemo.data.remote.repository

import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.data.model.WeatherData
import com.weatherappdemo.data.model.toForecastDataModelList
import com.weatherappdemo.data.model.toWeatherData
import com.weatherappdemo.data.model.toWeeklyForecastDataModelList
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.data.remote.api.APIServices
import com.weatherappdemo.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(private val apiServices: APIServices) {

    private val application = MyApplication.instance

    suspend fun getCurrentWeather(lat: Double, lon: Double): APIResponse<WeatherData> =
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


    suspend fun getWeatherForecast(lat: Double, lon: Double): APIResponse<List<ForecastData>> =
        withContext(Dispatchers.IO)
        {
            try {
                val response = apiServices.getWeatherForecast(lat, lon)

                // Handle the response
                if (response.isSuccessful) {
                    response.body()?.let {
                        APIResponse.Success(it.toForecastDataModelList())
                    }
                        ?: APIResponse.Error(application.getString(R.string.no_data_available)) // Handle null body
                } else {
                    APIResponse.Error(application.getString(R.string.failed_to_fetch_weather))
                }
            } catch (e: Exception) {
                APIResponse.Error(application.getString(R.string.network_error, e.message))
            }
        }

    suspend fun getFiveDaysForecast(lat: Double, lon: Double): APIResponse<List<ForecastData>> {
        LogUtils.log("getFiveDaysForecast from repo $lat, $lon")
        return withContext(Dispatchers.IO) {
            try {
                val response = apiServices.getWeatherForecast(lat, lon)
                if (response.isSuccessful) {
                    response.body()?.let {
                        LogUtils.log("getFiveDaysForecast success")
                        APIResponse.Success(it.toWeeklyForecastDataModelList())
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
