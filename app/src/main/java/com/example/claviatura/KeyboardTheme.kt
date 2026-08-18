package com.example.claviatura

import android.graphics.Color

data class KeyboardTheme(
    val id: String,
    val name: String,
    val keyboardBackground: Int,
    val keyBackground: Int,
    val keyPressedBackground: Int,
    val keyStrokeColor: Int,
    val keyTextColor: Int,
    val keySubTextColor: Int,
    val controlKeyBackground: Int,
    val controlKeyPressedBackground: Int,
    val controlKeyStrokeColor: Int,
    val controlKeyTextColor: Int,
    val numberKeyBackground: Int,
    val numberKeyPressedBackground: Int,
    val numberKeyTextColor: Int,
    val previewBackground: Int,
    val previewTextColor: Int,
    val previewStrokeColor: Int,
    val previewHoldBackground: Int,
    val previewHoldTextColor: Int,
    val accentColor: Int
) {
    companion object {
        val LIGHT = KeyboardTheme(
            id = "light",
            name = "라이트 (기본)",
            keyboardBackground = Color.parseColor("#ECEFF1"),
            keyBackground = Color.WHITE,
            keyPressedBackground = Color.parseColor("#CBD5E1"),
            keyStrokeColor = Color.parseColor("#D5D9DF"),
            keyTextColor = Color.parseColor("#111827"),
            keySubTextColor = Color.parseColor("#9CA3AF"),
            controlKeyBackground = Color.parseColor("#D6DBE1"),
            controlKeyPressedBackground = Color.parseColor("#CBD5E1"),
            controlKeyStrokeColor = Color.TRANSPARENT,
            controlKeyTextColor = Color.parseColor("#374151"),
            numberKeyBackground = Color.parseColor("#DFE3E7"),
            numberKeyPressedBackground = Color.parseColor("#CBD5E1"),
            numberKeyTextColor = Color.parseColor("#374151"),
            previewBackground = Color.WHITE,
            previewTextColor = Color.parseColor("#111827"),
            previewStrokeColor = Color.parseColor("#D5D8DE"),
            previewHoldBackground = Color.parseColor("#4C84F3"),
            previewHoldTextColor = Color.WHITE,
            accentColor = Color.parseColor("#4C84F3")
        )

        val DARK = KeyboardTheme(
            id = "dark",
            name = "다크",
            keyboardBackground = Color.parseColor("#1E222B"),
            keyBackground = Color.parseColor("#2C3240"),
            keyPressedBackground = Color.parseColor("#3E4658"),
            keyStrokeColor = Color.parseColor("#384052"),
            keyTextColor = Color.parseColor("#F9FAFB"),
            keySubTextColor = Color.parseColor("#9CA3AF"),
            controlKeyBackground = Color.parseColor("#232834"),
            controlKeyPressedBackground = Color.parseColor("#353C4D"),
            controlKeyStrokeColor = Color.TRANSPARENT,
            controlKeyTextColor = Color.parseColor("#D1D5DB"),
            numberKeyBackground = Color.parseColor("#262C39"),
            numberKeyPressedBackground = Color.parseColor("#353C4D"),
            numberKeyTextColor = Color.parseColor("#D1D5DB"),
            previewBackground = Color.parseColor("#2C3240"),
            previewTextColor = Color.WHITE,
            previewStrokeColor = Color.parseColor("#4B5563"),
            previewHoldBackground = Color.parseColor("#3B82F6"),
            previewHoldTextColor = Color.WHITE,
            accentColor = Color.parseColor("#3B82F6")
        )

        val PASTEL_BLUE = KeyboardTheme(
            id = "pastel_blue",
            name = "파스텔 블루",
            keyboardBackground = Color.parseColor("#E0E7FF"),
            keyBackground = Color.WHITE,
            keyPressedBackground = Color.parseColor("#C7D2FE"),
            keyStrokeColor = Color.parseColor("#C7D2FE"),
            keyTextColor = Color.parseColor("#1E1B4B"),
            keySubTextColor = Color.parseColor("#6366F1"),
            controlKeyBackground = Color.parseColor("#C7D2FE"),
            controlKeyPressedBackground = Color.parseColor("#A5B4FC"),
            controlKeyStrokeColor = Color.TRANSPARENT,
            controlKeyTextColor = Color.parseColor("#312E81"),
            numberKeyBackground = Color.parseColor("#D5DEFF"),
            numberKeyPressedBackground = Color.parseColor("#C7D2FE"),
            numberKeyTextColor = Color.parseColor("#312E81"),
            previewBackground = Color.WHITE,
            previewTextColor = Color.parseColor("#1E1B4B"),
            previewStrokeColor = Color.parseColor("#818CF8"),
            previewHoldBackground = Color.parseColor("#4F46E5"),
            previewHoldTextColor = Color.WHITE,
            accentColor = Color.parseColor("#4F46E5")
        )

        val ALL_THEMES = listOf(LIGHT, DARK, PASTEL_BLUE)

        fun getThemeById(id: String): KeyboardTheme {
            return ALL_THEMES.find { it.id == id } ?: LIGHT
        }
    }
}
