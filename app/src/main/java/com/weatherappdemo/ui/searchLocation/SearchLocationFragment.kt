package com.weatherappdemo.ui.searchLocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.weatherappdemo.R
import com.weatherappdemo.adapter.WeeklyForecastAdapter
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.databinding.FragmentSearchLocationBinding
import com.weatherappdemo.ui.customView.CustomProgressDialog
import com.weatherappdemo.utils.LogUtils
import com.weatherappdemo.utils.Utils
import com.weatherappdemo.viewmodel.WeatherViewModel
import kotlin.math.roundToInt

class SearchLocationFragment : Fragment() {

    private lateinit var binding: FragmentSearchLocationBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var forecastAdapter: WeeklyForecastAdapter
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search_location, container, false)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        progressDialog = CustomProgressDialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setupForecastRecyclerView()
        setUpObservers()

        binding.edtSearchCity.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString()
                if (query.isNotEmpty()) {
                    Utils.hideKeyboard(requireActivity())
                    progressDialog.show()
                    viewModel.getWeatherDataByCity(query)
                }
                true // Consume the event
            } else {
                false // Pass the event further
            }
        }

    }


    private fun setupForecastRecyclerView() {
        forecastAdapter = WeeklyForecastAdapter()
        binding.rvForecastData.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvForecastData.adapter = forecastAdapter
    }


    private fun setUpObservers() {
        // Observe current weather data
        viewModel.getWeatherByCity.observe(viewLifecycleOwner) { apiResponse ->
            progressDialog.dismiss()
            LogUtils.log(message = "Observer called and response $apiResponse")

            if (apiResponse == null) {
                Utils.showToast(requireActivity(), getString(R.string.no_data_available))
                return@observe
            }
            when (apiResponse) {
                is APIResponse.Success -> {
                    val currentLocationWeather = apiResponse.data
                    LogUtils.log(message = "Data = $currentLocationWeather")

                    //API calling

                    viewModel.fetchFiveDaysForecast(
                        currentLocationWeather.latitude,
                        currentLocationWeather.longitude
                    )
                    //added searched city
                    viewModel.addSearchedCity(currentLocationWeather)

                    currentLocationWeather.apply {
                        temperature = temperature.roundToInt().toDouble()
                        feelsLike = feelsLike.roundToInt().toDouble()
                        tempMin = tempMin.roundToInt().toDouble()
                        tempMax = tempMax.roundToInt().toDouble()
                    }
                    binding.weatherData = currentLocationWeather
                    binding.weatherDetailsLayout.tvTemprature.text =
                        buildString {
                            append(
                                Utils.formatTemperature(
                                    requireActivity(),
                                    currentLocationWeather.temperature
                                )
                            )
                            append(R.string.degree_symbol_celcious)
                        }
                    binding.weatherDetailsLayout.tvSunrise.text =
                        Utils.convertUnixToAmPm(currentLocationWeather.sunrise)
                    binding.weatherDetailsLayout.tvSunset.text =
                        Utils.convertUnixToAmPm(currentLocationWeather.sunset)
                }

                is APIResponse.Error -> {
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        }

        //forcast for next 5 days
        viewModel.fiveDaysForecast.observe(viewLifecycleOwner) { apiResponse ->
            progressDialog.dismiss()
            binding.rlForecast.visibility = View.VISIBLE
            when (apiResponse) {
                is APIResponse.Success -> {
                    // Update RecyclerView with the weekly forecast
                    LogUtils.log("Size ${apiResponse.data.size}")
                    if (apiResponse.data.isNotEmpty()) {
                        binding.tvNoDataFoundForecast.visibility = View.GONE

                        forecastAdapter.addData(apiResponse.data)
                    } else {
                        binding.tvNoDataFoundForecast.visibility = View.VISIBLE
                    }
                }

                is APIResponse.Error -> {
                    binding.tvNoDataFoundForecast.visibility = View.VISIBLE
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        }
    }


}