package com.ascii.warmpackage

import android.annotation.TargetApi
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build

class WarmApp : Application() {

    companion object {
        const val DEFAULT_CHANNEL_ID: String = "WarmPackage"
    }

    override fun onCreate() {
        super.onCreate()
        startWarmService()
    }

    private fun startWarmService() {
        val startServiceIntent = Intent(this, WarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startServiceIntent)
        }
        createNotificationChannel()
        startService(startServiceIntent)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        var notificationChannel = NotificationChannel(DEFAULT_CHANNEL_ID, "WarmPackage", NotificationManager.IMPORTANCE_LOW)
        notificationChannel.setDescription("WarmPackage")
        notificationChannel.setShowBadge(true)
        notificationChannel.enableVibration(false)
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}