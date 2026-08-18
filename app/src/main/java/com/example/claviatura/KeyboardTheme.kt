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
        val DARK = KeyboardTheme(
            id = "dark",
            name = "Catppuccin Mocha",
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
            previewHoldBackground = Color.parseColor("#38BDF8"),
            previewHoldTextColor = Color.parseColor("#0F172A"),
            accentColor = Color.parseColor("#38BDF8")
        )

        val PASTEL_BLUE = KeyboardTheme(
            id = "pastel_blue",
            name = "Nord Cyan Slate",
            keyboardBackground = Color.parseColor("#0F172A"),
            keyBackground = Color.parseColor("#1E293B"),
            keyPressedBackground = Color.parseColor("#334155"),
            keyStrokeColor = Color.parseColor("#38BDF8"),
            keyTextColor = Color.parseColor("#F8FAFC"),
            keySubTextColor = Color.parseColor("#38BDF8"),
            controlKeyBackground = Color.parseColor("#121927"),
            controlKeyPressedBackground = Color.parseColor("#1E293B"),
            controlKeyStrokeColor = Color.TRANSPARENT,
            controlKeyTextColor = Color.parseColor("#94A3B8"),
            numberKeyBackground = Color.parseColor("#151D2C"),
            numberKeyPressedBackground = Color.parseColor("#1E293B"),
            numberKeyTextColor = Color.parseColor("#94A3B8"),
            previewBackground = Color.parseColor("#1E293B"),
            previewTextColor = Color.WHITE,
            previewStrokeColor = Color.parseColor("#38BDF8"),
            previewHoldBackground = Color.parseColor("#0284C7"),
            previewHoldTextColor = Color.WHITE,
            accentColor = Color.parseColor("#38BDF8")
        )

        val LIGHT = KeyboardTheme(
            id = "light",
            name = "Clean Minimalist Light",
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
            previewHoldBackground = Color.parseColor("#0284C7"),
            previewHoldTextColor = Color.WHITE,
            accentColor = Color.parseColor("#0284C7")
        )

        val MATRIX = KeyboardTheme(
            id = "matrix",
            name = "Matrix Green Rice",
            keyboardBackground = Color.parseColor("#051208"),
            keyBackground = Color.parseColor("#0D2313"),
            keyPressedBackground = Color.parseColor("#163A20"),
            keyStrokeColor = Color.parseColor("#22C55E"),
            keyTextColor = Color.parseColor("#4ADE80"),
            keySubTextColor = Color.parseColor("#22C55E"),
            controlKeyBackground = Color.parseColor("#081A0D"),
            controlKeyPressedBackground = Color.parseColor("#163A20"),
            controlKeyStrokeColor = Color.TRANSPARENT,
            controlKeyTextColor = Color.parseColor("#86EFAC"),
            numberKeyBackground = Color.parseColor("#0A1E0F"),
            numberKeyPressedBackground = Color.parseColor("#163A20"),
            numberKeyTextColor = Color.parseColor("#86EFAC"),
            previewBackground = Color.parseColor("#0D2313"),
            previewTextColor = Color.parseColor("#4ADE80"),
            previewStrokeColor = Color.parseColor("#22C55E"),
            previewHoldBackground = Color.parseColor("#22C55E"),
            previewHoldTextColor = Color.parseColor("#051208"),
            accentColor = Color.parseColor("#4ADE80")
        )

        val GRUVBOX = KeyboardTheme(
            id = "gruvbox",
            name = "Gruvbox Dark Rice",
            keyboardBackground = Color.parseColor("#1D2021"),
            keyBackground = Color.parseColor("#282828"),
            keyPressedBackground = Color.parseColor("#3C3836"),
            keyStrokeColor = Color.parseColor("#FE8019"),
            keyTextColor = Color.parseColor("#EBDBB2"),
            keySubTextColor = Color.parseColor("#D79921"),
            controlKeyBackground = Color.parseColor("#202020"),
            controlKeyPressedBackground = Color.parseColor("#3C3836"),
            controlKeyStrokeColor = Color.TRANSPARENT,
            controlKeyTextColor = Color.parseColor("#D5C4A1"),
            numberKeyBackground = Color.parseColor("#242424"),
            numberKeyPressedBackground = Color.parseColor("#3C3836"),
            numberKeyTextColor = Color.parseColor("#D5C4A1"),
            previewBackground = Color.parseColor("#282828"),
            previewTextColor = Color.parseColor("#EBDBB2"),
            previewStrokeColor = Color.parseColor("#FE8019"),
            previewHoldBackground = Color.parseColor("#FE8019"),
            previewHoldTextColor = Color.parseColor("#1D2021"),
            accentColor = Color.parseColor("#FE8019")
        )

        val ALL_THEMES = listOf(DARK, PASTEL_BLUE, LIGHT, MATRIX, GRUVBOX)

        fun getThemeById(id: String): KeyboardTheme {
            return ALL_THEMES.find { it.id == id } ?: DARK
        }
    }
}
