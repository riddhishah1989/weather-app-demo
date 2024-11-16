package com.weatherappdemo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.weatherappdemo.R
import com.weatherappdemo.adapter.SearchCitiesAdapter
import com.weatherappdemo.adapter.WeeklyForecastAdapter
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.model.WeatherData
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.databinding.FragmentHomeBinding
import com.weatherappdemo.ui.customView.CustomProgressDialog
import com.weatherappdemo.utils.CustomInterfaces
import com.weatherappdemo.utils.LocationHelper
import com.weatherappdemo.utils.LogUtils
import com.weatherappdemo.utils.Utils
import com.weatherappdemo.viewmodel.WeatherViewModel
import kotlin.math.roundToInt

class HomeFragment : Fragment(), CustomInterfaces.OnSearchedCityItemClick {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var locationHelper: LocationHelper
    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: SearchCitiesAdapter
    private lateinit var forecastAdapter: WeeklyForecastAdapter
    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var currentLocationWeather: WeatherData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        progressDialog = CustomProgressDialog(requireActivity())
        init()
        return binding.root
    }

    private fun init() {
        getLocationData()
        setupSearchedCitiesRecyclerView()
        setupForecastRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        locationHelper.checkAndRequestLocationPermission()
    }

    private fun getLocationData() {
        locationHelper = LocationHelper(
            requireActivity(),
            requestPermissionLauncher
        ) { latitude, longitude ->
            Utils.showToast(requireActivity(), "Lat = $latitude, long = $longitude")
            setUpObservers()
            getWeatherDataByLocation(latitude, longitude)
            getFiveDaysForecastData(latitude, longitude)
        }
    }

    private fun setUpObservers() {
        progressDialog.show()

        // Observe current weather data
        viewModel.getCurrentWeather.observe(viewLifecycleOwner) { apiResponse ->
            progressDialog.dismiss()
            LogUtils.log(message = "Observer called and response $apiResponse")

            if (apiResponse == null) {
                Utils.showToast(requireActivity(), getString(R.string.no_data_available))
                return@observe
            }
            when (apiResponse) {
                is APIResponse.Success -> {
                    currentLocationWeather = apiResponse.data
                    LogUtils.log(message = "Data = $currentLocationWeather")

                    currentLocationWeather.apply {
                        temperature = temperature.roundToInt().toDouble()
                    }

                    binding.weatherData = currentLocationWeather
                    val imageUrl = Utils.getWeatherIconUrl(currentLocationWeather.icon)
                    LogUtils.log("URL = $imageUrl")
                    binding.weatherIconUrl = imageUrl

                    binding.textDay.text = Utils.showDayFromCurrentDate()
                }

                is APIResponse.Error -> {
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        }

        // Observe favorite cities
        viewModel.getSearchedCitiesList.observe(viewLifecycleOwner) { dbResponse ->
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

        //forcast for next 5 days
        viewModel.fiveDaysForecast.observe(viewLifecycleOwner) { apiResponse ->
            progressDialog.dismiss()
            when (apiResponse) {
                is APIResponse.Success -> {
                    // Update RecyclerView with the weekly forecast
                    LogUtils.log("Size ${apiResponse.data.size}")
                    forecastAdapter.addData(apiResponse.data)
                }

                is APIResponse.Error -> {
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        }
    }

    private fun setupSearchedCitiesRecyclerView() {
        adapter = SearchCitiesAdapter(this)
        binding.rvSearchCities.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvSearchCities.adapter = adapter

        viewModel.getAllSearchedCitiesList() // Fetch favourite cities


    }

    private fun setupForecastRecyclerView() {
        forecastAdapter = WeeklyForecastAdapter()
        binding.rvUpcomingForcast.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvUpcomingForcast.adapter = forecastAdapter
    }

    private fun getWeatherDataByLocation(latitude: Double, longitude: Double) {
        viewModel.getCurrentLocationWeather(latitude, longitude)
    }

    private fun getFiveDaysForecastData(latitude: Double, longitude: Double) {
        LogUtils.log("getFiveDaysForecastData() $latitude, $longitude")
        viewModel.fetchFiveDaysForecast(latitude, longitude)
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationHelper.checkAndRequestLocationPermission()
        } else {
            Utils.showToast(requireActivity(), "Location permission denied")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHelper.stopLocationUpdates()
    }

    override fun onItemClick(data: WeatherData) {
        Utils.showToast(requireActivity(), "Navigation to details")
    }
}



