package com.weatherappdemo.ui.searchLocation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.weatherappdemo.data.local.DBResponse
import com.weatherappdemo.data.model.WeatherDataModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        progressDialog = CustomProgressDialog(requireActivity())
        forecastAdapter = WeeklyForecastAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search_location, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    /**
     * Initialise for live data, setup RecyclerViews
     * */
    private fun init() {
        binding.ivClear.visibility = View.GONE
        setUpObservers()
        setupForecastRecyclerView()

        binding.edtSearchCity.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.ivClear.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
            setOnEditorActionListener { textView, actionId, _ ->
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

        binding.ivClear.setOnClickListener {
            onResetView()
        }
    }

    /**
     * Initialise recylerview for 5 days forecast list.
     *  */
    private fun setupForecastRecyclerView() {
        binding.rvForecastData.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
            isSmoothScrollbarEnabled = true
        }
        binding.rvForecastData.adapter = forecastAdapter
    }

    /**
     * setup observers for live updates and update UI
     *  */
    private fun setUpObservers() {
        // Observe current weather data
        viewModel.getWeatherByCity.observe(viewLifecycleOwner) { apiResponse ->
            progressDialog.dismiss()

            binding.edtSearchCity.text = null

            if (apiResponse == null) {
                Utils.showToast(requireActivity(), getString(R.string.no_data_available))
                return@observe
            }
            when (apiResponse) {
                is APIResponse.Success -> {
                    setUpSearchCityResponse(apiResponse)
                }

                is APIResponse.Error -> {
                    Utils.showToast(requireActivity(), apiResponse.message)
                }
            }
        }


        //add search city
        viewModel.addSearchedCity.observe(viewLifecycleOwner) { dbResponse ->
            when (dbResponse) {
                is DBResponse.Error -> Utils.showToast(requireActivity(), dbResponse.message)
                is DBResponse.Success -> LogUtils.log(dbResponse.data)
            }
        }

        //forcast for next 5 days
        viewModel.cityFiveDaysForecast.observe(viewLifecycleOwner) { apiResponse ->
            progressDialog.dismiss()
            binding.rlForecast.visibility = View.VISIBLE
            when (apiResponse) {
                is APIResponse.Success -> {
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

    /**
     * setup Search City API response
     *  */
    private fun setUpSearchCityResponse(apiResponse: APIResponse.Success<WeatherDataModel>) {
        val weatherData = apiResponse.data

        //added searched city
        viewModel.addSearchedCity(weatherData)

        //API calling
        viewModel.fetchCityFiveDaysForecast(weatherData.latitude, weatherData.longitude)

        weatherData.apply {
            temperature = temperature.roundToInt().toDouble()
            feelsLike = feelsLike.roundToInt().toDouble()
            tempMin = tempMin.roundToInt().toDouble()
            tempMax = tempMax.roundToInt().toDouble()
        }
        binding.weatherData = weatherData
        binding.weatherDetailsLayout.tvSunrise.text =
            Utils.convertUnixToAmPm(
                weatherData.sunrise,
                weatherData.timezone
            )
        binding.weatherDetailsLayout.tvSunset.text =
            Utils.convertUnixToAmPm(
                weatherData.sunset,
                weatherData.timezone
            )
    }

    override fun onResume() {
        super.onResume()
        onResetView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cityFiveDaysForecast.removeObservers(viewLifecycleOwner)
        viewModel.addSearchedCity.removeObservers(viewLifecycleOwner)
        viewModel.getWeatherByCity.removeObservers(viewLifecycleOwner)
    }

    private fun onResetView() {
        binding.weatherData = null
        binding.edtSearchCity.text = null
        forecastAdapter.clearData()
        Utils.hideKeyboard(requireActivity())

    }

}