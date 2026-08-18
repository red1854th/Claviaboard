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
    private lateinit var step1Command: TextView
    private lateinit var step1Icon: TextView

    private lateinit var step2Number: TextView
    private lateinit var step2Title: TextView
    private lateinit var step2Command: TextView
    private lateinit var step2Icon: TextView

    private lateinit var step3Number: TextView
    private lateinit var step3Title: TextView
    private lateinit var step3Command: TextView
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
        step1Command = findViewById(R.id.step1Command)
        step1Icon = findViewById(R.id.step1Icon)

        step2Number = findViewById(R.id.step2Number)
        step2Title = findViewById(R.id.step2Title)
        step2Command = findViewById(R.id.step2Command)
        step2Icon = findViewById(R.id.step2Icon)

        step3Number = findViewById(R.id.step3Number)
        step3Title = findViewById(R.id.step3Title)
        step3Command = findViewById(R.id.step3Command)
        step3Icon = findViewById(R.id.step3Icon)

        val clickListener = View.OnClickListener {
            handleSetupStepAction()
        }

        section1.setOnClickListener(clickListener)
        section2.setOnClickListener(clickListener)
        section3.setOnClickListener(clickListener)
        findViewById<View>(R.id.onboardingRoot).setOnClickListener(clickListener)
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

        val activeBg = Color.parseColor("#1E293B")
        val completedBg = Color.parseColor("#14233A")
        val inactiveBg = Color.parseColor("#0A0D14")

        val cyanColor = Color.parseColor("#38BDF8")
        val greenColor = Color.parseColor("#4ADE80")
        val textWhite = Color.parseColor("#F8FAFC")
        val textMuted = Color.parseColor("#64748B")
        val textDim = Color.parseColor("#475569")

        when (currentStep) {
            1 -> {
                // Step 1 Active
                section1.setBackgroundColor(activeBg)
                step1Number.setTextColor(cyanColor)
                step1Number.text = "❯ [ 01 // STEP 1 : ACTIVE ]"
                step1Title.setTextColor(textWhite)
                step1Command.setTextColor(Color.parseColor("#94A3B8"))
                step1Icon.setTextColor(cyanColor)
                step1Icon.text = "❯ Open Settings (Enable Switch) →"

                // Step 2 Inactive
                section2.setBackgroundColor(inactiveBg)
                step2Number.setTextColor(textMuted)
                step2Number.text = "[ 02 // STEP 2 ]"
                step2Title.setTextColor(textMuted)
                step2Command.setTextColor(textDim)
                step2Icon.setTextColor(textMuted)
                step2Icon.text = "❯ Unlocks after Step 1"

                // Step 3 Inactive
                section3.setBackgroundColor(inactiveBg)
                step3Number.setTextColor(textMuted)
                step3Number.text = "[ 03 // STEP 3 ]"
                step3Title.setTextColor(textMuted)
                step3Command.setTextColor(textDim)
                step3Icon.setTextColor(textMuted)
                step3Icon.text = "❯ Pending..."
            }
            2 -> {
                // Step 1 Completed
                section1.setBackgroundColor(completedBg)
                step1Number.setTextColor(greenColor)
                step1Number.text = "[OK] [ 01 // STEP 1 : ENABLED ]"
                step1Title.setTextColor(Color.parseColor("#94A3B8"))
                step1Command.setTextColor(textDim)
                step1Icon.setTextColor(greenColor)
                step1Icon.text = "❯ System IME Registered"

                // Step 2 Active
                section2.setBackgroundColor(activeBg)
                step2Number.setTextColor(cyanColor)
                step2Number.text = "❯ [ 02 // STEP 2 : SELECT DEFAULT ]"
                step2Title.setTextColor(textWhite)
                step2Command.setTextColor(Color.parseColor("#94A3B8"))
                step2Icon.setTextColor(cyanColor)
                step2Icon.text = "❯ Choose Claviatura as Default IME →"

                // Step 3 Inactive
                section3.setBackgroundColor(inactiveBg)
                step3Number.setTextColor(textMuted)
                step3Number.text = "[ 03 // STEP 3 ]"
                step3Title.setTextColor(textMuted)
                step3Command.setTextColor(textDim)
                step3Icon.setTextColor(textMuted)
                step3Icon.text = "❯ Pending..."
            }
            3 -> {
                // Step 1 Completed
                section1.setBackgroundColor(completedBg)
                step1Number.setTextColor(greenColor)
                step1Number.text = "[OK] [ 01 // STEP 1 : ENABLED ]"
                step1Title.setTextColor(Color.parseColor("#94A3B8"))
                step1Command.setTextColor(textDim)
                step1Icon.setTextColor(greenColor)
                step1Icon.text = "❯ Ready"

                // Step 2 Completed
                section2.setBackgroundColor(completedBg)
                step2Number.setTextColor(greenColor)
                step2Number.text = "[OK] [ 02 // STEP 2 : DEFAULT ]"
                step2Title.setTextColor(Color.parseColor("#94A3B8"))
                step2Command.setTextColor(textDim)
                step2Icon.setTextColor(greenColor)
                step2Icon.text = "❯ Active Default IME"

                // Step 3 Active
                section3.setBackgroundColor(activeBg)
                step3Number.setTextColor(greenColor)
                step3Number.text = "❯ [ 03 // STEP 3 : COMPLETE ]"
                step3Title.setTextColor(textWhite)
                step3Command.setTextColor(Color.parseColor("#94A3B8"))
                step3Icon.setTextColor(greenColor)
                step3Icon.text = "❯ Launch Dashboard &amp; Configure →"
            }
        }
    }

    private fun handleSetupStepAction() {
        when (currentStep) {
            1 -> {
                try {
                    startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                } catch (e: Exception) {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }
            2 -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
            3 -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun isKeyboardEnabled(): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val list = imm.enabledInputMethodList
        val myPackageName = packageName
        for (info in list) {
            if (info.packageName == myPackageName) {
                return true
            }
        }
        return false
    }

    private fun isKeyboardSelectedAsDefault(): Boolean {
        val defaultIme = Secure.getString(contentResolver, Secure.DEFAULT_INPUT_METHOD)
        return defaultIme != null && defaultIme.contains(packageName)
    }
}
