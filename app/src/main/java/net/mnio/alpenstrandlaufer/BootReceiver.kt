package net.mnio.alpenstrandlaufer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("App", "device boot completedd, registering service")
            val serviceIntent = Intent(context, AirplaneModeService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
