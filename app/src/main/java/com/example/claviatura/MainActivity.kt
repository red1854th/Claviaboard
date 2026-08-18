package com.example.claviatura

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings.Secure
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: KeyboardPreferences

    // System & Status
    private lateinit var riceThemeBadge: TextView
    private lateinit var mainKoreanLayoutBadge: TextView
    private lateinit var mainEnglishLayoutBadge: TextView
    private lateinit var btnLanguageSettings: View

    // Live Sandbox
    private lateinit var testEditText: EditText
    private lateinit var clearSandboxButton: TextView
    private lateinit var focusKeyboardButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = KeyboardPreferences(this)

        if (!isKeyboardEnabled() || !isKeyboardSelectedAsDefault()) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        initRiceHeaderAndLanguageCard()
        initLiveSandbox()
        initKeyVisibilityControls()
        initGeometrySliders()
        initTypingBehaviorControls()
        initTimingSliders()
        initNumberKeypadControls()
        initHapticAndAudioControls()
        initThemeControls()
    }

    override fun onResume() {
        super.onResume()
        if (!isKeyboardEnabled() || !isKeyboardSelectedAsDefault()) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        updateBadges()
    }

    private fun initRiceHeaderAndLanguageCard() {
        riceThemeBadge = findViewById(R.id.riceThemeBadge)
        mainKoreanLayoutBadge = findViewById(R.id.mainKoreanLayoutBadge)
        mainEnglishLayoutBadge = findViewById(R.id.mainEnglishLayoutBadge)
        btnLanguageSettings = findViewById(R.id.btnLanguageSettings)

        btnLanguageSettings.setOnClickListener {
            startActivity(Intent(this, LanguageSettingsActivity::class.java))
        }

        updateBadges()
    }

    private fun updateBadges() {
        mainKoreanLayoutBadge.text = "❯ KOREAN  : ${prefs.koreanLayoutType}"
        mainEnglishLayoutBadge.text = "❯ ENGLISH : ${prefs.englishLayoutType}"
        val theme = KeyboardTheme.getThemeById(prefs.selectedThemeId)
        riceThemeBadge.text = "❯ THEME   : ${theme.name}"
    }

    private fun initLiveSandbox() {
        testEditText = findViewById(R.id.testEditText)
        clearSandboxButton = findViewById(R.id.clearSandboxButton)
        focusKeyboardButton = findViewById(R.id.focusKeyboardButton)

        clearSandboxButton.setOnClickListener {
            testEditText.setText("")
        }

        focusKeyboardButton.setOnClickListener {
            testEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(testEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    // 01: KEYBOARD KEYS & VISIBILITY
    private fun initKeyVisibilityControls() {
        val btnToggleNumberRow = findViewById<Button>(R.id.btnToggleNumberRow)
        val btnToggleSpecialKey = findViewById<Button>(R.id.btnToggleSpecialKey)
        val btnTogglePeriodKey = findViewById<Button>(R.id.btnTogglePeriodKey)
        val btnToggleVoiceKey = findViewById<Button>(R.id.btnToggleVoiceKey)
        val btnToggleEmojiKey = findViewById<Button>(R.id.btnToggleEmojiKey)

        updateToggleButton(btnToggleNumberRow, "NUMBER ROW", prefs.showNumberRow)
        updateToggleButton(btnToggleSpecialKey, "SPECIAL KEY (@/SYM)", prefs.showSpecialKey)
        updateToggleButton(btnTogglePeriodKey, "PERIOD KEY (.)", prefs.showPeriodKey)
        updateToggleButton(btnToggleVoiceKey, "VOICE / ACTION (漢)", prefs.showVoiceKey)
        updateToggleButton(btnToggleEmojiKey, "EMOJI / SYMBOL", prefs.showEmojiKey)

        btnToggleNumberRow.setOnClickListener {
            prefs.showNumberRow = !prefs.showNumberRow
            updateToggleButton(btnToggleNumberRow, "NUMBER ROW", prefs.showNumberRow)
        }

        btnToggleSpecialKey.setOnClickListener {
            prefs.showSpecialKey = !prefs.showSpecialKey
            updateToggleButton(btnToggleSpecialKey, "SPECIAL KEY (@/SYM)", prefs.showSpecialKey)
        }

        btnTogglePeriodKey.setOnClickListener {
            prefs.showPeriodKey = !prefs.showPeriodKey
            updateToggleButton(btnTogglePeriodKey, "PERIOD KEY (.)", prefs.showPeriodKey)
        }

        btnToggleVoiceKey.setOnClickListener {
            prefs.showVoiceKey = !prefs.showVoiceKey
            updateToggleButton(btnToggleVoiceKey, "VOICE / ACTION (漢)", prefs.showVoiceKey)
        }

        btnToggleEmojiKey.setOnClickListener {
            prefs.showEmojiKey = !prefs.showEmojiKey
            updateToggleButton(btnToggleEmojiKey, "EMOJI / SYMBOL", prefs.showEmojiKey)
        }
    }

    // 02: GEOMETRY & SIZING (Terminal Sliders)
    private fun initGeometrySliders() {
        val sliderHeight = findViewById<TerminalSliderBarView>(R.id.sliderKeyboardHeight)
        sliderHeight.apply {
            label = "keyboard_height"
            unit = "dp"
            minValue = 180
            maxValue = 360
            stepSize = 4
            showPercent = true
            currentValue = prefs.keyboardHeight
            accentColor = Color.parseColor("#38BDF8")
            onValueChanged = { prefs.keyboardHeight = it }
        }

        val sliderHMargin = findViewById<TerminalSliderBarView>(R.id.sliderHorizontalMargin)
        sliderHMargin.apply {
            label = "horizontal_margin"
            unit = "dp"
            minValue = 0
            maxValue = 32
            stepSize = 1
            showPercent = false
            currentValue = prefs.horizontalMargin
            accentColor = Color.parseColor("#38BDF8")
            onValueChanged = { prefs.horizontalMargin = it }
        }

        val sliderBMargin = findViewById<TerminalSliderBarView>(R.id.sliderBottomMargin)
        sliderBMargin.apply {
            label = "bottom_margin"
            unit = "dp"
            minValue = 0
            maxValue = 48
            stepSize = 2
            showPercent = false
            currentValue = prefs.bottomMargin
            accentColor = Color.parseColor("#38BDF8")
            onValueChanged = { prefs.bottomMargin = it }
        }

        val sliderCornerRadius = findViewById<TerminalSliderBarView>(R.id.sliderKeyCornerRadius)
        sliderCornerRadius.apply {
            label = "key_corner_radius"
            unit = "px"
            minValue = 0
            maxValue = 16
            stepSize = 1
            showPercent = false
            currentValue = prefs.keyCornerRadius
            accentColor = Color.parseColor("#38BDF8")
            onValueChanged = { prefs.keyCornerRadius = it }
        }
    }

    // 03: TYPING BEHAVIOR & ENGINE
    private fun initTypingBehaviorControls() {
        val btnToggleSystemFont = findViewById<Button>(R.id.btnToggleSystemFont)
        val btnToggleKeyPreview = findViewById<Button>(R.id.btnToggleKeyPreview)
        val btnToggleNumberRowLandscape = findViewById<Button>(R.id.btnToggleNumberRowLandscape)
        val btnToggleDoubleSpacePeriod = findViewById<Button>(R.id.btnToggleDoubleSpacePeriod)
        val btnToggleAutoCapitalize = findViewById<Button>(R.id.btnToggleAutoCapitalize)
        val btnToggleDoubleConsonant = findViewById<Button>(R.id.btnToggleDoubleConsonant)

        updateToggleButton(btnToggleSystemFont, "USE SYSTEM FONT", prefs.useSystemFont)
        updateToggleButton(btnToggleKeyPreview, "KEY POPUP PREVIEW", prefs.showKeyPreview)
        updateToggleButton(btnToggleNumberRowLandscape, "NUMBER ROW IN LANDSCAPE", prefs.numberRowLandscape)
        updateToggleButton(btnToggleDoubleSpacePeriod, "DOUBLE SPACE PERIOD (.)", prefs.doubleSpacePeriod)
        updateToggleButton(btnToggleAutoCapitalize, "AUTO CAPITALIZE FIRST LETTER", prefs.autoCapitalize)
        updateToggleButton(btnToggleDoubleConsonant, "DOUBLE TAP TENSE CONSONANT (ㄱ+ㄱ->ㄲ)", prefs.doubleConsonantMode)

        btnToggleSystemFont.setOnClickListener {
            prefs.useSystemFont = !prefs.useSystemFont
            updateToggleButton(btnToggleSystemFont, "USE SYSTEM FONT", prefs.useSystemFont)
        }

        btnToggleKeyPreview.setOnClickListener {
            prefs.showKeyPreview = !prefs.showKeyPreview
            updateToggleButton(btnToggleKeyPreview, "KEY POPUP PREVIEW", prefs.showKeyPreview)
        }

        btnToggleNumberRowLandscape.setOnClickListener {
            prefs.numberRowLandscape = !prefs.numberRowLandscape
            updateToggleButton(btnToggleNumberRowLandscape, "NUMBER ROW IN LANDSCAPE", prefs.numberRowLandscape)
        }

        btnToggleDoubleSpacePeriod.setOnClickListener {
            prefs.doubleSpacePeriod = !prefs.doubleSpacePeriod
            updateToggleButton(btnToggleDoubleSpacePeriod, "DOUBLE SPACE PERIOD (.)", prefs.doubleSpacePeriod)
        }

        btnToggleAutoCapitalize.setOnClickListener {
            prefs.autoCapitalize = !prefs.autoCapitalize
            updateToggleButton(btnToggleAutoCapitalize, "AUTO CAPITALIZE FIRST LETTER", prefs.autoCapitalize)
        }

        btnToggleDoubleConsonant.setOnClickListener {
            prefs.doubleConsonantMode = !prefs.doubleConsonantMode
            updateToggleButton(btnToggleDoubleConsonant, "DOUBLE TAP TENSE CONSONANT (ㄱ+ㄱ->ㄲ)", prefs.doubleConsonantMode)
        }
    }

    // 04: TIMINGS & SENSITIVITY (Terminal Sliders)
    private fun initTimingSliders() {
        val sliderSensitivity = findViewById<TerminalSliderBarView>(R.id.sliderCursorSensitivity)
        sliderSensitivity.apply {
            label = "cursor_sensitivity"
            unit = " Level"
            minValue = 1
            maxValue = 10
            stepSize = 1
            showPercent = false
            currentValue = prefs.cursorSensitivity
            accentColor = Color.parseColor("#EC4899")
            onValueChanged = { prefs.cursorSensitivity = it }
        }

        val sliderLongPress = findViewById<TerminalSliderBarView>(R.id.sliderLongPressTimeout)
        sliderLongPress.apply {
            label = "long_press_delay"
            unit = "ms"
            minValue = 100
            maxValue = 900
            stepSize = 25
            showPercent = false
            currentValue = prefs.longPressTimeout.toInt()
            accentColor = Color.parseColor("#EC4899")
            onValueChanged = { prefs.longPressTimeout = it.toLong() }
        }

        val sliderDeleteHold = findViewById<TerminalSliderBarView>(R.id.sliderDeleteHoldDelay)
        sliderDeleteHold.apply {
            label = "backspace_hold_delay"
            unit = "ms"
            minValue = 100
            maxValue = 600
            stepSize = 25
            showPercent = false
            currentValue = prefs.deleteHoldDelay.toInt()
            accentColor = Color.parseColor("#EC4899")
            onValueChanged = { prefs.deleteHoldDelay = it.toLong() }
        }

        val sliderDeleteRate = findViewById<TerminalSliderBarView>(R.id.sliderDeleteRepeatInterval)
        sliderDeleteRate.apply {
            label = "backspace_repeat_rate"
            unit = "ms"
            minValue = 20
            maxValue = 100
            stepSize = 5
            showPercent = false
            currentValue = prefs.deleteRepeatInterval.toInt()
            accentColor = Color.parseColor("#EC4899")
            onValueChanged = { prefs.deleteRepeatInterval = it.toLong() }
        }
    }

    // 05: NUMBER KEYPAD TYPE
    private fun initNumberKeypadControls() {
        val btnLinked = findViewById<Button>(R.id.btnNumberKeypadLinked)
        val btn3x4 = findViewById<Button>(R.id.btnNumberKeypad3x4)
        val btnQwerty = findViewById<Button>(R.id.btnNumberKeypadQwerty)
        val buttons = listOf(btnLinked, btn3x4, btnQwerty)

        fun updateSelection() {
            when (prefs.numberKeypadType) {
                "pin_3x4" -> selectChip(btn3x4, buttons, "#14B8A6")
                "qwerty" -> selectChip(btnQwerty, buttons, "#14B8A6")
                else -> selectChip(btnLinked, buttons, "#14B8A6")
            }
        }

        btnLinked.setOnClickListener {
            prefs.numberKeypadType = "linked"
            updateSelection()
        }
        btn3x4.setOnClickListener {
            prefs.numberKeypadType = "pin_3x4"
            updateSelection()
        }
        btnQwerty.setOnClickListener {
            prefs.numberKeypadType = "qwerty"
            updateSelection()
        }

        updateSelection()
    }

    // 06: HAPTIC & AUDIO ENGINE
    private fun initHapticAndAudioControls() {
        val btnToggleSound = findViewById<Button>(R.id.btnToggleSound)
        updateToggleButton(btnToggleSound, "KEYBOARD SOUND", prefs.soundEnabled)
        btnToggleSound.setOnClickListener {
            prefs.soundEnabled = !prefs.soundEnabled
            updateToggleButton(btnToggleSound, "KEYBOARD SOUND", prefs.soundEnabled)
        }

        val btnSoundMech = findViewById<Button>(R.id.btnSoundMech)
        val btnSoundPop = findViewById<Button>(R.id.btnSoundPop)
        val btnSoundTypewriter = findViewById<Button>(R.id.btnSoundTypewriter)
        val btnSoundTerminal = findViewById<Button>(R.id.btnSoundTerminal)
        val soundButtons = listOf(btnSoundMech, btnSoundPop, btnSoundTypewriter, btnSoundTerminal)

        fun updateSoundSelection() {
            when (prefs.soundProfile) {
                "soft_pop" -> selectChip(btnSoundPop, soundButtons, "#38BDF8")
                "classic_typewriter" -> selectChip(btnSoundTypewriter, soundButtons, "#38BDF8")
                "terminal_beep" -> selectChip(btnSoundTerminal, soundButtons, "#38BDF8")
                else -> selectChip(btnSoundMech, soundButtons, "#38BDF8")
            }
        }

        btnSoundMech.setOnClickListener {
            prefs.soundProfile = "mech_click"
            updateSoundSelection()
        }
        btnSoundPop.setOnClickListener {
            prefs.soundProfile = "soft_pop"
            updateSoundSelection()
        }
        btnSoundTypewriter.setOnClickListener {
            prefs.soundProfile = "classic_typewriter"
            updateSoundSelection()
        }
        btnSoundTerminal.setOnClickListener {
            prefs.soundProfile = "terminal_beep"
            updateSoundSelection()
        }
        updateSoundSelection()

        val btnToggleVibrate = findViewById<Button>(R.id.btnToggleVibrate)
        updateToggleButton(btnToggleVibrate, "KEYBOARD VIBRATION", prefs.vibrateEnabled)
        btnToggleVibrate.setOnClickListener {
            prefs.vibrateEnabled = !prefs.vibrateEnabled
            updateToggleButton(btnToggleVibrate, "KEYBOARD VIBRATION", prefs.vibrateEnabled)
        }

        val sliderVibrate = findViewById<TerminalSliderBarView>(R.id.sliderVibrateStrength)
        sliderVibrate.apply {
            label = "vibration_strength"
            unit = "%"
            minValue = 0
            maxValue = 100
            stepSize = 5
            showPercent = false
            currentValue = prefs.vibrateStrength
            accentColor = Color.parseColor("#6366F1")
            onValueChanged = { prefs.vibrateStrength = it }
        }
    }

    // 07: APPEARANCE & DARK MODE
    private fun initThemeControls() {
        val btnToggleDarkModeAuto = findViewById<Button>(R.id.btnToggleDarkModeAuto)
        updateToggleButton(btnToggleDarkModeAuto, "FOLLOW SYSTEM DARK MODE", prefs.followSystemDarkMode)
        btnToggleDarkModeAuto.setOnClickListener {
            prefs.followSystemDarkMode = !prefs.followSystemDarkMode
            updateToggleButton(btnToggleDarkModeAuto, "FOLLOW SYSTEM DARK MODE", prefs.followSystemDarkMode)
        }

        val btnThemeDark = findViewById<Button>(R.id.btnThemeDark)
        val btnThemeNord = findViewById<Button>(R.id.btnThemeNord)
        val btnThemeLight = findViewById<Button>(R.id.btnThemeLight)
        val btnThemeMatrix = findViewById<Button>(R.id.btnThemeMatrix)
        val btnThemeGruvbox = findViewById<Button>(R.id.btnThemeGruvbox)
        val themeButtons = listOf(btnThemeDark, btnThemeNord, btnThemeLight, btnThemeMatrix, btnThemeGruvbox)

        fun updateThemeSelection() {
            when (prefs.selectedThemeId) {
                "pastel_blue" -> selectChip(btnThemeNord, themeButtons, "#38BDF8")
                "light" -> selectChip(btnThemeLight, themeButtons, "#F59E0B")
                "matrix" -> selectChip(btnThemeMatrix, themeButtons, "#4ADE80")
                "gruvbox" -> selectChip(btnThemeGruvbox, themeButtons, "#FE8019")
                else -> selectChip(btnThemeDark, themeButtons, "#38BDF8")
            }
            updateBadges()
        }

        btnThemeDark.setOnClickListener {
            prefs.selectedThemeId = "dark"
            updateThemeSelection()
        }
        btnThemeNord.setOnClickListener {
            prefs.selectedThemeId = "pastel_blue"
            updateThemeSelection()
        }
        btnThemeLight.setOnClickListener {
            prefs.selectedThemeId = "light"
            updateThemeSelection()
        }
        btnThemeMatrix.setOnClickListener {
            prefs.selectedThemeId = "matrix"
            updateThemeSelection()
        }
        btnThemeGruvbox.setOnClickListener {
            prefs.selectedThemeId = "gruvbox"
            updateThemeSelection()
        }

        updateThemeSelection()
    }

    private fun updateToggleButton(button: Button, label: String, enabled: Boolean) {
        if (enabled) {
            button.text = "[ ● ON  |  $label ]"
            button.setTextColor(Color.parseColor("#4ADE80"))
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#152338"))
        } else {
            button.text = "[ ○ OFF |  $label ]"
            button.setTextColor(Color.parseColor("#94A3B8"))
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0E131F"))
        }
    }

    private fun selectChip(selectedBtn: Button, allButtons: List<Button>, activeColor: String = "#38BDF8") {
        allButtons.forEach { btn ->
            if (btn == selectedBtn) {
                btn.setTextColor(Color.parseColor(activeColor))
                btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#152338"))
            } else {
                btn.setTextColor(Color.parseColor("#64748B"))
                btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0E131F"))
            }
        }
    }

    private fun isKeyboardEnabled(): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val enabledMethods = imm.enabledInputMethodList
        val myPackage = packageName
        return enabledMethods.any { it.packageName == myPackage }
    }

    private fun isKeyboardSelectedAsDefault(): Boolean {
        val currentIme = Secure.getString(contentResolver, Secure.DEFAULT_INPUT_METHOD)
        return currentIme != null && currentIme.contains(packageName)
    }
}
