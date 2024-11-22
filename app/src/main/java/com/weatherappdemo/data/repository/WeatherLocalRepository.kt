package com.weatherappdemo.data.repository

import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.local.entities.WeatherEntity
import com.weatherappdemo.data.model.WeatherDataModel
import com.weatherappdemo.data.utils.toWeatherDataList
import com.weatherappdemo.data.utils.toWeatherEntity
import com.weatherappdemo.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherLocalRepository private constructor() {

    private val application = MyApplication.instance
    private val weatherDao = MyApplication.weatherDao

    companion object {
        @Volatile
        private var instance: WeatherLocalRepository? = null

        fun getInstance(): WeatherLocalRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherLocalRepository().also { instance = it }
            }
        }
    }

    suspend fun getAllSearchedCitiesList(): DBResponse<List<WeatherDataModel>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val tempList = weatherDao.getAllWeatherData()
                DBResponse.Success(tempList.toWeatherDataList())
            } catch (e: Exception) {

                DBResponse.Error(
                    application.getString(
                        R.string.error_while_fetching_data,
                        e.message
                    )
                )
            }
        }

    suspend fun addSearchedCity(weatherData: WeatherDataModel): DBResponse<String> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val entity = weatherData.toWeatherEntity()
                val existingEntry =
                    weatherDao.getSearchedCityWeatherData(weatherData.latitude, weatherData.longitude)

                if (existingEntry != null) {
                    entity.id = existingEntry.id // Keep the same ID for the update
                    weatherDao.updateSearchCityData(entity)
                } else {
                    weatherDao.addSearchCityData(entity)
                }

                DBResponse.Success("Weather data added/updated successfully")
            } catch (e: Exception) {
                DBResponse.Error(application.getString(R.string.error_while_adding_data, e.message))
            }
        }

    suspend fun getWeatherByLatLng(latitude: Double, longitude: Double): WeatherEntity? {
        return weatherDao.getCityWeatherByLatLng(latitude, longitude)
    }

    suspend fun saveWeatherData(weatherEntity: WeatherEntity) {
        weatherDao.insertOrUpdateWeather(weatherEntity)
    }



}
