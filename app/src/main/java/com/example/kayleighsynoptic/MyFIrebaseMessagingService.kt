package com.example.kayleighsynoptic

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "weather_alerts_channel"
const val channelName = "Weather Alerts"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "Refreshed token: $token")
        // Consider sending the FCM token to your server here
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.isNotEmpty().let {
            val title = remoteMessage.data["title"] ?: "Extreme Weather Alert"
            val message = remoteMessage.data["message"] ?: "Please check the app for more details."
            generateNotification(title, message)
            // If you have a specific action or data to handle, add it here
            updateWeatherWidget(title, message)
        }
    }

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews(packageName, R.layout.weather_widget)
        // Customize your widget layout with the alert
        remoteView.setTextViewText(R.id.weather_status, title)
        remoteView.setTextViewText(R.id.weather_temperature, message)
        remoteView.setInt(R.id.widget_root_layout, "setBackgroundColor", Color.RED) // Assuming you have a background ID to set
        return remoteView
    }

    fun generateNotification(title: String, message: String) {
        val intent = Intent(this, WidgetConfigActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        var builder = NotificationCompat.Builder(applicationContext, channelId).apply {
            setSmallIcon(R.drawable.notification)
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            setContent(getRemoteView(title, message))
            priority = NotificationCompat.PRIORITY_HIGH
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for Extreme Weather Alerts"
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }

    private fun updateWeatherWidget(title: String, message: String) {
        // Assuming WeatherWidgetProvider is the class handling your widget
        val intent = Intent(this, WeatherWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra("widget_extra_title", title)
            putExtra("widget_extra_message", message)
        }
        sendBroadcast(intent)
    }
}
