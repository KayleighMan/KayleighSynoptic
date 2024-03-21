package com.example.kayleighsynoptic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.isNotEmpty().let {
            val title = remoteMessage.data["title"] ?: "Alert"
            val message = remoteMessage.data["message"] ?: "Check your app for details."
            generateNotification(title, message)

            // Assuming the message requires showing a warning on the widget
            if (remoteMessage.data.containsKey("showWarning")) {
                showWarningOnWidget(this, "WARNING: Extreme weather conditions!")
            }
        }
    }

    private fun generateNotification(title: String, message: String) {
        val intent = Intent(this, WeatherWidgetProvider::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Weather Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel description"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "weather_alerts_channel"

        fun showWarningOnWidget(context: Context, warningText: String) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, WeatherWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

            allWidgetIds.forEach { appWidgetId ->
                val views = RemoteViews(context.packageName, R.layout.weather_widget).apply {
                    setViewVisibility(R.id.widget_warning_text, View.VISIBLE)
                    setTextViewText(R.id.widget_warning_text, warningText)
                    setTextColor(R.id.widget_warning_text, Color.RED)
                }
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
