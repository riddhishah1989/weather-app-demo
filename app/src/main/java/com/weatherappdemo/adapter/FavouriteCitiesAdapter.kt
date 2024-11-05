package com.weatherappdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weatherappdemo.R
import com.weatherappdemo.data.model.WeatherData
import com.weatherappdemo.databinding.ItemFavouriteCityBinding

class FavoriteCitiesAdapter : RecyclerView.Adapter<FavoriteCitiesAdapter.FavoriteCityViewHolder>() {

    private var cities: List<WeatherData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteCityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFavouriteCityBinding.inflate(inflater, parent, false)
        return FavoriteCityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteCityViewHolder, position: Int) {
        val city = cities?.get(position)
        city?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = cities?.size ?: 0

    class FavoriteCityViewHolder(private val binding: ItemFavouriteCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(city: WeatherData) {
            binding.weatherData = city
            val degreeSymbol = itemView.context.getString(R.string.degree_symbol_celcious)
            binding.cityTemperature.text = buildString {
                append(city.main.temp)
                append(degreeSymbol)
            }
        }
    }

    fun addData(cities: List<WeatherData>) {
        this.cities = cities
        notifyDataSetChanged()
    }
}
