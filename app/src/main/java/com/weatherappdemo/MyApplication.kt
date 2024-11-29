package com.weatherappdemo

import android.app.Application
import androidx.room.Room
import com.weatherappdemo.data.local.AppDatabase
import com.weatherappdemo.data.local.dao.WeatherDao
import com.weatherappdemo.utils.SharedPreferencesUtils

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication

        private val database: AppDatabase by lazy {
            Room.databaseBuilder(
                instance.applicationContext,
                AppDatabase::class.java, "weather_database"
            ).fallbackToDestructiveMigration()
                .build()
        }

        val weatherDao: WeatherDao by lazy {
            database.weatherDao()
        }


    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        SharedPreferencesUtils.init(this)
    }


}