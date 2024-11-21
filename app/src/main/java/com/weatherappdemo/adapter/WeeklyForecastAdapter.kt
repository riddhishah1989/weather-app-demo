package com.weatherappdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.databinding.ItemForecastWeeklyBinding
import com.weatherappdemo.utils.LogUtils
import kotlin.math.roundToInt

class WeeklyForecastAdapter :
    RecyclerView.Adapter<WeeklyForecastAdapter.ForecastViewHolder>() {

    private lateinit var weeklyForecastList: List<ForecastData>
    private var minTemp = 0.0
    private var maxTemp = 30.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemForecastWeeklyBinding.inflate(inflater, parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val city = weeklyForecastList?.get(position)
        city?.let { holder.bind(it, minTemp, maxTemp) }
    }

    override fun getItemCount(): Int =
        if (::weeklyForecastList.isInitialized) weeklyForecastList.size else 0

    class ForecastViewHolder(private val binding: ItemForecastWeeklyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forecastData: ForecastData, minTemp: Double, maxTemp: Double) {
            binding.forecast = forecastData
            // Temperature range calculation
            val tempRange = (maxTemp - minTemp).coerceAtLeast(1.0) // Ensure valid range
            val adjustedProgress = ((forecastData.temperature - minTemp) / tempRange * 100).toInt()

            LogUtils.log("Bind - Temperature ${forecastData.temperature}, Min $minTemp, Max $maxTemp, Range $tempRange, Progress $adjustedProgress")

            binding.seekbar.apply {
                max = 100 // Fixed scale
                progress = adjustedProgress
            }
        }


    }

    fun addData(forecastDataList: List<ForecastData>) {
        this.weeklyForecastList = forecastDataList.map { forecastData ->
            forecastData.apply {
                minTemperature = minTemperature.roundToInt().toDouble()
                maxTemperature = maxTemperature.roundToInt().toDouble()
            }
        }
        // get min and max temperature from list
        minTemp = this.weeklyForecastList.minOfOrNull { it.minTemperature } ?: 0.0
        maxTemp = this.weeklyForecastList.maxOfOrNull { it.maxTemperature } ?: 0.0


        LogUtils.log("AddData - minTemp=$minTemp, maxTemp=$maxTemp")
        notifyDataSetChanged()
    }


}
