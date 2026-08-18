package com.example.claviatura

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LanguageSettingsActivity : AppCompatActivity() {

    private lateinit var prefs: KeyboardPreferences
    private lateinit var koreanLayoutName: TextView
    private lateinit var englishLayoutName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_settings)

        prefs = KeyboardPreferences(this)

        koreanLayoutName = findViewById(R.id.koreanLayoutName)
        englishLayoutName = findViewById(R.id.englishLayoutName)

        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.testKeyboardButton).setOnClickListener {
            showKeyboardTestDialog()
        }

        findViewById<View>(R.id.koreanCard).setOnClickListener {
            val intent = Intent(this, KeyboardLayoutSelectActivity::class.java).apply {
                putExtra(KeyboardLayoutSelectActivity.EXTRA_MODE, "korean")
            }
            startActivity(intent)
        }

        findViewById<View>(R.id.englishCard).setOnClickListener {
            val intent = Intent(this, KeyboardLayoutSelectActivity::class.java).apply {
                putExtra(KeyboardLayoutSelectActivity.EXTRA_MODE, "english")
            }
            startActivity(intent)
        }

        findViewById<View>(R.id.addLanguageCard).setOnClickListener {
            showAddLanguageDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        koreanLayoutName.text = "LAYOUT: ${prefs.koreanLayoutType}"
        englishLayoutName.text = "LAYOUT: ${prefs.englishLayoutType}"
    }

    private fun showAddLanguageDialog() {
        val languages = arrayOf("Japanese (Kana/Romaji)", "Chinese (Pinyin)", "Spanish (Español)", "French (Français)", "German (Deutsch)")
        AlertDialog.Builder(this)
            .setTitle("[ ADD LANGUAGE ENGINE ]")
            .setItems(languages) { _, which ->
                Toast.makeText(this, "[+] Added ${languages[which]} language engine.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("[ CANCEL ]", null)
            .show()
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
