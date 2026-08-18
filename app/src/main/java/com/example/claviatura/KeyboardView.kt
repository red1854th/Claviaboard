package com.example.claviatura

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

@SuppressLint("ViewConstructor")
class KeyboardView(
    context: Context,
    private val prefs: KeyboardPreferences
) : FrameLayout(context) {

    enum class ShiftMode {
        OFF,        // Outline arrow, lowercase
        ON,         // Filled arrow with base key color, uppercase once
        CAPS_LOCK   // Filled arrow with bottom underline bar (Caps Lock), uppercase locked
    }

    var onKeyPressed: ((String) -> Unit)? = null
    var onActionRequested: ((String) -> Unit)? = null

    private var shiftMode = ShiftMode.OFF
    private var isSymbolMode = false
    private var currentLanguage = prefs.selectedLanguage

    private val cachedKeyEntries = mutableListOf<KeyViewEntry>()
    private val locationOnScreen = IntArray(2)
    private val handler = Handler(Looper.getMainLooper())

    // Container for all keyboard rows
    private val rowsContainer: LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    // In-view Key preview overlay (100% reliable inside IME window)
    private val previewCardView: FrameLayout = FrameLayout(context)
    private val previewTextView: TextView = TextView(context)

    // Active touch tracking per pointerId
    private val activePointers = mutableMapOf<Int, PointerState>()

    // Continuous delete
    private var isDeleting = false
    private val deleteRunnable = object : Runnable {
        override fun run() {
            if (isDeleting) {
                onKeyPressed?.invoke("BACKSPACE")
                handler.postDelayed(this, prefs.deleteRepeatInterval)
            }
        }
    }

    init {
        clipChildren = false
        clipToPadding = false
        isClickable = true
        isFocusable = true

        val theme = prefs.currentTheme
        setBackgroundColor(theme.keyboardBackground)

        addView(rowsContainer)
        initPreviewOverlay()
        buildLayout()
    }

    private fun initPreviewOverlay() {
        val popupSize = dpToPx(48)

        previewTextView.apply {
            textSize = 20f
            typeface = if (prefs.useSystemFont) Typeface.DEFAULT_BOLD else Typeface.MONOSPACE
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

        previewCardView.apply {
            visibility = View.GONE
            elevation = 0f
            layoutParams = LayoutParams(popupSize, popupSize)
            setPadding(0, 0, 0, 0)
            addView(previewTextView)
        }

        addView(previewCardView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = dpToPx(prefs.keyboardHeight)
        val newHeightSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        updateKeyBoundsCache()
    }

    private fun updateKeyBoundsCache() {
        cachedKeyEntries.clear()
        getLocationOnScreen(locationOnScreen)
        collectKeyViews(rowsContainer)
    }

    private fun collectKeyViews(group: ViewGroup) {
        val childLoc = IntArray(2)
        for (i in 0 until group.childCount) {
            val child = group.getChildAt(i)
            if (child.tag is KeyTagData) {
                val tagData = child.tag as KeyTagData
                child.getLocationOnScreen(childLoc)
                val rect = Rect(childLoc[0], childLoc[1], childLoc[0] + child.width, childLoc[1] + child.height)
                cachedKeyEntries.add(KeyViewEntry(child, tagData.key, tagData.holdChar, tagData.isControl, rect))
            } else if (child is ViewGroup) {
                collectKeyViews(child)
            }
        }
    }

    fun buildLayout() {
        val theme = prefs.currentTheme
        setBackgroundColor(theme.keyboardBackground)

        currentLanguage = prefs.selectedLanguage
        rowsContainer.removeAllViews()

        val hMargin = dpToPx(prefs.horizontalMargin)
        rowsContainer.setPadding(dpToPx(3) + hMargin, dpToPx(3), dpToPx(3) + hMargin, dpToPx(3))

        // 1. Dedicated Number row (if enabled and not in symbol mode)
        if (prefs.showNumberRow && !isSymbolMode) {
            buildNumberRow()
        }

        // 2. Character rows
        when {
            isSymbolMode -> buildSymbolLayout()
            currentLanguage == "ko_KR" -> buildKoreanLayout()
            else -> buildEnglishLayout()
        }

        post { updateKeyBoundsCache() }
    }

    /**
     * Top dedicated number row: 1 2 3 4 5 6 7 8 9 0
     * Sized with square key proportions
     */
    private fun buildNumberRow() {
        val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        val holdSymbols = listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")")
        val row = createRowContainer().apply {
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.95f)
        }
        for (i in numbers.indices) {
            addKeyToRow(row, numbers[i], holdSymbols[i], 1f, isNumberRowKey = true)
        }
        rowsContainer.addView(row)
    }

    /**
     * Korean Layout:
     * - Supports Dubeolshik, Danmoeum, Danmoeum+, Cheonjiin, Cheonjiin+, Naratgeul, Vega.
     * - Hold characters are unified with English.
     */
    private fun buildKoreanLayout() {
        val isShifted = shiftMode != ShiftMode.OFF
        val layoutType = prefs.koreanLayoutType

        when {
            layoutType.contains("Danmoeum", ignoreCase = true) || layoutType.contains("단모음") -> {
                val isPlus = layoutType.contains("+")
                val row1 = if (isPlus) {
                    if (isShifted) listOf("ㅃ", "ㅉ", "ㄸ", "ㄲ", "ㅆ", "ㅛ", "ㅕ", "ㅑ", "ㅒ", "ㅖ")
                    else listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ")
                } else {
                    listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅗ", "ㅐ", "ㅔ")
                }

                val row2 = if (isPlus) {
                    listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ")
                } else {
                    listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅓ", "ㅏ", "ㅣ")
                }

                val row3 = if (isPlus) {
                    listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ")
                } else {
                    listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅜ", "ㅡ")
                }

                val row1Hold = if (prefs.showNumberRow) {
                    List(row1.size) { i -> listOf("%", "₩", "=", "&", "'", "*", "-", "+", "<", ">").getOrElse(i) { "" } }
                } else {
                    List(row1.size) { i -> "${i + 1}" }
                }
                val row2Hold = List(row2.size) { i -> listOf("@", "#", ":", ";", "^", "~", "/", "(", ")").getOrElse(i) { "" } }
                val row3Hold = List(row3.size) { i -> listOf("`", "\"", "'", "$", ",", "!", "?").getOrElse(i) { "" } }

                val r1 = createRowContainer()
                for (i in row1.indices) addKeyToRow(r1, row1[i], row1Hold[i], 1f)
                rowsContainer.addView(r1)

                val r2 = createRowContainer()
                r2.addView(View(context).apply { layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f) })
                for (i in row2.indices) addKeyToRow(r2, row2[i], row2Hold[i], 1f)
                r2.addView(View(context).apply { layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f) })
                rowsContainer.addView(r2)

                val r3 = createRowContainer()
                addKeyToRow(r3, "SHIFT", null, 1.4f, isControl = true)
                for (i in row3.indices) addKeyToRow(r3, row3[i], row3Hold[i], 1f)
                addKeyToRow(r3, "BACKSPACE", null, 1.4f, isControl = true)
                rowsContainer.addView(r3)

                buildBottomRow()
            }
            layoutType.contains("Cheonjiin", ignoreCase = true) || layoutType.contains("천지인") -> {
                val isPlus = layoutType.contains("+")
                val r1Keys = listOf("ㅣ", "·", "ㅡ")
                val r2Keys = listOf("ㄱㅋ", "ㄴㄹ", "ㄷㅌ")
                val r3Keys = listOf("ㅂㅍ", "ㅅㅎ", "ㅈㅊ")

                val r1 = createRowContainer()
                for (k in r1Keys) addKeyToRow(r1, k, null, 1f)
                rowsContainer.addView(r1)

                val r2 = createRowContainer()
                for (k in r2Keys) addKeyToRow(r2, k, null, 1f)
                rowsContainer.addView(r2)

                val r3 = createRowContainer()
                addKeyToRow(r3, "SHIFT", null, 1.2f, isControl = true)
                for (k in r3Keys) addKeyToRow(r3, k, null, 1f)
                addKeyToRow(r3, "BACKSPACE", null, 1.2f, isControl = true)
                rowsContainer.addView(r3)

                val r4 = createRowContainer()
                addKeyToRow(r4, "!?", null, 1f, isControl = true)
                addKeyToRow(r4, "ㅇㅁ", null, 1f)
                addKeyToRow(r4, "SPACE", null, 2f, isControl = true)
                addKeyToRow(r4, "ENTER", null, 1.2f, isControl = true)
                rowsContainer.addView(r4)
            }
            layoutType.contains("Naratgeul", ignoreCase = true) || layoutType.contains("나랏글") -> {
                val r1Keys = listOf("ㄱ", "ㄴ", "ㅏ")
                val r2Keys = listOf("ㄹ", "ㅁ", "ㅓ")
                val r3Keys = listOf("ㅅ", "ㅇ", "ㅣ")

                val r1 = createRowContainer()
                for (k in r1Keys) addKeyToRow(r1, k, null, 1f)
                addKeyToRow(r1, "BACKSPACE", null, 1f, isControl = true)
                rowsContainer.addView(r1)

                val r2 = createRowContainer()
                for (k in r2Keys) addKeyToRow(r2, k, null, 1f)
                addKeyToRow(r2, "ENTER", null, 1f, isControl = true)
                rowsContainer.addView(r2)

                val r3 = createRowContainer()
                for (k in r3Keys) addKeyToRow(r3, k, null, 1f)
                addKeyToRow(r3, "+획", null, 1f, isControl = true)
                rowsContainer.addView(r3)

                val r4 = createRowContainer()
                addKeyToRow(r4, "SPACE", null, 1.2f, isControl = true)
                addKeyToRow(r4, "ㅡ", null, 1f)
                addKeyToRow(r4, "·", null, 1f)
                addKeyToRow(r4, "쌍자음", null, 1f, isControl = true)
                rowsContainer.addView(r4)
            }
            else -> {
                // Standard Dubeolshik
                val row1 = if (isShifted) listOf("ㅃ", "ㅉ", "ㄸ", "ㄲ", "ㅆ", "ㅛ", "ㅕ", "ㅑ", "ㅒ", "ㅖ")
                else listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ")

                val row1Hold = if (prefs.showNumberRow) {
                    listOf("%", "₩", "=", "&", "'", "*", "-", "+", "<", ">")
                } else {
                    listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
                }

                val row2 = listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ")
                val row2Hold = if (prefs.showNumberRow) {
                    listOf("@", "#", ":", ";", "^", "~", "/", "(", ")")
                } else {
                    listOf("@", "#", "$", "%", "&", "-", "+", "(", ")")
                }

                val row3 = listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ")
                val row3Hold = if (prefs.showNumberRow) {
                    listOf("`", "\"", "'", "$", ",", "!", "?")
                } else {
                    listOf("*", "\"", "'", ":", ";", "!", "?")
                }

                val r1 = createRowContainer()
                for (i in row1.indices) addKeyToRow(r1, row1[i], row1Hold[i], 1f)
                rowsContainer.addView(r1)

                val r2 = createRowContainer()
                r2.addView(View(context).apply { layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f) })
                for (i in row2.indices) addKeyToRow(r2, row2[i], row2Hold[i], 1f)
                r2.addView(View(context).apply { layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f) })
                rowsContainer.addView(r2)

                val r3 = createRowContainer()
                addKeyToRow(r3, "SHIFT", null, 1.4f, isControl = true)
                for (i in row3.indices) addKeyToRow(r3, row3[i], row3Hold[i], 1f)
                addKeyToRow(r3, "BACKSPACE", null, 1.4f, isControl = true)
                rowsContainer.addView(r3)

                buildBottomRow()
            }
        }
    }

    /**
     * English Layout:
     * - Supports QWERTY, Dvorak, Colemak, Workman, QWERTZ, AZERTY.
     * - Shift states: OFF (lowercase), ON (uppercase once), CAPS_LOCK (uppercase locked)
     */
    private fun buildEnglishLayout() {
        val isUpperCase = shiftMode != ShiftMode.OFF
        val layoutType = prefs.englishLayoutType

        val (rawR1, rawR2, rawR3) = when {
            layoutType.contains("Dvorak", ignoreCase = true) || layoutType.contains("드보락") -> Triple(
                listOf("P", "Y", "F", "G", "C", "R", "L", "?", "/", "="),
                listOf("A", "O", "E", "U", "I", "D", "H", "T", "N", "S"),
                listOf("Q", "J", "K", "X", "B", "M", "W", "V", "Z")
            )
            layoutType.contains("Colemak", ignoreCase = true) || layoutType.contains("콜맥") -> Triple(
                listOf("Q", "W", "F", "P", "G", "J", "L", "U", "Y", ";"),
                listOf("A", "R", "S", "T", "D", "H", "N", "E", "I", "O"),
                listOf("Z", "X", "C", "V", "B", "K", "M")
            )
            layoutType.contains("Workman", ignoreCase = true) -> Triple(
                listOf("Q", "D", "R", "W", "B", "J", "F", "U", "P", ";"),
                listOf("A", "S", "H", "T", "G", "Y", "N", "E", "O", "I"),
                listOf("Z", "X", "M", "C", "V", "K", "L")
            )
            layoutType.contains("QWERTZ", ignoreCase = true) -> Triple(
                listOf("Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P"),
                listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
                listOf("Y", "X", "C", "V", "B", "N", "M")
            )
            layoutType.contains("AZERTY", ignoreCase = true) -> Triple(
                listOf("A", "Z", "E", "R", "T", "Y", "U", "I", "O", "P"),
                listOf("Q", "S", "D", "F", "G", "H", "J", "K", "L", "M"),
                listOf("W", "X", "C", "V", "B", "N")
            )
            else -> Triple(
                listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
                listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
                listOf("Z", "X", "C", "V", "B", "N", "M")
            )
        }

        val row1 = if (isUpperCase) rawR1 else rawR1.map { it.lowercase() }
        val row2 = if (isUpperCase) rawR2 else rawR2.map { it.lowercase() }
        val row3 = if (isUpperCase) rawR3 else rawR3.map { it.lowercase() }

        // Unified hold characters across Korean & English
        val row1Hold = if (prefs.showNumberRow) {
            listOf("%", "₩", "=", "&", "'", "*", "-", "+", "<", ">")
        } else {
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        }

        val row2Hold = if (prefs.showNumberRow) {
            listOf("@", "#", ":", ";", "^", "~", "/", "(", ")")
        } else {
            listOf("@", "#", "$", "%", "&", "-", "+", "(", ")")
        }

        val row3Hold = if (prefs.showNumberRow) {
            listOf("`", "\"", "'", "$", ",", "!", "?")
        } else {
            listOf("*", "\"", "'", ":", ";", "!", "?")
        }

        // Row 1
        val r1 = createRowContainer()
        for (i in row1.indices) addKeyToRow(r1, row1[i], row1Hold[i], 1f)
        rowsContainer.addView(r1)

        // Row 2
        val r2 = createRowContainer()
        r2.addView(View(context).apply { layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f) })
        for (i in row2.indices) addKeyToRow(r2, row2[i], row2Hold[i], 1f)
        r2.addView(View(context).apply { layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f) })
        rowsContainer.addView(r2)

        // Row 3
        val r3 = createRowContainer()
        addKeyToRow(r3, "SHIFT", null, 1.4f, isControl = true)
        for (i in row3.indices) addKeyToRow(r3, row3[i], row3Hold[i], 1f)
        addKeyToRow(r3, "BACKSPACE", null, 1.4f, isControl = true)
        rowsContainer.addView(r3)

        buildBottomRow()
    }

    /**
     * Symbol mode layout triggered by @ / +=♥ key
     */
    private fun buildSymbolLayout() {
        val row1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        val row2 = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/")
        val row3 = listOf("*", "\"", "'", ":", ";", "!", "?", "~", "\\", "_")

        val r1 = createRowContainer()
        for (key in row1) addKeyToRow(r1, key, null, 1f)
        rowsContainer.addView(r1)

        val r2 = createRowContainer()
        for (key in row2) addKeyToRow(r2, key, null, 1f)
        rowsContainer.addView(r2)

        val r3 = createRowContainer()
        addKeyToRow(r3, "SYM_LOCK", null, 1.2f, isControl = true)
        for (key in row3) addKeyToRow(r3, key, null, 1f)
        addKeyToRow(r3, "BACKSPACE", null, 1.2f, isControl = true)
        rowsContainer.addView(r3)

        buildBottomRow()
    }

    /**
     * Bottom Control Row:
     * [ @ / 한글 | 🌐A | 漢 | SPACE (···) | . | ↵ ]
     */
    private fun buildBottomRow() {
        val row = createRowContainer()

        // 1. Symbol mode toggle key (@ vector icon in text mode, "한글"/"ABC" in symbol mode)
        if (prefs.showSpecialKey || isSymbolMode) {
            val symKeyTag = if (isSymbolMode) (if (currentLanguage == "ko_KR") "한글" else "ABC") else "@"
            addKeyToRow(row, symKeyTag, null, 1.2f, isControl = true)
        }

        // 2. Language switch key (🌐A with ··· hint)
        addKeyToRow(row, "🌐A", "···", 1.15f, isControl = true)

        // 3. Hanja key (漢 with ··· hint)
        if (prefs.showVoiceKey && currentLanguage == "ko_KR" && !isSymbolMode) {
            addKeyToRow(row, "漢", "···", 1.0f, isControl = true)
        }

        // 4. Space bar (clean key)
        val spaceWeight = when {
            isSymbolMode -> 4.5f
            currentLanguage == "ko_KR" -> if (prefs.showVoiceKey) 3.6f else 4.4f
            else -> 4.5f
        }
        addKeyToRow(row, "SPACE", null, spaceWeight, isControl = false)

        // 5. Period key (.) with ' hint
        if (prefs.showPeriodKey) {
            addKeyToRow(row, ".", "'", 1.1f, isControl = false)
        }

        // 6. Enter key (↵)
        addKeyToRow(row, "ENTER", null, 1.4f, isControl = true)

        rowsContainer.addView(row)
    }

    private fun createRowContainer() = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
    }

    private fun addKeyToRow(
        row: LinearLayout,
        key: String,
        holdChar: String?,
        weight: Float,
        isControl: Boolean = false,
        isNumberRowKey: Boolean = false
    ) {
        val theme = prefs.currentTheme
        val actualControl = isControl || key in listOf("BACKSPACE", "ENTER", "SHIFT", "🌐A", "@", "한글", "ABC", "SYM_LOCK", "漢")
        val isShiftActive = (key == "SHIFT" && shiftMode != ShiftMode.OFF)

        val keyFrame = FrameLayout(context).apply {
            tag = KeyTagData(key, holdChar, actualControl)
            layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, weight).apply {
                if (isNumberRowKey) {
                    setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))
                } else {
                    setMargins(dpToPx(2), dpToPx(3), dpToPx(2), dpToPx(3))
                }
            }
            background = createKeyBackground(key, actualControl, isPressed = false, isNumberRow = isNumberRowKey)
            elevation = 0f
        }

        val iconRes = when (key) {
            "BACKSPACE" -> R.drawable.ic_keyboard_backspace
            "ENTER" -> R.drawable.ic_keyboard_enter
            "SHIFT" -> when (shiftMode) {
                ShiftMode.OFF -> R.drawable.ic_keyboard_shift_outline
                ShiftMode.ON -> R.drawable.ic_keyboard_shift_filled
                ShiftMode.CAPS_LOCK -> R.drawable.ic_keyboard_caps_lock
            }
            "🌐A" -> R.drawable.ic_keyboard_language
            "@" -> R.drawable.ic_keyboard_at
            else -> null
        }

        if (iconRes != null) {
            val iconView = ImageView(context).apply {
                setImageResource(iconRes)
                scaleType = ImageView.ScaleType.FIT_CENTER
                val iconW = when (key) {
                    "@" -> dpToPx(24)
                    "BACKSPACE" -> dpToPx(26)
                    "ENTER" -> dpToPx(24)
                    "SHIFT" -> dpToPx(26)
                    "🌐A" -> dpToPx(24)
                    else -> dpToPx(22)
                }
                val iconH = when (key) {
                    "@" -> dpToPx(24)
                    "BACKSPACE" -> dpToPx(24)
                    "ENTER" -> dpToPx(24)
                    "SHIFT" -> dpToPx(26)
                    "🌐A" -> dpToPx(24)
                    else -> dpToPx(22)
                }
                layoutParams = FrameLayout.LayoutParams(iconW, iconH, Gravity.CENTER)

                val tintColor = if (isShiftActive) theme.keyTextColor else if (actualControl) theme.controlKeyTextColor else theme.keyTextColor
                setColorFilter(tintColor)
            }
            keyFrame.addView(iconView)
        } else {
            // Main text label
            val mainTextView = TextView(context).apply {
                text = when (key) {
                    "SPACE" -> ""
                    else -> key
                }
                textSize = when (key) {
                    "한글", "ABC", "SYM_LOCK" -> 13f
                    "漢" -> 14f
                    else -> if (isNumberRowKey) 16f else 19f
                }
                typeface = if (actualControl || isNumberRowKey) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                gravity = Gravity.CENTER
                setTextColor(
                    when {
                        key == "SPACE" -> theme.keySubTextColor
                        actualControl -> theme.controlKeyTextColor
                        isNumberRowKey -> theme.numberKeyTextColor
                        else -> theme.keyTextColor
                    }
                )
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
            }
            keyFrame.addView(mainTextView)
        }

        // Hold character hint on top-right (e.g. %, ₩, =, &, etc.)
        if (holdChar != null) {
            val hintTextView = TextView(context).apply {
                text = holdChar
                textSize = if (holdChar == "···") 8f else 10f
                setTextColor(theme.keySubTextColor)
                gravity = Gravity.END or Gravity.TOP
                setPadding(0, dpToPx(2), dpToPx(4), 0)
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
            }
            keyFrame.addView(hintTextView)
        }

        row.addView(keyFrame)
    }

    private fun createKeyBackground(
        key: String,
        isControl: Boolean,
        isPressed: Boolean,
        isNumberRow: Boolean = false
    ): GradientDrawable {
        val theme = prefs.currentTheme
        val isShiftActive = (key == "SHIFT" && shiftMode != ShiftMode.OFF)

        return GradientDrawable().apply {
            cornerRadius = dpToPx(prefs.keyCornerRadius).toFloat()
            val baseColor = when {
                // When shift is active (ON or CAPS_LOCK), use base key color (white/theme key color) as requested
                isShiftActive -> if (isPressed) theme.keyPressedBackground else theme.keyBackground
                isPressed -> if (isControl) theme.controlKeyPressedBackground else if (isNumberRow) theme.numberKeyPressedBackground else theme.keyPressedBackground
                isControl -> theme.controlKeyBackground
                isNumberRow -> theme.numberKeyBackground
                key == "SPACE" -> theme.keyBackground
                else -> theme.keyBackground
            }
            setColor(baseColor)
            // Stroke for normal keys and shift-active keys to give tactile depth
            if ((!isControl || isShiftActive) && !isNumberRow && !isPressed) {
                if (theme.keyStrokeColor != Color.TRANSPARENT) {
                    setStroke(dpToPx(1), theme.keyStrokeColor)
                }
            } else if (isControl && !isPressed && theme.controlKeyStrokeColor != Color.TRANSPARENT) {
                setStroke(dpToPx(1), theme.controlKeyStrokeColor)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (cachedKeyEntries.isEmpty()) {
            updateKeyBoundsCache()
        }
        getLocationOnScreen(locationOnScreen)

        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        val x = event.getX(pointerIndex).toInt() + locationOnScreen[0]
        val y = event.getY(pointerIndex).toInt() + locationOnScreen[1]

        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val entry = findNearestKey(x, y)
                if (entry != null) {
                    val state = PointerState(
                        entry = entry,
                        startX = x,
                        isHold = false,
                        downTime = System.currentTimeMillis()
                    )
                    activePointers[pointerId] = state
                    setKeyPressedState(entry.view, true, entry.key)

                    // Continuous delete for backspace
                    if (entry.key == "BACKSPACE") {
                        isDeleting = true
                        onKeyPressed?.invoke("BACKSPACE")
                        handler.postDelayed(deleteRunnable, prefs.deleteHoldDelay)
                    } else {
                        // Show key preview (Press state - card with normal char, disabled for spacebar and control keys)
                        if (prefs.showKeyPreview && !entry.isControlKey && entry.key != "SPACE") {
                            showKeyPreview(entry.view, entry.key, isHold = false)
                        }

                        // Long press hold timer (Hold state - theme hold card with hold symbol, disabled for spacebar)
                        val holdTimeout = prefs.longPressTimeout
                        val holdRunnable = Runnable {
                            if (activePointers[pointerId] == state) {
                                state.isHold = true
                                val holdChar = entry.holdChar
                                if (holdChar != null && holdChar != "···" && entry.key != "SPACE") {
                                    showKeyPreview(entry.view, holdChar, isHold = true)
                                }
                            }
                        }
                        state.holdRunnable = holdRunnable
                        handler.postDelayed(holdRunnable, holdTimeout)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                // Support spacebar slide cursor movement gesture
                for (i in 0 until event.pointerCount) {
                    val pid = event.getPointerId(i)
                    val state = activePointers[pid] ?: continue
                    if (state.entry.key == "SPACE") {
                        val currentX = event.getX(i).toInt() + locationOnScreen[0]
                        val diffX = currentX - state.startX
                        val stepPx = dpToPx(14)
                        if (diffX > stepPx) {
                            onActionRequested?.invoke("CURSOR_RIGHT")
                            state.startX = currentX
                        } else if (diffX < -stepPx) {
                            onActionRequested?.invoke("CURSOR_LEFT")
                            state.startX = currentX
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val state = activePointers.remove(pointerId)
                if (state != null) {
                    state.holdRunnable?.let { handler.removeCallbacks(it) }
                    setKeyPressedState(state.entry.view, false, state.entry.key)
                    dismissKeyPreview()

                    if (state.entry.key == "BACKSPACE") {
                        isDeleting = false
                        handler.removeCallbacks(deleteRunnable)
                    } else {
                        // Commit key on release
                        if (state.isHold && state.entry.holdChar != null && state.entry.holdChar != "···") {
                            onKeyPressed?.invoke(state.entry.holdChar)
                        } else {
                            handleKeyReleaseAction(state.entry.key)
                        }
                    }
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                isDeleting = false
                handler.removeCallbacks(deleteRunnable)
                activePointers.values.forEach { state ->
                    state.holdRunnable?.let { handler.removeCallbacks(it) }
                    setKeyPressedState(state.entry.view, false, state.entry.key)
                }
                activePointers.clear()
                dismissKeyPreview()
            }
        }
        return true
    }

    private fun handleKeyReleaseAction(key: String) {
        when (key) {
            "🌐A", "한/영", "EN/KO" -> {
                currentLanguage = if (currentLanguage == "ko_KR") "en_US" else "ko_KR"
                prefs.selectedLanguage = currentLanguage
                shiftMode = ShiftMode.OFF
                onActionRequested?.invoke("EN/KO")
                buildLayout()
            }
            "SHIFT" -> {
                shiftMode = when {
                    currentLanguage == "ko_KR" -> {
                        if (shiftMode == ShiftMode.OFF) ShiftMode.ON else ShiftMode.OFF
                    }
                    else -> {
                        // English: OFF -> ON (Single uppercase) -> CAPS_LOCK (Locked) -> OFF
                        when (shiftMode) {
                            ShiftMode.OFF -> ShiftMode.ON
                            ShiftMode.ON -> ShiftMode.CAPS_LOCK
                            ShiftMode.CAPS_LOCK -> ShiftMode.OFF
                        }
                    }
                }
                onActionRequested?.invoke("SHIFT")
                buildLayout()
            }
            "@", "+=♥" -> {
                isSymbolMode = true
                buildLayout()
            }
            "한글", "ABC" -> {
                isSymbolMode = false
                buildLayout()
            }
            "SYM_LOCK" -> {
                // Secondary symbol page toggle
                buildLayout()
            }
            "漢" -> {
                onActionRequested?.invoke("HANJA")
            }
            "CURSOR_LEFT" -> onActionRequested?.invoke("CURSOR_LEFT")
            "CURSOR_RIGHT" -> onActionRequested?.invoke("CURSOR_RIGHT")
            "SETTINGS" -> onActionRequested?.invoke("SETTINGS")
            "HIDE" -> onActionRequested?.invoke("HIDE")
            else -> {
                onKeyPressed?.invoke(key)
                // If single shift was ON (not caps lock), revert to OFF after typing a character
                if (shiftMode == ShiftMode.ON) {
                    shiftMode = ShiftMode.OFF
                    buildLayout()
                }
            }
        }
    }

    /**
     * Finds the nearest key even if pressed in gaps, margins, or near edges.
     */
    private fun findNearestKey(x: Int, y: Int): KeyViewEntry? {
        // 1. Exact match
        val exact = cachedKeyEntries.firstOrNull { it.rect.contains(x, y) }
        if (exact != null) return exact

        // 2. Nearest Euclidean distance to key boundary
        var closestEntry: KeyViewEntry? = null
        var minDistanceSq = Long.MAX_VALUE

        for (entry in cachedKeyEntries) {
            val dx = when {
                x < entry.rect.left -> entry.rect.left - x
                x > entry.rect.right -> x - entry.rect.right
                else -> 0
            }
            val dy = when {
                y < entry.rect.top -> entry.rect.top - y
                y > entry.rect.bottom -> y - entry.rect.bottom
                else -> 0
            }
            val distSq = (dx * dx + dy * dy).toLong()
            if (distSq < minDistanceSq) {
                minDistanceSq = distSq
                closestEntry = entry
            }
        }
        return closestEntry
    }

    private fun setKeyPressedState(view: View, isPressed: Boolean, key: String) {
        val tagData = view.tag as? KeyTagData ?: return
        view.background = createKeyBackground(key, tagData.isControl, isPressed)
    }

    /**
     * Key Preview Floating Overlay:
     * - Normal Press: Theme card with key text color
     * - Long Press / Hold: Theme hold card (e.g. #4C84F3) with white text
     */
    private fun showKeyPreview(anchorView: View, text: String, isHold: Boolean = false) {
        if (!prefs.showKeyPreview) return
        try {
            val theme = prefs.currentTheme
            previewTextView.apply {
                this.text = text
                setTextColor(if (isHold) theme.previewHoldTextColor else theme.previewTextColor)
            }

            previewCardView.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                if (isHold) {
                    setColor(theme.previewHoldBackground)
                } else {
                    setColor(theme.previewBackground)
                    if (theme.previewStrokeColor != Color.TRANSPARENT) {
                        setStroke(dpToPx(1), theme.previewStrokeColor)
                    }
                }
            }

            val keyLoc = IntArray(2)
            val kbLoc = IntArray(2)
            anchorView.getLocationOnScreen(keyLoc)
            this.getLocationOnScreen(kbLoc)

            val relX = (keyLoc[0] - kbLoc[0]).toFloat()
            val relY = (keyLoc[1] - kbLoc[1]).toFloat()

            val popupSize = dpToPx(48).toFloat()

            val posX = relX + (anchorView.width - popupSize) / 2f
            val posY = relY - popupSize - dpToPxF(8f)

            val minX = dpToPxF(2f)
            val maxX = (this.width.toFloat() - popupSize - dpToPxF(2f)).coerceAtLeast(0f)

            previewCardView.x = posX.coerceIn(minX, maxX)
            previewCardView.y = posY
            previewCardView.visibility = View.VISIBLE
            previewCardView.bringToFront()
        } catch (_: Exception) { }
    }

    private fun dismissKeyPreview() {
        try {
            previewCardView.visibility = View.GONE
        } catch (_: Exception) { }
    }

    fun setShiftState(active: Boolean) {
        val targetMode = if (active) ShiftMode.ON else ShiftMode.OFF
        if (shiftMode != targetMode) {
            shiftMode = targetMode
            buildLayout()
        }
    }

    fun applyTheme(themeId: String) {
        prefs.selectedThemeId = themeId
        buildLayout()
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
    private fun dpToPxF(dp: Float): Float = dp * resources.displayMetrics.density

    private data class KeyTagData(
        val key: String,
        val holdChar: String?,
        val isControl: Boolean
    )

    private data class KeyViewEntry(
        val view: View,
        val key: String,
        val holdChar: String?,
        val isControlKey: Boolean,
        val rect: Rect
    )

    private data class PointerState(
        val entry: KeyViewEntry,
        var startX: Int,
        var isHold: Boolean,
        val downTime: Long,
        var holdRunnable: Runnable? = null
    )
}
