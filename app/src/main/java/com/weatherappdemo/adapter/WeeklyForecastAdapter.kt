package com.weatherappdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weatherappdemo.R
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.databinding.ItemForecastWeeklyBinding
import com.weatherappdemo.utils.LogUtils
import com.weatherappdemo.utils.Utils
import kotlin.math.roundToInt

class WeeklyForecastAdapter :
    RecyclerView.Adapter<WeeklyForecastAdapter.ForecastViewHolder>() {

    private var weeklyForecastList: List<ForecastData>? = null
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

    override fun getItemCount(): Int = weeklyForecastList?.size ?: 0

    class ForecastViewHolder(private val binding: ItemForecastWeeklyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forecastData: ForecastData, minTemp: Double, maxTemp: Double) {
            binding.forecast = forecastData
            binding.degreeSymbol = itemView.context.getString(R.string.degree_symbol_celcious)
            binding.iconURL = Utils.getWeatherIconUrl(forecastData.icon)
            binding.seekbar.apply {
                max = (maxTemp - minTemp).roundToInt()
                progress = (forecastData.temperature - minTemp).roundToInt()
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
        minTemp = this.weeklyForecastList?.minOf { it.minTemperature } ?: 0.0
        maxTemp = this.weeklyForecastList?.maxOf { it.maxTemperature } ?: 0.0
        LogUtils.log("minTemp=$minTemp, maxTemp=$maxTemp")
        notifyDataSetChanged()
    }

}
