package com.weatherappdemo.data.repository

import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.local.entities.ForecastEntity
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

    suspend fun getAllSearchedCitiesList(): DBResponse<MutableList<WeatherDataModel>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val tempList = weatherDao.getAllSearchedCityList()
                LogUtils.log("getAllSearchedCitiesList tempList Size: ${tempList.size}")
                DBResponse.Success(tempList.toWeatherDataList().toMutableList())
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
                entity.isCurrentLocation = false
                val existingEntry =
                    weatherDao.getSearchedCityData(
                        weatherData.latitude,
                        weatherData.longitude
                    )

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

    suspend fun getCurrentLocationWeather(
        latitude: Double,
        longitude: Double
    ): DBResponse<WeatherEntity> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val entity = weatherDao.getCurrentLocationWeatherData(latitude, longitude)
                DBResponse.Success(entity)
            } catch (e: Exception) {
                DBResponse.Error(application.getString(R.string.error_while_adding_data, e.message))
            }
        }


    suspend fun saveCurrentLocationData(weatherEntity: WeatherEntity): DBResponse<String> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                weatherEntity.isCurrentLocation = true
                weatherDao.insertOrUpdateCurrentLocationWeather(weatherEntity)
                DBResponse.Success("Weather data added/updated successfully")
            } catch (e: Exception) {
                DBResponse.Error(
                    application.getString(
                        R.string.error_while_adding_data,
                        e.message
                    )
                )
            }
        }

    suspend fun add5DayForecastData(list: List<ForecastEntity>): DBResponse<String> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                weatherDao.insertForecastData(list)

                DBResponse.Success("Weather data added/updated successfully")
            } catch (e: Exception) {
                DBResponse.Error(
                    application.getString(
                        R.string.error_while_adding_data,
                        e.message
                    )
                )
            }
        }

    suspend fun get5DaysForecastFromDB(
        latitude: Double,
        longitude: Double
    ): DBResponse<List<ForecastEntity>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val tempList = weatherDao.getForecastListByLatLng(latitude, longitude)
                DBResponse.Success(tempList)
            } catch (e: Exception) {
                DBResponse.Error(
                    application.getString(
                        R.string.error_while_adding_data,
                        e.message
                    )
                )
            }
        }
}

