package com.example.kayleighsynoptic

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton

class CitiesAdapter(
    private val cities: List<String>,
    private var selectedCity: String?,
    private val onCitySelected: (String) -> Unit
) : RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    class ViewHolder(val radioButton: MaterialRadioButton) : RecyclerView.ViewHolder(radioButton)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val radioButton = LayoutInflater.from(parent.context)
            .inflate(R.layout.city_item, parent, false) as MaterialRadioButton
        return ViewHolder(radioButton)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = cities[position]
        holder.radioButton.text = city
        holder.radioButton.isChecked = city == selectedCity

        holder.radioButton.setOnClickListener {
            selectedCity = city
            notifyDataSetChanged()
            onCitySelected(city)
        }
    }

    override fun getItemCount() = cities.size
}
