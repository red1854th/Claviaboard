package com.example.claviatura

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Secure
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var section1: View
    private lateinit var section2: View
    private lateinit var section3: View

    private lateinit var step1Number: TextView
    private lateinit var step1Title: TextView
    private lateinit var step1Icon: TextView

    private lateinit var step2Number: TextView
    private lateinit var step2Title: TextView
    private lateinit var step2Icon: TextView

    private lateinit var step3Number: TextView
    private lateinit var step3Title: TextView
    private lateinit var step3Icon: TextView

    private var currentStep: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        section1 = findViewById(R.id.section1)
        section2 = findViewById(R.id.section2)
        section3 = findViewById(R.id.section3)

        step1Number = findViewById(R.id.step1Number)
        step1Title = findViewById(R.id.step1Title)
        step1Icon = findViewById(R.id.step1Icon)

        step2Number = findViewById(R.id.step2Number)
        step2Title = findViewById(R.id.step2Title)
        step2Icon = findViewById(R.id.step2Icon)

        step3Number = findViewById(R.id.step3Number)
        step3Title = findViewById(R.id.step3Title)
        step3Icon = findViewById(R.id.step3Icon)

        val clickListener = View.OnClickListener {
            handleSetupStepAction()
        }

        findViewById<View>(R.id.onboardingRoot).setOnClickListener(clickListener)
        section1.setOnClickListener(clickListener)
        section2.setOnClickListener(clickListener)
        section3.setOnClickListener(clickListener)
    }

    override fun onResume() {
        super.onResume()
        updateSetupState()
    }

    private fun updateSetupState() {
        val enabled = isKeyboardEnabled()
        val defaultSelected = isKeyboardSelectedAsDefault()

        currentStep = when {
            !enabled -> 1
            !defaultSelected -> 2
            else -> 3
        }

        val activeBlue = Color.parseColor("#4C84F3")
        val completedBlue = Color.parseColor("#3B73E2")
        val inactiveBg = Color.parseColor("#F8FAFC")
        val textMuted = Color.parseColor("#94A3B8")
        val textWhite = Color.WHITE

        when (currentStep) {
            1 -> {
                // Step 1 Active
                section1.setBackgroundColor(activeBlue)
                step1Number.setTextColor(textWhite)
                step1Title.setTextColor(textWhite)
                step1Icon.setTextColor(textWhite)
                step1Icon.text = "→"

                // Step 2 Inactive
                section2.setBackgroundColor(inactiveBg)
                step2Number.setTextColor(textMuted)
                step2Title.setTextColor(textMuted)
                step2Icon.setTextColor(textMuted)
                step2Icon.text = "→"

                // Step 3 Inactive
                section3.setBackgroundColor(inactiveBg)
                step3Number.setTextColor(textMuted)
                step3Title.setTextColor(textMuted)
                step3Icon.setTextColor(textMuted)
                step3Icon.text = "→"
            }
            2 -> {
                // Step 1 Completed
                section1.setBackgroundColor(completedBlue)
                step1Number.setTextColor(textWhite)
                step1Title.setTextColor(textWhite)
                step1Icon.setTextColor(textWhite)
                step1Icon.text = "✓"

                // Step 2 Active
                section2.setBackgroundColor(activeBlue)
                step2Number.setTextColor(textWhite)
                step2Title.setTextColor(textWhite)
                step2Icon.setTextColor(textWhite)
                step2Icon.text = "→"

                // Step 3 Inactive
                section3.setBackgroundColor(inactiveBg)
                step3Number.setTextColor(textMuted)
                step3Title.setTextColor(textMuted)
                step3Icon.setTextColor(textMuted)
                step3Icon.text = "→"
            }
            3 -> {
                // Step 1 Completed
                section1.setBackgroundColor(completedBlue)
                step1Number.setTextColor(textWhite)
                step1Title.setTextColor(textWhite)
                step1Icon.setTextColor(textWhite)
                step1Icon.text = "✓"

                // Step 2 Completed
                section2.setBackgroundColor(completedBlue)
                step2Number.setTextColor(textWhite)
                step2Title.setTextColor(textWhite)
                step2Icon.setTextColor(textWhite)
                step2Icon.text = "✓"

                // Step 3 Active (Ready)
                section3.setBackgroundColor(activeBlue)
                step3Number.setTextColor(textWhite)
                step3Title.setTextColor(textWhite)
                step3Icon.setTextColor(textWhite)
                step3Icon.text = "시작하기 →"
            }
        }
    }

    private fun handleSetupStepAction() {
        when (currentStep) {
            1 -> {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }
            2 -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
            3 -> {
                val prefs = KeyboardPreferences(this)
                prefs.firstRun = false
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun isKeyboardEnabled(): Boolean {
        return try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.enabledInputMethodList.any { it.packageName == packageName }
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
