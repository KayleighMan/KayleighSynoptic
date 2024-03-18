package com.example.kayleighsynoptic

import android.util.Log
import kotlinx.coroutines.*

class WeatherRepository(private val apiService: WeatherApiService) {

    fun getWeather(city: String, callback: (WeatherResponse?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getWeather(city)
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    Log.d("WeatherWidget", "API Data Retrieved: $weatherData")
                } else {
                    Log.e("WeatherWidget", "API Call Failed: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("WidgetUpdate", "Exception: ${e.message}")
            }
        }
    }
}
