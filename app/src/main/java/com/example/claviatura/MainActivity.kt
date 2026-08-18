package com.example.claviatura

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Secure
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: KeyboardPreferences

    // System & Status
    private lateinit var riceThemeBadge: TextView
    private lateinit var activationPromptCard: View
    private lateinit var enableImeButton: Button
    private lateinit var selectImeButton: Button

    // Live Sandbox
    private lateinit var testEditText: EditText
    private lateinit var clearSandboxButton: TextView
    private lateinit var focusKeyboardButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = KeyboardPreferences(this)

        initRiceHeaderAndActivation()
        initLiveSandbox()
        initGeometryControls()
        initTimingControls()
        initHapticAndSoundControls()
        initLayoutAndPreviewControls()
        initThemeControls()
    }

    override fun onResume() {
        super.onResume()
        checkActivationState()
        updateThemeBadge()
    }

    private fun initRiceHeaderAndActivation() {
        riceThemeBadge = findViewById(R.id.riceThemeBadge)
        activationPromptCard = findViewById(R.id.activationPromptCard)
        enableImeButton = findViewById(R.id.enableImeButton)
        selectImeButton = findViewById(R.id.selectImeButton)

        enableImeButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        selectImeButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        checkActivationState()
        updateThemeBadge()
    }

    private fun checkActivationState() {
        val enabled = isKeyboardEnabled()
        val defaultSelected = isKeyboardSelectedAsDefault()

        if (enabled && defaultSelected) {
            // Already active and default: hide activation prompt completely
            activationPromptCard.visibility = View.GONE
        } else {
            // Needs activation: show sleek inline prompt
            activationPromptCard.visibility = View.VISIBLE
            val desc = findViewById<TextView>(R.id.activationDescription)
            if (!enabled) {
                desc.text = "❯ STEP 1: 시스템 설정에서 Claviatura 키보드를 활성화하세요."
            } else {
                desc.text = "❯ STEP 2: 기본 입력 방법을 Claviatura로 전환하세요."
            }
        }
    }

    private fun updateThemeBadge() {
        val themeName = when (prefs.selectedThemeId) {
            "dark" -> "Catppuccin Mocha [Dark]"
            "pastel_blue" -> "Nord Cyan Slate [Pastel]"
            else -> "Clean Minimalist [Light]"
        }
        riceThemeBadge.text = "❯ THEME   : $themeName"
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

    private fun initGeometryControls() {
        // 1. Height: 180 ~ 360 (progress: 0 ~ 180, offset: 180)
        val heightSeekBar = findViewById<SeekBar>(R.id.heightSeekBar)
        val heightValue = findViewById<TextView>(R.id.heightValue)
        val currentHeight = prefs.keyboardHeight.coerceIn(180, 360)
        heightSeekBar.progress = currentHeight - 180
        heightValue.text = "${currentHeight}dp"

        heightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = 180 + progress
                prefs.keyboardHeight = value
                heightValue.text = "${value}dp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 2. Bottom Margin: 0 ~ 60
        val bottomMarginSeekBar = findViewById<SeekBar>(R.id.bottomMarginSeekBar)
        val bottomMarginValue = findViewById<TextView>(R.id.bottomMarginValue)
        val currentBottomMargin = prefs.bottomMargin.coerceIn(0, 60)
        bottomMarginSeekBar.progress = currentBottomMargin
        bottomMarginValue.text = "${currentBottomMargin}dp"

        bottomMarginSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.bottomMargin = progress
                bottomMarginValue.text = "${progress}dp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 3. Side Margin: 0 ~ 40
        val horizontalMarginSeekBar = findViewById<SeekBar>(R.id.horizontalMarginSeekBar)
        val horizontalMarginValue = findViewById<TextView>(R.id.horizontalMarginValue)
        val currentHMargin = prefs.horizontalMargin.coerceIn(0, 40)
        horizontalMarginSeekBar.progress = currentHMargin
        horizontalMarginValue.text = "${currentHMargin}dp"

        horizontalMarginSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.horizontalMargin = progress
                horizontalMarginValue.text = "${progress}dp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 4. Corner Radius: 0 ~ 24
        val cornerRadiusSeekBar = findViewById<SeekBar>(R.id.cornerRadiusSeekBar)
        val cornerRadiusValue = findViewById<TextView>(R.id.cornerRadiusValue)
        val currentRadius = prefs.keyCornerRadius.coerceIn(0, 24)
        cornerRadiusSeekBar.progress = currentRadius
        cornerRadiusValue.text = "${currentRadius}dp"

        cornerRadiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.keyCornerRadius = progress
                cornerRadiusValue.text = "${progress}dp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun initTimingControls() {
        // 1. Long Press Timeout: 100 ~ 600ms (progress: 0 ~ 50, step: 10ms, offset: 100ms)
        val longPressSeekBar = findViewById<SeekBar>(R.id.longPressSeekBar)
        val longPressValue = findViewById<TextView>(R.id.longPressValue)
        val currentLongPress = prefs.longPressTimeout.toInt().coerceIn(100, 600)
        longPressSeekBar.progress = (currentLongPress - 100) / 10
        longPressValue.text = "${currentLongPress}ms"

        longPressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = 100L + (progress * 10L)
                prefs.longPressTimeout = value
                longPressValue.text = "${value}ms"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 2. Continuous Delete Hold Delay: 100 ~ 600ms
        val deleteHoldSeekBar = findViewById<SeekBar>(R.id.deleteHoldSeekBar)
        val deleteHoldValue = findViewById<TextView>(R.id.deleteHoldValue)
        val currentDeleteHold = prefs.deleteHoldDelay.toInt().coerceIn(100, 600)
        deleteHoldSeekBar.progress = (currentDeleteHold - 100) / 10
        deleteHoldValue.text = "${currentDeleteHold}ms"

        deleteHoldSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = 100L + (progress * 10L)
                prefs.deleteHoldDelay = value
                deleteHoldValue.text = "${value}ms"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 3. Delete Repeat Speed: 20 ~ 100ms
        val deleteRepeatSeekBar = findViewById<SeekBar>(R.id.deleteRepeatSeekBar)
        val deleteRepeatValue = findViewById<TextView>(R.id.deleteRepeatValue)
        val currentRepeat = prefs.deleteRepeatInterval.toInt().coerceIn(20, 100)
        deleteRepeatSeekBar.progress = currentRepeat - 20
        deleteRepeatValue.text = "${currentRepeat}ms"

        deleteRepeatSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = 20L + progress
                prefs.deleteRepeatInterval = value
                deleteRepeatValue.text = "${value}ms"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun initHapticAndSoundControls() {
        val strengthContainer = findViewById<View>(R.id.vibrateStrengthContainer)
        val vibrateSwitch = findViewById<Switch>(R.id.vibrateSwitch)

        vibrateSwitch.isChecked = prefs.vibrateEnabled
        strengthContainer.visibility = if (prefs.vibrateEnabled) View.VISIBLE else View.GONE

        vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.vibrateEnabled = isChecked
            strengthContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        val strengthSeekBar = findViewById<SeekBar>(R.id.vibrateStrengthSeekBar)
        val strengthValue = findViewById<TextView>(R.id.vibrateStrengthValue)
        val currentStrength = prefs.vibrateStrength.coerceIn(5, 100)
        strengthSeekBar.progress = currentStrength
        strengthValue.text = "$currentStrength"

        strengthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val strength = progress.coerceAtLeast(5)
                prefs.vibrateStrength = strength
                strengthValue.text = "$strength"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        findViewById<Switch>(R.id.soundSwitch).apply {
            isChecked = prefs.soundEnabled
            setOnCheckedChangeListener { _, isChecked -> prefs.soundEnabled = isChecked }
        }
    }

    private fun initLayoutAndPreviewControls() {
        findViewById<Switch>(R.id.showNumberRowSwitch).apply {
            isChecked = prefs.showNumberRow
            setOnCheckedChangeListener { _, isChecked -> prefs.showNumberRow = isChecked }
        }

        findViewById<Switch>(R.id.showPeriodKeySwitch).apply {
            isChecked = prefs.showPeriodKey
            setOnCheckedChangeListener { _, isChecked -> prefs.showPeriodKey = isChecked }
        }

        findViewById<Switch>(R.id.showKeyPreviewSwitch).apply {
            isChecked = prefs.showKeyPreview
            setOnCheckedChangeListener { _, isChecked -> prefs.showKeyPreview = isChecked }
        }
    }

    private fun initThemeControls() {
        val radioGroup = findViewById<RadioGroup>(R.id.themeRadioGroup)
        when (prefs.selectedThemeId) {
            "light" -> radioGroup.check(R.id.themeLightRadio)
            "pastel_blue" -> radioGroup.check(R.id.themePastelBlueRadio)
            else -> radioGroup.check(R.id.themeDarkRadio)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.themeLightRadio -> prefs.selectedThemeId = "light"
                R.id.themePastelBlueRadio -> prefs.selectedThemeId = "pastel_blue"
                else -> prefs.selectedThemeId = "dark"
            }
            updateThemeBadge()
        }
    }

    private fun isKeyboardEnabled(): Boolean {
        return try {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.enabledInputMethodList.any { method ->
                method.packageName == packageName
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun isKeyboardSelectedAsDefault(): Boolean {
        return try {
            val defaultIme = Secure.getString(contentResolver, Secure.DEFAULT_INPUT_METHOD)
            defaultIme != null && (defaultIme.startsWith("${packageName}/") || defaultIme == packageName)
        } catch (_: Exception) {
            false
        }
    }
}
