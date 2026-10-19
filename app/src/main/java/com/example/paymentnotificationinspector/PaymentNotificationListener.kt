package com.example.paymentnotificationinspector

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentNotificationListener : NotificationListenerService() {

    // Package names can vary by app/version/region. Confirm these on the test device.
    private val targetPackages = setOf(
        "com.phonepe.app",
        "com.google.android.apps.nbu.paisa.user",
        "net.one97.paytm",
        "com.paytm"
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName !in targetPackages) return

        val n = sbn.notification
        val extras = n.extras
        val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val bigText = extras?.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val subText = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        val infoText = extras?.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString()
        val summaryText = extras?.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString()
        val textLines = extras?.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        val lines = JSONArray()
        textLines?.forEach { lines.put(it?.toString()) }

        val rawExtras = JSONObject()
        extras?.keySet()?.sorted()?.forEach { key ->
            val value = extras.get(key)
            rawExtras.put(key, safeValue(value))
        }

        val timestamp = sbn.postTime
        val formatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US)
            .format(Date(timestamp))

        val event = JSONObject().apply {
            put("packageName", sbn.packageName)
            put("notificationId", sbn.id)
            put("notificationKey", sbn.key)
            put("postTimeMillis", timestamp)
            put("postTimeFormatted", formatted)
            put("title", title ?: JSONObject.NULL)
            put("text", text ?: JSONObject.NULL)
            put("bigText", bigText ?: JSONObject.NULL)
            put("subText", subText ?: JSONObject.NULL)
            put("infoText", infoText ?: JSONObject.NULL)
            put("summaryText", summaryText ?: JSONObject.NULL)
            put("textLines", lines)
            put("category", n.category ?: JSONObject.NULL)
            put("channelId", if (android.os.Build.VERSION.SDK_INT >= 26) n.channelId else JSONObject.NULL)
            put("groupKey", sbn.groupKey ?: JSONObject.NULL)
            put("isOngoing", n.flags and Notification.FLAG_ONGOING_EVENT != 0)
            put("rawExtras", rawExtras)
        }

        NotificationStore.add(applicationContext, event.toString(2))
    }

    private fun safeValue(value: Any?): Any {
        if (value == null) return JSONObject.NULL
        return when (value) {
            is CharSequence -> value.toString()
            is String, is Number, is Boolean -> value
            is IntArray -> JSONArray(value.toList())
            is LongArray -> JSONArray(value.toList())
            is Array<*> -> JSONArray().apply {
                value.forEach { put(safeValue(it)) }
            }
            is android.os.Bundle -> {
                val obj = JSONObject()
                value.keySet().sorted().forEach { key ->
                    try { obj.put(key, safeValue(value.get(key))) }
                    catch (_: Exception) { obj.put(key, "<unavailable>") }
                }
                obj
            }
            else -> value.toString()
        }
    }
}
