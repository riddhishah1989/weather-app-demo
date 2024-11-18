package com.weatherappdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weatherappdemo.R
import com.weatherappdemo.data.model.WeatherDataModel
import com.weatherappdemo.databinding.ItemSearchedCityBinding
import com.weatherappdemo.utils.CustomInterfaces

class SearchCitiesAdapter(private val onItemClick: CustomInterfaces.OnSearchedCityItemClick) :
    RecyclerView.Adapter<SearchCitiesAdapter.SearchCityViewHolder>() {

    private var searchCitiesData: List<WeatherDataModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchedCityBinding.inflate(inflater, parent, false)
        return SearchCityViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: SearchCityViewHolder, position: Int) {
        val city = searchCitiesData?.get(position)
        city?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = searchCitiesData?.size ?: 0

    class SearchCityViewHolder(
        private val binding: ItemSearchedCityBinding,
        private val onItemClick: CustomInterfaces.OnSearchedCityItemClick
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: WeatherDataModel) {
            binding.weatherData = data
            val degreeSymbol = itemView.context.getString(R.string.degree_symbol_celcious)
            binding.cityTemperature.text = buildString {
                append(data.temperature)
                append(degreeSymbol)
            }
            binding.root.setOnClickListener {
                onItemClick.onItemClick(data)

            }
        }
    }

    fun addData(cities: List<WeatherDataModel>) {
        this.searchCitiesData = cities
        notifyDataSetChanged()
    }
}