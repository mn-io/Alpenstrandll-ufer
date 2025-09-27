package net.mnio.alpenstrandlaufer.utils

import android.util.Log
import net.mnio.alpenstrandlaufer.DEBUG_MODE

fun Any.logd(message: String) {
    if (DEBUG_MODE) {
        Log.d(this::class.java.simpleName, message)
    }
}
