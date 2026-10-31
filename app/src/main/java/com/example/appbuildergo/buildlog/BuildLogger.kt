package com.example.appbuildergo.buildlog

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogLevel { INFO, OK, WARN, ERROR, DEBUG, DONE }

data class LogEntry(
    val level: LogLevel,
    val message: String,
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
) {
    fun render(): String = "[$timestamp] [${level.name.padEnd(5)}] $message"
}

/**
 * Single source of truth log pembungkusan.
 * UI meng-collect [entries]; engine build asli (AI logic) cukup memanggil i/ok/w/e/d/done.
 * Setiap entri juga di-mirror ke Logcat (tag: AppBuilderGo/Build).
 */
object BuildLogger {
    private const val TAG = "AppBuilderGo/Build"

    private val _entries = MutableStateFlow<List<LogEntry>>(emptyList())
    val entries: StateFlow<List<LogEntry>> = _entries.asStateFlow()

    fun reset() {
        _entries.value = emptyList()
    }

    private fun push(level: LogLevel, message: String) {
        _entries.value = _entries.value + LogEntry(level, message)
        when (level) {
            LogLevel.ERROR -> Log.e(TAG, message)
            LogLevel.WARN -> Log.w(TAG, message)
            LogLevel.DEBUG -> Log.d(TAG, message)
            else -> Log.i(TAG, message)
        }
    }

    fun i(message: String) = push(LogLevel.INFO, message)
    fun ok(message: String) = push(LogLevel.OK, message)
    fun w(message: String) = push(LogLevel.WARN, message)
    fun e(message: String) = push(LogLevel.ERROR, message)
    fun d(message: String) = push(LogLevel.DEBUG, message)
    fun done(message: String) = push(LogLevel.DONE, message)
}
