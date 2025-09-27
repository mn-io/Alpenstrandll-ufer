package net.mnio.alpenstrandlaufer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mnio.alpenstrandlaufer.data.AirplaneSession
import net.mnio.alpenstrandlaufer.data.AppDatabase

private const val PREF_START_TIME = "airplane_start"

class AirplaneModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            Log.d("App", "non airplane intent received: ${intent.action}")
            return
        }

        val isOn = intent.getBooleanExtra("state", false)
        val prefs = context.getSharedPreferences("airplane_prefs", Context.MODE_PRIVATE)

        val now = System.currentTimeMillis()
        if (isOn) {
            prefs.edit { putLong(PREF_START_TIME, now) }
            Log.d("App", "intent received with airplane mode ON, saving now as: ${now}")
            Toast.makeText(
                context,
                context.getString(R.string.airplane_mode_enabled), Toast.LENGTH_SHORT
            ).show()
        } else {
            val start = prefs.getLong(PREF_START_TIME, -1L)
            if (start <= 0) {
                Log.d(
                    "App",
                    "intent received with airplane mode OFF, but no start time known, abort here"
                )
                return
            }

            Log.d("App", "intent received with airplane mode OFF, saving new session.")

            val session = AirplaneSession(
                startTime = start,
                endTime = now
            )

            GlobalScope.launch {
                val sessionDao = AppDatabase.getDatabase(context).sessionDao()
                sessionDao.insert(session)
                Log.d(
                    "App",
                    "New session inserted, known sessions count: ${sessionDao.getAll().size}"
                )
            }

            val durationString = session.getDurationFormatted()
            Toast.makeText(
                context,
                "Disabled. ${durationString}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
