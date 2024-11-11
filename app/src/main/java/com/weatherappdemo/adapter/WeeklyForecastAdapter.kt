package com.weatherappdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weatherappdemo.R
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.databinding.ItemForecastWeeklyBinding

class WeeklyForecastAdapter : RecyclerView.Adapter<WeeklyForecastAdapter.ForecastViewHolder>() {

    private var weeklyForecastList: List<ForecastData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemForecastWeeklyBinding.inflate(inflater, parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val city = weeklyForecastList?.get(position)
        city?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = weeklyForecastList?.size ?: 0

    class ForecastViewHolder(private val binding: ItemForecastWeeklyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forecastData: ForecastData) {
            binding.forecast = forecastData
            val degreeSymbol = itemView.context.getString(R.string.degree_symbol_celcious)
            binding.tvTemperature.text = buildString {
                append(forecastData.temperature)
                append(degreeSymbol)
            }
        }
    }

    fun addData(forecastDataList: List<ForecastData>) {
        this.weeklyForecastList = forecastDataList
        notifyDataSetChanged()
    }
}
