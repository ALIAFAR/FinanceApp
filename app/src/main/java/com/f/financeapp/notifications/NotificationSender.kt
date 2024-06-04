package com.f.financeapp.notifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat

import com.f.financeapp.R

class NotificationSender {
    companion object {
        fun sendLimitNotification(application: Application, limit: String, difference: Double) {
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(application)
                .setSmallIcon(R.drawable.ic_expenses)
                .setContentTitle("Лимит превышен")
                .setContentText("Лимит расходов (" + limit + "руб.) превышен на " + difference.toString() +  " руб. !")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val manager = application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val notificationManager = application.getSystemService(
                NOTIFICATION_SERVICE
            ) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "channel_id"
                val channel = NotificationChannel(
                    channelId,
                    "notifications_channel",
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.enableVibration(true)
                notificationManager.createNotificationChannel(channel)
                builder.setChannelId(channelId)
            }
            manager.notify(0, builder.build())
        }
    }
}