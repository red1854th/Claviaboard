package com.example.claviatura

import android.content.Context
import android.content.SharedPreferences

class KeyboardPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("claviatura_prefs", Context.MODE_PRIVATE)

    var firstRun: Boolean
        get() = prefs.getBoolean("first_run", true)
        set(value) = prefs.edit().putBoolean("first_run", value).apply()

    // Dimensions & Geometry
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

    // Key Toggles (키 옵션)
    var showNumberRow: Boolean
        get() = prefs.getBoolean("show_number_row", true)
        set(value) = prefs.edit().putBoolean("show_number_row", value).apply()

    var showSpecialKey: Boolean
        get() = prefs.getBoolean("show_special_key", true)
        set(value) = prefs.edit().putBoolean("show_special_key", value).apply()

    var showPeriodKey: Boolean
        get() = prefs.getBoolean("show_period_key", true)
        set(value) = prefs.edit().putBoolean("show_period_key", value).apply()

    var showVoiceKey: Boolean
        get() = prefs.getBoolean("show_voice_key", true)
        set(value) = prefs.edit().putBoolean("show_voice_key", true).apply()

    var showEmojiKey: Boolean
        get() = prefs.getBoolean("show_emoji_key", true)
        set(value) = prefs.edit().putBoolean("show_emoji_key", value).apply()

    // Sound & Haptics
    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", false)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var soundProfile: String
        get() = prefs.getString("sound_profile", "mech_click") ?: "mech_click"
        set(value) = prefs.edit().putString("sound_profile", value).apply()

    var vibrateEnabled: Boolean
        get() = prefs.getBoolean("vibrate_enabled", true)
        set(value) = prefs.edit().putBoolean("vibrate_enabled", value).apply()

    var vibrateStrength: Int
        get() = prefs.getInt("vibrate_strength", 30)
        set(value) = prefs.edit().putInt("vibrate_strength", value).apply()

    // Typing Behaviors & Modes
    var useSystemFont: Boolean
        get() = prefs.getBoolean("use_system_font", false)
        set(value) = prefs.edit().putBoolean("use_system_font", value).apply()

    var showKeyPreview: Boolean
        get() = prefs.getBoolean("show_key_preview", true)
        set(value) = prefs.edit().putBoolean("show_key_preview", value).apply()

    var numberRowLandscape: Boolean
        get() = prefs.getBoolean("number_row_landscape", true)
        set(value) = prefs.edit().putBoolean("number_row_landscape", value).apply()

    var doubleSpacePeriod: Boolean
        get() = prefs.getBoolean("double_space_period", true)
        set(value) = prefs.edit().putBoolean("double_space_period", value).apply()

    var autoCapitalize: Boolean
        get() = prefs.getBoolean("auto_capitalize", false)
        set(value) = prefs.edit().putBoolean("auto_capitalize", value).apply()

    var doubleConsonantMode: Boolean
        get() = prefs.getBoolean("double_consonant_mode", true)
        set(value) = prefs.edit().putBoolean("double_consonant_mode", value).apply()

    var cursorSensitivity: Int
        get() = prefs.getInt("cursor_sensitivity", 6)
        set(value) = prefs.edit().putInt("cursor_sensitivity", value).apply()

    // Timings
    var longPressTimeout: Long
        get() = prefs.getLong("long_press_timeout", 350L)
        set(value) = prefs.edit().putLong("long_press_timeout", value).apply()

    var deleteHoldDelay: Long
        get() = prefs.getLong("delete_hold_delay", 350L)
        set(value) = prefs.edit().putLong("delete_hold_delay", value).apply()

    var deleteRepeatInterval: Long
        get() = prefs.getLong("delete_repeat_interval", 45L)
        set(value) = prefs.edit().putLong("delete_repeat_interval", value).apply()

    // Number / Symbol Keypad Mode
    var numberKeypadType: String
        get() = prefs.getString("number_keypad_type", "linked") ?: "linked"
        set(value) = prefs.edit().putString("number_keypad_type", value).apply()

    // Themes & Dark Mode
    var followSystemDarkMode: Boolean
        get() = prefs.getBoolean("follow_system_dark_mode", false)
        set(value) = prefs.edit().putBoolean("follow_system_dark_mode", value).apply()

    var selectedThemeId: String
        get() = prefs.getString("selected_theme_id", "dark") ?: "dark"
        set(value) = prefs.edit().putString("selected_theme_id", value).apply()

    var selectedLanguage: String
        get() = prefs.getString("selected_language", "ko_KR") ?: "ko_KR"
        set(value) = prefs.edit().putString("selected_language", value).apply()

    var koreanLayoutType: String
        get() = prefs.getString("korean_layout_type", "두벌식") ?: "두벌식"
        set(value) = prefs.edit().putString("korean_layout_type", value).apply()

    var englishLayoutType: String
        get() = prefs.getString("english_layout_type", "쿼티") ?: "쿼티"
        set(value) = prefs.edit().putString("english_layout_type", value).apply()

    val currentTheme: KeyboardTheme
        get() = KeyboardTheme.getThemeById(selectedThemeId)
}
