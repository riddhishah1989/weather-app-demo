package com.weatherappdemo.data.remote.repository

import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.data.model.WeatherDataModel
import com.weatherappdemo.data.model.toForecastDataModelList
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.data.remote.api.APIServices
import com.weatherappdemo.data.utils.toWeatherData
import com.weatherappdemo.data.utils.toWeeklyForecastDataModelList
import com.weatherappdemo.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(private val apiServices: APIServices) {

    private val application = MyApplication.instance

    suspend fun getCurrentWeather(lat: Double, lon: Double): APIResponse<WeatherDataModel> =
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
     * Returns all 40 hours data
     */

    suspend fun getAllWeatherForecast(lat: Double, lon: Double): APIResponse<List<ForecastData>> =
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

    /**
     * Returns 5 days data
     */

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
