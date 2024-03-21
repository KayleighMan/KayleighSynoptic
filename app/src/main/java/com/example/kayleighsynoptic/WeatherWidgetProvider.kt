package com.example.kayleighsynoptic


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import com.example.kayleighsynoptic.R
import com.example.kayleighsynoptic.RetrofitInstance
import com.example.kayleighsynoptic.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class WeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateWeatherData(context, appWidgetManager, appWidgetId)
        }
    }


    private fun getCityPreference(context: Context, appWidgetId: Int): String {
        val prefs = context.getSharedPreferences("weatherWidget", Context.MODE_PRIVATE)
        return prefs.getString("city_$appWidgetId", "Valletta") ?: "Valletta" // Default to Valletta if not found
    }

    private fun updateWeatherData(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val city = getCityPreference(context, appWidgetId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getWeather(city)
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    val views = RemoteViews(context.packageName, R.layout.weather_widget).apply {
                        setTextViewText(R.id.weather_location, weatherData?.name)
                        setTextViewText(R.id.weather_temperature, "${weatherData?.temp}°C")
                        setTextViewText(R.id.weather_status, weatherData?.condition)
                    }
                    withContext(Dispatchers.Main) {
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } else {
                    Log.e("WidgetUpdate", "API call unsuccessful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("WidgetUpdate", "API call failed: ${e.message}")
            }
        }
    }



    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        weather: WeatherResponse
    ) {
        val views = RemoteViews(context.packageName, R.layout.weather_widget).apply {
            setTextViewText(R.id.weather_location, weather.name)
            setTextViewText(R.id.weather_temperature, "${weather.temp}°C")
            setTextViewText(R.id.weather_status, weather.condition)
        }

        views.setInt(R.id.widget_root_layout, "setBackgroundResource", R.drawable.widget_colour)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
