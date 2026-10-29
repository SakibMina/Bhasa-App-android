package com.bhasha.dictionary

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class FloatingDictionaryService : Service() {
    private lateinit var windowManager: WindowManager
    private var bubble: android.view.View? = null
    private var panel: android.view.View? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showBubble()
    }

    private fun overlayParams(width: Int, height: Int) = WindowManager.LayoutParams(
        width, height,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply { gravity = Gravity.TOP or Gravity.END; x = 20; y = 260 }

    private fun showBubble() {
        val button = Button(this).apply {
            text = "ব"
            textSize = 20f
            setOnClickListener { lookupClipboard() }
        }
        bubble = button
        windowManager.addView(button, overlayParams(72, 72))
    }

    private fun lookupClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = if (clipboard.hasPrimaryClip()) clipboard.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString().orEmpty() else ""
        val cleaned = text.trim().take(2500)
        if (cleaned.isBlank()) {
            Toast.makeText(this, "আগে অন্য অ্যাপ থেকে একটি শব্দ বা বাক্য Copy করুন", Toast.LENGTH_LONG).show()
            return
        }
        val entry = Dictionary.lookup(cleaned, "English → বাংলা")
        showPanel(cleaned, entry?.meaning ?: "এই শব্দটি sample offline dictionary-তে নেই।")
    }

    private fun showPanel(original: String, meaning: String) {
        panel?.let { runCatching { windowManager.removeView(it) } }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 20, 28, 18)
            setBackgroundColor(0xFFFAFFFC.toInt())
        }
        layout.addView(TextView(this).apply { text = original; textSize = 20f; setTextColor(0xFF146C5A.toInt()) })
        layout.addView(TextView(this).apply { text = meaning; textSize = 18f; setTextColor(0xFF202823.toInt()) })
        layout.addView(Button(this).apply { text = "Close"; setOnClickListener { panel?.let { v -> windowManager.removeView(v) }; panel = null } })
        panel = layout
        windowManager.addView(layout, overlayParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT).apply {
            width = (resources.displayMetrics.widthPixels * 0.88).toInt()
            gravity = Gravity.CENTER
            x = 0; y = 0
        })
    }

    override fun onDestroy() {
        bubble?.let { runCatching { windowManager.removeView(it) } }
        panel?.let { runCatching { windowManager.removeView(it) } }
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
