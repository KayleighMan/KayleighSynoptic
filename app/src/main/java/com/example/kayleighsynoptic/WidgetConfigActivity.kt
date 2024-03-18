package com.example.kayleighsynoptic

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WidgetConfigActivity : AppCompatActivity() {

    private lateinit var selectedCity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)

        val cities = listOf("Valletta", "Paris", "Rome")
        val citiesRecyclerView: RecyclerView = findViewById(R.id.cities_recycler_view)
        citiesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize selectedCity with a default or previously saved value
        selectedCity = cities[0]

        citiesRecyclerView.adapter = CitiesAdapter(cities, selectedCity) { city ->
            selectedCity = city
        }

        val confirmButton: Button = findViewById(R.id.confirm_button)
        confirmButton.setOnClickListener {
            // Here, selectedCity holds the selected city
            val appWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
            saveCityPreference(this, appWidgetId, selectedCity)

            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    companion object {
        fun saveCityPreference(context: Context, appWidgetId: Int, city: String) {
            val prefs = context.getSharedPreferences("weatherWidget", Context.MODE_PRIVATE)
            with(prefs.edit()) {
                putString("city_$appWidgetId", city)
                apply()
            }
        }
    }
}
