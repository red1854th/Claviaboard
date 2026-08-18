package com.example.claviatura

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class KeyboardLayoutSelectActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "extra_mode"
    }

    private lateinit var prefs: KeyboardPreferences
    private var isKoreanMode: Boolean = true

    private data class LayoutOption(
        val name: String,
        val tag: String,
        val description: String
    )

    private val koreanOptions = listOf(
        LayoutOption("Dubeolshik", "dubeolshik", "Standard 2-Set Hangul"),
        LayoutOption("Danmoeum", "danmoeum", "Short Vowels 8-Keys"),
        LayoutOption("Danmoeum+", "danmoeum_plus", "Extended Short Vowels"),
        LayoutOption("Cheonjiin", "cheonjiin", "Traditional 3x4 (ㅣ·ㅡ)"),
        LayoutOption("Cheonjiin+", "cheonjiin_plus", "3x4 with Extra Symbols"),
        LayoutOption("Naratgeul", "naratgeul", "Stroke Addition 3x4"),
        LayoutOption("Vega", "vega", "SKY / Vega 3x4 Layout")
    )

    private val englishOptions = listOf(
        LayoutOption("QWERTY", "qwerty", "Standard English QWERTY"),
        LayoutOption("Dvorak", "dvorak", "High Efficiency Layout"),
        LayoutOption("Colemak", "colemak", "Ergonomic Typing Layout"),
        LayoutOption("Workman", "workman", "Reduced Finger Travel"),
        LayoutOption("QWERTZ", "qwertz", "Central European Standard"),
        LayoutOption("AZERTY", "azerty", "French Standard Layout")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard_layout_select)

        prefs = KeyboardPreferences(this)
        val mode = intent.getStringExtra(EXTRA_MODE) ?: "korean"
        isKoreanMode = mode == "korean"

        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        titleTextView.text = if (isKoreanMode) "[ LAYOUT // KOREAN ]" else "[ LAYOUT // ENGLISH ]"

        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.testKeyboardButton).setOnClickListener {
            showKeyboardTestDialog()
        }

        populateLayoutCards()
    }

    private fun populateLayoutCards() {
        val container = findViewById<LinearLayout>(R.id.layoutGridContainer)
        container.removeAllViews()

        val options = if (isKoreanMode) koreanOptions else englishOptions
        val currentSelected = if (isKoreanMode) prefs.koreanLayoutType else prefs.englishLayoutType

        val density = resources.displayMetrics.density

        // Render in pairs (2-column rows)
        for (i in options.indices step 2) {
            val row = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = (14 * density).toInt()
                }
                orientation = LinearLayout.HORIZONTAL
                weightSum = 2f
            }

            val opt1 = options[i]
            val card1 = createLayoutCard(opt1, currentSelected)
            row.addView(card1)

            if (i + 1 < options.size) {
                val opt2 = options[i + 1]
                val card2 = createLayoutCard(opt2, currentSelected)
                row.addView(card2)
            } else {
                // Spacer for odd count
                val empty = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        marginStart = (6 * density).toInt()
                    }
                }
                row.addView(empty)
            }

            container.addView(row)
        }
    }

    private fun createLayoutCard(option: LayoutOption, currentSelected: String): View {
        val density = resources.displayMetrics.density
        val isSelected = option.name.equals(currentSelected, ignoreCase = true) ||
                (isKoreanMode && currentSelected.startsWith(option.name)) ||
                (!isKoreanMode && currentSelected.startsWith(option.name))

        val cardWrapper = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (4 * density).toInt()
                marginEnd = (4 * density).toInt()
            }
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            isClickable = true
            isFocusable = true
        }

        // Preview Frame
        val previewFrame = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (108 * density).toInt()
            )
            background = ContextCompat.getDrawable(
                this@KeyboardLayoutSelectActivity,
                if (isSelected) R.drawable.bg_layout_card_selected else R.drawable.bg_layout_card_unselected
            )
            setPadding((6 * density).toInt(), (6 * density).toInt(), (6 * density).toInt(), (6 * density).toInt())
        }

        val miniPreview = MiniKeyboardPreviewView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            tag = option.tag
        }
        previewFrame.addView(miniPreview)
        cardWrapper.addView(previewFrame)

        // Title
        val titleText = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (8 * density).toInt()
            }
            text = option.name
            typeface = android.graphics.Typeface.MONOSPACE
            textSize = 12.5f
            setTextColor(if (isSelected) Color.parseColor("#38BDF8") else Color.parseColor("#F8FAFC"))
            paint.isFakeBoldText = true
        }
        cardWrapper.addView(titleText)

        // Status badge
        val statusText = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (2 * density).toInt()
            }
            text = if (isSelected) "[ ACTIVE ]" else "[ TAP TO SET ]"
            typeface = android.graphics.Typeface.MONOSPACE
            textSize = 10f
            setTextColor(if (isSelected) Color.parseColor("#4ADE80") else Color.parseColor("#64748B"))
        }
        cardWrapper.addView(statusText)

        cardWrapper.setOnClickListener {
            if (isKoreanMode) {
                prefs.koreanLayoutType = option.name
            } else {
                prefs.englishLayoutType = option.name
            }
            populateLayoutCards()
        }

        return cardWrapper
    }

    private fun showKeyboardTestDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_test_input)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.92).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val editText = dialog.findViewById<EditText>(R.id.dialogEditText)
        dialog.findViewById<View>(R.id.dialogCloseButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        editText.requestFocus()
        editText.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 150)
    }
}
