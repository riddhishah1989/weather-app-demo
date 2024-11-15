package com.weatherappdemo.data.remote.repository

import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.local.utils.toWeatherDataList
import com.weatherappdemo.data.local.utils.toWeatherEntity
import com.weatherappdemo.data.model.WeatherData
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


    suspend fun addFavourite(weatherData: WeatherData): DBResponse<String> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val entity = weatherData.toWeatherEntity()
                LogUtils.log("lat = ${entity.latitude}, long = ${entity.longitude}")
                entity.isFavCity = true
                weatherDao.addFavouriteCity(entity)
                DBResponse.Success(
                    application.getString(
                        R.string.added_to_favourite_successfully,
                        weatherData.cityName
                    )
                )
            } catch (e: Exception) {
                DBResponse.Error(application.getString(R.string.error_adding_favourite, e.message))
            }
        }


    suspend fun getAllFavouriteCities(): DBResponse<List<WeatherData>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val tempList = weatherDao.getFavoriteCities()
                LogUtils.log("getAllFavouriteCities list size  =  ${tempList.size}")
                DBResponse.Success(tempList.toWeatherDataList())
            } catch (e: Exception) {
                DBResponse.Error(
                    application.getString(
                        R.string.error_while_fetching_favourite,
                        e.message
                    )
                )

            }
        }

    suspend fun getOfflineWeatherData(): DBResponse<List<WeatherData>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val tempList = weatherDao.getAllWeatherData()
                if (tempList.isNotEmpty()) {
                    DBResponse.Success(tempList.toWeatherDataList())
                } else {
                    DBResponse.Error(application.getString(R.string.no_data_available))
                }
            } catch (e: Exception) {

                DBResponse.Error(
                    application.getString(
                        R.string.error_while_fetching_data,
                        e.message
                    )
                )
            }
        }

    suspend fun addSearchedCity(weatherData: WeatherData): DBResponse<String> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val entity = weatherData.toWeatherEntity()
                entity.longitude = weatherData.longitude
                entity.latitude = weatherData.latitude
                LogUtils.log("entity $entity")
                weatherDao.addWeatherData(entity)
                DBResponse.Success("added successfully")
            } catch (e: Exception) {
                DBResponse.Error(application.getString(R.string.error_while_adding_data, e.message))
            }

        }


}
