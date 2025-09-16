package net.mnio.alpenstrandlaufer.data

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity(tableName = "airplane_sessions")
data class AirplaneSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val durationHours: Int, // 0-...
    val durationMinutes: Int, // 0-60
    val durationSeconds: Int // 0-60
) {

    // Secondary constructor (ignored by Room)
    @Ignore
    constructor(startTime: Long, endTime: Long) : this(
        id = 0,
        startTime = startTime,
        endTime = endTime,
        durationHours = calcHours(startTime, endTime),
        durationMinutes = calcMinutes(startTime, endTime),
        durationSeconds = calcSeconds(startTime, endTime)
    )

    @SuppressLint("DefaultLocale")
    fun getDurationFormatted(): String {
        return when {
            durationHours > 0 -> String.format(
                "%02d:%02d:%02d",
                durationHours,
                durationMinutes,
                durationSeconds
            )

            else -> String.format("%02d:%02d", durationMinutes, durationSeconds)
        }
    }

    companion object {
        private fun calcHours(start: Long, end: Long): Int =
            TimeUnit.MILLISECONDS.toHours(end - start).toInt()

        private fun calcMinutes(start: Long, end: Long): Int =
            (TimeUnit.MILLISECONDS.toMinutes(end - start) % 60).toInt()

        private fun calcSeconds(start: Long, end: Long): Int =
            (TimeUnit.MILLISECONDS.toSeconds(end - start) % 60).toInt()
    }
}
