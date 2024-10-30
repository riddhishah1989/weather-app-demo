package com.weatherappdemo.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object SharedPreferencesUtils {

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    private const val PREFS_NAME = "my_prefs"

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
