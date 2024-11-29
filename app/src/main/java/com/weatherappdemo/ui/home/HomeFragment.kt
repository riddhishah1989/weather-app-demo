package com.weatherappdemo.ui.home

import android.content.Intent
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
import com.weatherappdemo.data.model.WeatherDataModel
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.databinding.FragmentHomeBinding
import com.weatherappdemo.ui.customView.CustomProgressDialog
import com.weatherappdemo.ui.forecast.ForecastDetailsActivity
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
    private lateinit var currentLocationWeather: WeatherDataModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        progressDialog = CustomProgressDialog(requireActivity())
        adapter = SearchCitiesAdapter(this)
        forecastAdapter = WeeklyForecastAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        setupRecyclerViews()
        binding.currentLocationWeatherCV.setOnClickListener {
            val intent = Intent(requireActivity(), ForecastDetailsActivity::class.java)
            intent.putExtra("weatherData", currentLocationWeather)
            startActivity(intent)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocationData()
        setUpObservers()
    }


    /**
     * Requesting location permission from user
     * */
    override fun onResume() {
        super.onResume()
        locationHelper.checkAndRequestLocationPermission()

    }

    /**
     * After receiving location permission, fetching Latitude & Longitude
     * 1. setup observers for live updates
     * 2. calling apis to get current location weather
     * 3. 5 days forecast for current location
     * */
    private fun getLocationData() {
        locationHelper = LocationHelper(
            requireActivity(),
            requestPermissionLauncher
        ) { latitude, longitude ->
            //Utils.showToast(requireActivity(), "Lat = $latitude, long = $longitude")
            setUpObservers()
            viewModel.fetchAndSaveCurrentLocationWeather(latitude, longitude)
            viewModel.fetchAndSaveFiveDaysForecast(latitude, longitude)
            viewModel.getAllSearchedCitiesList() // Fetch searched cities
        }
    }

    /**
     * setup observers for live updates and update UI
     *  */
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
                    currentLocationWeather.apply {
                        temperature = temperature.roundToInt().toDouble()
                        feelsLike = feelsLike.roundToInt().toDouble()
                        tempMax = tempMax.roundToInt().toDouble()
                        tempMin = tempMin.roundToInt().toDouble()
                    }

                    binding.weatherData = currentLocationWeather
                }

                is APIResponse.Error -> {
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        }

        // Observe searched cities
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
                    binding.tvNoDataFound.visibility = View.VISIBLE
                    Utils.showToast(
                        requireActivity(),
                        "getSearchedCitiesList Error: " + dbResponse.message
                    )
                }

                else -> {
                    binding.tvNoDataFound.visibility = View.VISIBLE
                    LogUtils.log(message = "favCitiesList else called")
                }
            }
        }

        //forcast for next 5 days
        viewModel.fiveDaysForecast.observe(viewLifecycleOwner) { apiResponse ->
            progressDialog.dismiss()
            when (apiResponse) {
                is APIResponse.Success -> {
                    forecastAdapter.addData(apiResponse.data)
                }

                is APIResponse.Error -> {
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        }
    }


    private fun setupRecyclerViews() {
        binding.rvSearchCities.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvSearchCities.adapter = adapter

        binding.rvUpcomingForcast.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvUpcomingForcast.adapter = forecastAdapter
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

    /**
     * Removing location updates
     *  */
    override fun onStop() {
        super.onStop()
        locationHelper.stopLocationUpdates() // Stop updates
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.getCurrentWeather.removeObservers(viewLifecycleOwner)
        viewModel.getSearchedCitiesList.removeObservers(viewLifecycleOwner)
        viewModel.fiveDaysForecast.removeObservers(viewLifecycleOwner)
    }


    /**
     * Click on Search City Item to see full weather details for that location.
     * *  */
    override fun onItemClick(data: WeatherDataModel) {
        val intent = Intent(requireActivity(), ForecastDetailsActivity::class.java)
        intent.putExtra("weatherData", data)
        startActivity(intent)
    }
}