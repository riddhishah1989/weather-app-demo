package com.weatherappdemo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.weatherappdemo.data.local.entities.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchCityData(weather: WeatherEntity)

    @Query("SELECT * FROM weather_table")
    suspend fun getAllWeatherData(): List<WeatherEntity>

    @Query("SELECT * FROM weather_table WHERE latitude = :lat AND longitude = :lon LIMIT 1")
    suspend fun getSearchedCityWeatherData(lat: Double, lon: Double): WeatherEntity?

    @Update
    suspend fun updateSearchCityData(weather: WeatherEntity)

    @Query("SELECT * FROM weather_table WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun getCityWeatherByLatLng(latitude: Double, longitude: Double): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWeather(weather: WeatherEntity)

}

