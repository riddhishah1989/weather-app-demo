package com.weatherappdemo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.weatherappdemo.R
import com.weatherappdemo.adapter.FavoriteCitiesAdapter
import com.weatherappdemo.adapter.WeeklyForecastAdapter
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.databinding.FragmentHomeBinding
import com.weatherappdemo.utils.LocationHelper
import com.weatherappdemo.utils.LogUtils
import com.weatherappdemo.utils.Utils
import com.weatherappdemo.viewmodel.WeatherViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var locationHelper: LocationHelper
    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: FavoriteCitiesAdapter
    private lateinit var forecastAdapter: WeeklyForecastAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        locationHelper = LocationHelper(requireActivity(), locationPermissionLauncher)

        setupFavCitiesRecyclerView()
        setupForecastRecyclerView()
        setUpObservers()

        return binding.root
    }

    private fun setUpObservers() {
        // Observe current weather data
        viewModel.getCurrentWeather.observe(viewLifecycleOwner) { apiResponse ->
            LogUtils.log(message = "Observer called and response $apiResponse")
            if (apiResponse == null) {
                Utils.showToast(requireActivity(), getString(R.string.no_data_available))
                return@observe
            }
            when (apiResponse) {
                is APIResponse.Success -> {
                    LogUtils.log(message = "Success called")
                    val currentLocationWeatherData = apiResponse.data
                    LogUtils.log(message = "Data = $currentLocationWeatherData")
                    binding.weatherData = currentLocationWeatherData
                    binding.weatherIconUrl =
                        Utils.getWeatherIconUrl(currentLocationWeatherData.icon)
                    binding.textDay.text = Utils.showDayFromCurrentDate()
                }

                is APIResponse.Error -> {
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        }

        // Observe favorite cities
        viewModel.favCitiesList.observe(viewLifecycleOwner) { dbResponse ->
            when (dbResponse) {
                is DBResponse.Success -> {
                    if (dbResponse.data.isNotEmpty()) {
                        binding.tvNoDataFound.visibility = View.GONE
                        adapter.addData(dbResponse.data)
                    } else {
                        binding.tvNoDataFound.visibility = View.VISIBLE
                    }
                }

                is DBResponse.Error -> {
                    Utils.showToast(requireActivity(), dbResponse.message)
                }

                else -> {
                    LogUtils.log(message = "favCitiesList else called")
                }
            }
        }


        viewModel.weeklyForecast.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is APIResponse.Success -> {
                    // Update RecyclerView with the weekly forecast
                    forecastAdapter.addData(apiResponse.data)
                }

                is APIResponse.Error -> {
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        }
    }

    private fun setupFavCitiesRecyclerView() {
        adapter = FavoriteCitiesAdapter()
        binding.rvFavCities.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvFavCities.adapter = adapter

        viewModel.getFavCitiesData() // Fetch favourite cities


    }

    private fun setupForecastRecyclerView() {
        forecastAdapter = WeeklyForecastAdapter()
        binding.rvUpcomingForcast.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvUpcomingForcast.adapter = forecastAdapter
    }

    private fun getWeatherDataByLocation(latitude: Double, longitude: Double) {
        viewModel.getCurrentLocationWeather(latitude, longitude)
    }

    private fun getWeeklyForecastData(latitude: Double, longitude: Double) {
        viewModel.fetchWeeklyForecast(latitude, longitude)
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationHelper.checkAndRequestLocationPermission { (latitude, longitude) ->
                getWeatherDataByLocation(latitude, longitude)
                getWeeklyForecastData(latitude, longitude)
            }
        } else {
            Utils.showToast(
                requireActivity(),
                getString(R.string.location_permission_message)
            )
        }
    }
}



