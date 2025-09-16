package net.mnio.alpenstrandlaufer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mnio.alpenstrandlaufer.data.AirplaneSession
import net.mnio.alpenstrandlaufer.data.AppDatabase

private const val PREF_START_TIME = "airplane_start"

const val ACTION_RELOAD_SESSION_INTENT_NAME = "ACTION_RELOAD_SESSIONS"

class AirplaneModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            return
        }

        val isOn = intent.getBooleanExtra("state", false)
        val prefs = context.getSharedPreferences("airplane_prefs", Context.MODE_PRIVATE)

        if (isOn) {
            prefs.edit().putLong(PREF_START_TIME, System.currentTimeMillis()).apply()
            Toast.makeText(context, "Airplane mode enabled", Toast.LENGTH_SHORT).show()
        } else {
            val start = prefs.getLong(PREF_START_TIME, -1L)
            if (start <= 0) {
                return
            }

            val session = AirplaneSession(
                startTime = start,
                endTime = System.currentTimeMillis()
            )

            GlobalScope.launch {
                AppDatabase.getDatabase(context).sessionDao().insert(session)
            }

            val updateIntent = Intent(ACTION_RELOAD_SESSION_INTENT_NAME)
            LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent)

            val durationString = session.getDurationFormatted()
            Toast.makeText(
                context,
                "Disabled. ${durationString}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
