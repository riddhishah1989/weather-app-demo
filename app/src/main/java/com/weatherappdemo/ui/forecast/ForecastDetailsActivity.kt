package com.weatherappdemo.ui.forecast

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.weatherappdemo.R
import com.weatherappdemo.adapter.WeeklyForecastAdapter
import com.weatherappdemo.data.model.WeatherDataModel
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.databinding.ActivityForecastDetailsBinding
import com.weatherappdemo.utils.Utils
import com.weatherappdemo.viewmodel.WeatherViewModel

class ForecastDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForecastDetailsBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var weatherData: WeatherDataModel
    private lateinit var forecastAdapter: WeeklyForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forecast_details)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        getDataFromIntent()
        init()
    }

    private fun getDataFromIntent() {
        if (intent != null && intent.hasExtra("weatherData")) {
            weatherData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(
                    "weatherData",
                    WeatherDataModel::class.java
                ) as WeatherDataModel
            } else {
                intent.getSerializableExtra("weatherData") as WeatherDataModel
            }
        }
    }

    private fun init() {
        binding.topBar.ivBack.setOnClickListener {
            finish()
        }
        binding.weatherData = weatherData
        binding.weatherDetailsLayout.tvSunrise.text =
            Utils.convertUnixToAmPm(weatherData.sunrise, weatherData.timezone)
        binding.weatherDetailsLayout.tvSunset.text =
            Utils.convertUnixToAmPm(weatherData.sunset, weatherData.timezone)
        setupForecastRecyclerView()
        setUpObservers()
        viewModel.fetchCityFiveDaysForecast(weatherData.latitude, weatherData.longitude)
    }

    private fun setupForecastRecyclerView() {
        forecastAdapter = WeeklyForecastAdapter()
        binding.rvForecastData.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvForecastData.adapter = forecastAdapter
    }

    private fun setUpObservers() {
        viewModel.cityFiveDaysForecast.observe(this) { apiResponse ->
            when (apiResponse) {
                is APIResponse.Success -> {
                    forecastAdapter.addData(apiResponse.data)
                }

                is APIResponse.Error -> {
                    Utils.showToast(this, apiResponse.message)
                }
            }
        }
    }
}