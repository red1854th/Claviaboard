package com.example.claviatura

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import com.example.claviatura.languages.ko_kr.HangulAutomaton

class KeyboardService : InputMethodService() {
    private lateinit var containerView: KeyboardContainerView
    private lateinit var keyboardView: KeyboardView
    private lateinit var prefs: KeyboardPreferences
    private val automaton = HangulAutomaton()

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }
    private val audioManager: AudioManager by lazy { getSystemService(AUDIO_SERVICE) as AudioManager }
    private var isShiftActive: Boolean = false

    override fun onCreate() {
        super.onCreate()
        prefs = KeyboardPreferences(this)
    }

    override fun onCreateInputView(): View {
        keyboardView = KeyboardView(this, prefs)
        keyboardView.onKeyPressed = { key -> handleKey(key) }
        keyboardView.onActionRequested = { action -> handleAction(action) }
        keyboardView.setShiftState(isShiftActive)
        containerView = KeyboardContainerView(this, prefs)
        containerView.setKeyboardContent(keyboardView)
        return containerView
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        automaton.reset()
        isShiftActive = false
        if (::keyboardView.isInitialized) {
            keyboardView.setShiftState(false)
            keyboardView.buildLayout()
        }
    }

    override fun onComputeInsets(outInsets: InputMethodService.Insets) {
        super.onComputeInsets(outInsets)
        if (!::containerView.isInitialized) return
        val visibleHeight = if (::keyboardView.isInitialized && keyboardView.height > 0) {
            keyboardView.height + dpToPx(prefs.bottomMargin)
        } else {
            dpToPx(prefs.keyboardHeight) + dpToPx(prefs.bottomMargin)
        }
        val topInset = maxOf(0, containerView.height - visibleHeight)
        outInsets.contentTopInsets = topInset
        outInsets.visibleTopInsets = topInset
        outInsets.touchableInsets = InputMethodService.Insets.TOUCHABLE_INSETS_CONTENT
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    private fun handleAction(action: String) {
        val connection: InputConnection = currentInputConnection ?: return
        when (action) {
            "EN/KO" -> {
                commitComposingText(connection)
            }
            "SHIFT" -> {
                isShiftActive = !isShiftActive
            }
            "SETTINGS" -> {
                commitComposingText(connection)
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
            "HIDE" -> {
                commitComposingText(connection)
                requestHideSelf(0)
            }
            "CURSOR_LEFT" -> {
                commitComposingText(connection)
                connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT))
                connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT))
            }
            "CURSOR_RIGHT" -> {
                commitComposingText(connection)
                connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT))
                connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT))
            }
            "HANJA" -> {
                handleHanjaConversion(connection)
            }
        }
    }

    private fun handleHanjaConversion(connection: InputConnection) {
        // Simple and useful common Hanja dictionary mapping
        val hanjaMap = mapOf(
            '한' to listOf("韓", "漢", "限", "恨"),
            '국' to listOf("國", "局", "菊"),
            '문' to listOf("文", "門", "問", "聞"),
            '자' to listOf("字", "子", "自", "者"),
            '일' to listOf("日", "一", "事"),
            '이' to listOf("二", "以", "異", "利"),
            '삼' to listOf("三", "森"),
            '사' to listOf("四", "事", "社", "死"),
            '오' to listOf("五", "午", "誤"),
            '육' to listOf("六", "肉"),
            '칠' to listOf("七", "漆"),
            '팔' to listOf("八", "八"),
            '구' to listOf("九", "求", "具"),
            '십' to listOf("十"),
            '백' to listOf("百", "白"),
            '천' to listOf("千", "天", "川"),
            '만' to listOf("萬", "滿"),
            '년' to listOf("年"),
            '월' to listOf("月"),
            '일' to listOf("日"),
            '시' to listOf("時", "市", "視", "詩"),
            '분' to listOf("分"),
            '초' to listOf("秒", "初", "草"),
            '대' to listOf("大", "對", "臺"),
            '소' to listOf("小", "所", "消"),
            '중' to listOf("中", "重", "衆"),
            '상' to listOf("上", "常", "相"),
            '하' to listOf("下", "夏", "河"),
            '동' to listOf("東", "同", "動"),
            '서' to listOf("西", "書", "序"),
            '남' to listOf("南", "男"),
            '북' to listOf("北"),
            '수' to listOf("水", "手", "數", "秀"),
            '화' to listOf("火", "花", "話", "華"),
            '목' to listOf("木", "目"),
            '금' to listOf("金", "今", "禁"),
            '토' to listOf("土"),
            '인' to listOf("人", "仁", "因"),
            '심' to listOf("心", "深", "審"),
            '신' to listOf("身", "神", "新", "信"),
            '생' to listOf("生"),
            '명' to listOf("命", "名", "明")
        )

        val composing = automaton.getComposedChar()
        if (composing.isNotEmpty()) {
            val lastChar = composing.last()
            val candidates = hanjaMap[lastChar]
            if (candidates != null && candidates.isNotEmpty()) {
                automaton.reset()
                connection.commitText(candidates[0], 1)
                return
            }
        }

        // Try extracting previous character from text
        val textBefore = connection.getTextBeforeCursor(1, 0)
        if (!textBefore.isNullOrEmpty()) {
            val char = textBefore[0]
            val candidates = hanjaMap[char]
            if (candidates != null && candidates.isNotEmpty()) {
                connection.deleteSurroundingText(1, 0)
                connection.commitText(candidates[0], 1)
            }
        }
    }

    private fun handleKey(key: String) {
        performHapticFeedback()
        performSoundFeedback()
        val connection: InputConnection = currentInputConnection ?: return

        when (key) {
            "BACKSPACE" -> {
                if (automaton.delete()) {
                    val composing = automaton.getComposedChar()
                    if (composing.isNotEmpty()) {
                        connection.setComposingText(composing, 1)
                    } else {
                        connection.setComposingText("", 0)
                        connection.finishComposingText()
                    }
                } else {
                    connection.deleteSurroundingText(1, 0)
                }
            }
            "ENTER" -> {
                commitComposingText(connection)
                handleEnter(connection)
            }
            "SPACE" -> {
                commitComposingText(connection)
                connection.commitText(" ", 1)
                if (isShiftActive) toggleShift()
            }
            "SHIFT" -> toggleShift()
            "EN/KO" -> {
                commitComposingText(connection)
                keyboardView.buildLayout()
            }
            "SETTINGS" -> handleAction("SETTINGS")
            "HIDE" -> handleAction("HIDE")
            else -> {
                if (key.length == 1 && isHangul(key[0])) {
                    val committed = automaton.append(key[0])
                    if (committed.isNotEmpty()) {
                        connection.commitText(committed, 1)
                    }
                    val composing = automaton.getComposedChar()
                    if (composing.isNotEmpty()) {
                        connection.setComposingText(composing, 1)
                    }
                } else {
                    commitComposingText(connection)
                    val text = if (isShiftActive && key.length == 1 && key[0].isLetter()) key.uppercase() else key
                    connection.commitText(text, 1)
                }
                if (isShiftActive && key.length == 1) {
                    toggleShift()
                }
            }
        }
    }

    private fun commitComposingText(connection: InputConnection) {
        val composing = automaton.getComposedChar()
        if (composing.isNotEmpty()) {
            connection.commitText(composing, 1)
            automaton.reset()
        }
    }

    private fun isHangul(c: Char): Boolean = automaton.isCho(c) || automaton.isJung(c)

    private fun toggleShift() {
        isShiftActive = !isShiftActive
        if (::keyboardView.isInitialized) {
            keyboardView.setShiftState(isShiftActive)
        }
    }

    private fun handleEnter(connection: InputConnection) {
        val editorInfo = currentInputEditorInfo ?: return
        val action = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
        if (action != EditorInfo.IME_ACTION_NONE) {
            connection.performEditorAction(action)
        } else {
            connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }

    private fun performHapticFeedback() {
        if (!prefs.vibrateEnabled) return
        try {
            val duration = (prefs.vibrateStrength.toLong()).coerceIn(5L, 100L)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val amplitude = (prefs.vibrateStrength * 2.55f).toInt().coerceIn(1, 255)
                vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        } catch (_: Exception) { }
    }

    private fun performSoundFeedback() {
        if (!prefs.soundEnabled) return
        try {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.5f)
        } catch (_: Exception) { }
    }
}
