package com.example.paymentnotificationinspector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val title = TextView(this).apply {
            text = "Payment Notification Inspector"
            textSize = 22f
        }
        root.addView(title)

        status = TextView(this).apply {
            textSize = 15f
            setPadding(0, 16, 0, 16)
        }
        root.addView(status)

        val settingsButton = Button(this).apply {
            text = "Enable notification access"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }
        root.addView(settingsButton)

        val refreshButton = Button(this).apply {
            text = "Refresh captured notifications"
            setOnClickListener { refresh() }
        }
        root.addView(refreshButton)

        val clearButton = Button(this).apply {
            text = "Clear captured data"
            setOnClickListener {
                NotificationStore.clear(this@MainActivity)
                refresh()
            }
        }
        root.addView(clearButton)

        val scroll = ScrollView(this)
        output = TextView(this).apply {
            textSize = 13f
            typeface = android.graphics.Typeface.MONOSPACE
            setTextIsSelectable(true)
            setPadding(0, 16, 0, 16)
        }
        scroll.addView(output)
        root.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        setContentView(root)
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val enabled = NotificationStore.hasNotificationAccess(this)
        status.text = if (enabled) {
            "Notification access: enabled\nListening for PhonePe, Google Pay and Paytm notifications."
        } else {
            "Notification access: not enabled. Tap the button above and enable this app."
        }
        output.text = NotificationStore.readAll(this).ifBlank {
            "No captured notifications yet. Send/receive a test notification, then tap Refresh."
        }
    }
}
