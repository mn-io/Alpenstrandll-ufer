package net.mnio.alpenstrandlaufer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_CHANNEL = "airplane_channel"

class AirplaneModeService : Service() {

    private val receiver = AirplaneModeReceiver()

    override fun onCreate() {
        super.onCreate()

        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(receiver, filter)

        // Create notification channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                getString(R.string.app_name_long),
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentTitle(getString(R.string.app_name_long))
            .setContentText(getString(R.string.app_description))
            .setSmallIcon(R.drawable.ic_airplane_foreground) // your monochrome icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
