package com.weatherappdemo.data.remote.repository

import com.example.yourapp.utils.toWeatherDataList
import com.example.yourapp.utils.toWeatherEntity
import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.model.WeatherData
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
                DBResponse.Success(tempList.toWeatherDataList())
                /*if (tempList.isNotEmpty()) {
                    DBResponse.Success(tempList.toWeatherDataList())
                } else {
                    DBResponse.Error(application.getString(R.string.no_favourite_cities_found))
                }*/
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
                weatherDao.addWeatherData(entity)
                DBResponse.Success("added successfully")
            } catch (e: Exception) {
                DBResponse.Error(application.getString(R.string.error_while_adding_data, e.message))
            }

        }


}
