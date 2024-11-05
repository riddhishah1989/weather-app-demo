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
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.model.WeatherData
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        locationHelper = LocationHelper(requireActivity(), locationPermissionLauncher)

        setupRecyclerView()
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
                    addCurrentLocationWeatherData(currentLocationWeatherData)
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
    }

    private fun setupRecyclerView() {
        adapter = FavoriteCitiesAdapter()
        binding.rvFavCities.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvFavCities.adapter = adapter

        viewModel.getFavCitiesData() // Fetch favourite cities
    }

    private fun getWeatherDataByLocation(latitude: Double, longitude: Double) {
        viewModel.getCurrentLocationWeather(latitude, longitude)
    }

    private fun addCurrentLocationWeatherData(data: WeatherData) {
        viewModel.addSearchedCity(data)
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationHelper.checkAndRequestLocationPermission { (latitude, longitude) ->
                getWeatherDataByLocation(latitude, longitude)
            }
        } else {
            Utils.showToast(
                requireActivity(),
                "Permission denied. You won't be able to see the weather."
            )
        }
    }
}



