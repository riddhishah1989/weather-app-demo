package com.weatherappdemo.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.weatherappdemo.R
import com.weatherappdemo.adapter.FavoriteCitiesAdapter
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.databinding.FragmentHomeBinding
import com.weatherappdemo.utils.Utils
import com.weatherappdemo.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: FavoriteCitiesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupRecyclerView()
        checkLocationPermission()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = FavoriteCitiesAdapter()
        binding.rvFavCities.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvFavCities.adapter = adapter

        // Observe favourite cities from the ViewModel
        viewModel.favCitiesList.observe(viewLifecycleOwner, Observer { dbResponse ->
            when (dbResponse) {
                is DBResponse.Success -> {
                    adapter.addData(dbResponse.data)
                }

                is DBResponse.Error -> {
                    Utils.showToast(requireActivity(), dbResponse.message)
                }
            }
        })
        viewModel.getFavCitiesData() // Fetch favourite cities
    }

    // Check Location Permission using ActivityResultLauncher
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            // Request permission
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Fetch current location using FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                getWeatherDataByLocation(location.latitude, location.longitude)
            } else {
                Utils.showToast(requireActivity(), getString(R.string.unable_to_fetch_location))
            }
        }.addOnFailureListener {
            Utils.showToast(
                requireActivity(),
                getString(R.string.failed_to_get_location, it.message)
            )
        }
    }

    // Permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Utils.showToast(requireActivity(), getString(R.string.location_permission_denied))
        }
    }

    // Observe Weather Data
    private fun getWeatherDataByLocation(latitude: Double, longitude: Double) {
        viewModel.getCurrentWeather.observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is APIResponse.Success -> {
                    binding.weatherData = apiResponse.data.data
                    binding.textDay.text =
                        SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
                }

                is APIResponse.Error -> {
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        })
        viewModel.getCurrentLocationWeather(latitude, longitude)
    }
}
