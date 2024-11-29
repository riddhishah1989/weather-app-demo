package com.weatherappdemo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.weatherappdemo.data.local.entities.ForecastEntity
import com.weatherappdemo.data.local.entities.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchCityData(weather: WeatherEntity)

    @Query("SELECT * FROM weather_table WHERE latitude = :lat AND longitude = :lon AND isCurrentLocation = 0 LIMIT 1")
    suspend fun getSearchedCityData(lat: Double, lon: Double): WeatherEntity?

    @Query("SELECT * FROM weather_table WHERE isCurrentLocation = 0")
    suspend fun getAllSearchedCityList(): List<WeatherEntity>

    @Update
    suspend fun updateSearchCityData(weather: WeatherEntity)

    @Query("SELECT * FROM weather_table WHERE latitude = :latitude AND longitude = :longitude AND isCurrentLocation = 1")
    suspend fun getCurrentLocationWeatherData(latitude: Double, longitude: Double): WeatherEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCurrentLocationWeather(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastData(forecastList: List<ForecastEntity>)

    @Query("SELECT * FROM forecast_table WHERE latitude = :latitude AND longitude = :longitude ORDER BY dateTime ASC")
    suspend fun getForecastListByLatLng(latitude: Double, longitude: Double): List<ForecastEntity>
}

