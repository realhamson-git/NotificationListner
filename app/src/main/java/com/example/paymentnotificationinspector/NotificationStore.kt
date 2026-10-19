package com.example.paymentnotificationinspector

import android.content.Context
import android.provider.Settings
import org.json.JSONArray

object NotificationStore {
    private const val PREFS = "notification_store"
    private const val KEY_EVENTS = "events"
    private const val MAX_EVENTS = 200

    fun hasNotificationAccess(context: Context): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return enabled.split(":").any {
            it.contains(context.packageName, ignoreCase = true)
        }
    }

    @Synchronized
    fun add(context: Context, eventJson: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = try {
            JSONArray(prefs.getString(KEY_EVENTS, "[]"))
        } catch (_: Exception) {
            JSONArray()
        }
        val next = JSONArray()
        val start = (existing.length() - MAX_EVENTS + 1).coerceAtLeast(0)
        for (i in start until existing.length()) next.put(existing.get(i))
        next.put(eventJson)
        prefs.edit().putString(KEY_EVENTS, next.toString()).apply()
    }

    fun readAll(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val array = try {
            JSONArray(prefs.getString(KEY_EVENTS, "[]"))
        } catch (_: Exception) {
            JSONArray()
        }
        return (0 until array.length()).mapNotNull { i ->
            try { array.getString(i) } catch (_: Exception) { null }
        }.asReversed().joinToString("\n\n────────────────────────\n\n")
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().remove(KEY_EVENTS).apply()
    }
}
