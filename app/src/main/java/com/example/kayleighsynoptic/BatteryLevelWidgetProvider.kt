package com.example.kayleighsynoptic

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.RemoteViews

class BatteryLevelWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        // Schedule the next update.
        BatteryLevelWidgetScheduler.scheduleUpdate(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null && intent != null) {
            when (intent.action) {
                Intent.ACTION_BATTERY_LOW, Intent.ACTION_POWER_CONNECTED, Intent.ACTION_POWER_DISCONNECTED -> {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, BatteryLevelWidgetProvider::class.java))
                    appWidgetIds.forEach { appWidgetId ->
                        updateAppWidget(context, appWidgetManager, appWidgetId)
                    }
                }
                Intent.ACTION_POWER_CONNECTED -> {
                    // Store the connection time when the device is connected to power.
                    storeChargingTime(context)
                }
            }
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = level.toFloat() / scale.toFloat()

            val views = RemoteViews(context.packageName, R.layout.widget_battery_level).apply {
                val imageRes = when {
                    batteryPct > 0.75 -> R.drawable.battery_green
                    batteryPct > 0.45 -> R.drawable.battery_orange
                    else -> R.drawable.battery_red
                }
                setImageViewResource(R.id.battery_image, imageRes)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun storeChargingTime(context: Context) {
            val prefs = context.getSharedPreferences("BatteryWidgetPrefs", Context.MODE_PRIVATE)
            prefs.edit().putLong("LastChargingTime", System.currentTimeMillis()).apply()
        }

        fun loadLastChargingTime(context: Context): Long {
            val prefs = context.getSharedPreferences("BatteryWidgetPrefs", Context.MODE_PRIVATE)
            return prefs.getLong("LastChargingTime", 0L)
        }
    }
}
