package com.example.claviatura

import android.content.Context
import android.content.SharedPreferences

class KeyboardPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("claviatura_prefs", Context.MODE_PRIVATE)

    var firstRun: Boolean
        get() = prefs.getBoolean("first_run", true)
        set(value) = prefs.edit().putBoolean("first_run", value).apply()

    var keyboardHeight: Int
        get() = prefs.getInt("keyboard_height", 240)
        set(value) = prefs.edit().putInt("keyboard_height", value).apply()

    var horizontalMargin: Int
        get() = prefs.getInt("horizontal_margin", 0)
        set(value) = prefs.edit().putInt("horizontal_margin", value).apply()

    var bottomMargin: Int
        get() = prefs.getInt("bottom_margin", 0)
        set(value) = prefs.edit().putInt("bottom_margin", value).apply()

    var keyCornerRadius: Int
        get() = prefs.getInt("key_corner_radius", 6)
        set(value) = prefs.edit().putInt("key_corner_radius", value).apply()

    var deleteHoldDelay: Long
        get() = prefs.getLong("delete_hold_delay", 400L)
        set(value) = prefs.edit().putLong("delete_hold_delay", value).apply()

    var deleteRepeatInterval: Long
        get() = prefs.getLong("delete_repeat_interval", 45L)
        set(value) = prefs.edit().putLong("delete_repeat_interval", value).apply()

    var longPressTimeout: Long
        get() = prefs.getLong("long_press_timeout", 350L)
        set(value) = prefs.edit().putLong("long_press_timeout", value).apply()

    var selectedLanguage: String
        get() = prefs.getString("selected_language", "ko_KR") ?: "ko_KR"
        set(value) = prefs.edit().putString("selected_language", value).apply()

    var vibrateEnabled: Boolean
        get() = prefs.getBoolean("vibrate_enabled", true)
        set(value) = prefs.edit().putBoolean("vibrate_enabled", value).apply()

    var vibrateStrength: Int
        get() = prefs.getInt("vibrate_strength", 30)
        set(value) = prefs.edit().putInt("vibrate_strength", value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", false)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var showNumberRow: Boolean
        get() = prefs.getBoolean("show_number_row", true)
        set(value) = prefs.edit().putBoolean("show_number_row", value).apply()

    var showPeriodKey: Boolean
        get() = prefs.getBoolean("show_period_key", true)
        set(value) = prefs.edit().putBoolean("show_period_key", value).apply()

    var showKeyPreview: Boolean
        get() = prefs.getBoolean("show_key_preview", true)
        set(value) = prefs.edit().putBoolean("show_key_preview", value).apply()

    var selectedThemeId: String
        get() = prefs.getString("selected_theme_id", "light") ?: "light"
        set(value) = prefs.edit().putString("selected_theme_id", value).apply()

    val currentTheme: KeyboardTheme
        get() = KeyboardTheme.getThemeById(selectedThemeId)
}
