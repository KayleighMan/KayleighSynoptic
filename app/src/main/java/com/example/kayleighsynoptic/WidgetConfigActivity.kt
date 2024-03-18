package com.example.kayleighsynoptic

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class WidgetConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)


        val button = findViewById<Button>(R.id.confirm_button)
        val radioGroup = findViewById<RadioGroup>(R.id.city_radio_group)

        button.setOnClickListener {
            val selectedCityId = radioGroup.checkedRadioButtonId
            val city = findViewById<RadioButton>(selectedCityId).text.toString()

            // Save the selected city in SharedPreferences
            val appWidgetId = intent.extras?.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

            saveCityPreference(this, appWidgetId, city)

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