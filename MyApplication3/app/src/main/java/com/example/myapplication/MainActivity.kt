package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var inputText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputText = findViewById(R.id.inputText)
        val btnOpenSecond = findViewById<Button>(R.id.btnOpenSecond)
        val btnCall = findViewById<Button>(R.id.btnCall)
        val btnShare = findViewById<Button>(R.id.btnShare)

        // 1. Явный Intent
        btnOpenSecond.setOnClickListener {
            val text = inputText.text.toString().trim()

            if (text.isEmpty()) {
                inputText.error = "Введите текст!"
                return@setOnClickListener
            }

            val intent = Intent(this, SecondActivity::class.java).apply {
                putExtra(SecondActivity.EXTRA_MESSAGE, text)
            }
            startActivity(intent)
        }

        // 2. Неявный Intent (Открыть приложение для звонка)
        btnCall.setOnClickListener {
            val phone = inputText.text.toString().trim()

            if (phone.isEmpty()) {
                inputText.error = "Введите номер телефона!"
                return@setOnClickListener
            }

            if (!phone.matches(Regex("^[+]?[0-9]{3,15}$"))) {
                inputText.error = "Некорректный формат номера"
                return@setOnClickListener
            }

            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }

            if (dialIntent.resolveActivity(packageManager) != null) {
                startActivity(dialIntent)
            } else {
                Toast.makeText(this, "Не найдено приложение для звонков", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Системный Intent (Поделиться)
        btnShare.setOnClickListener {
            val textToShare = inputText.text.toString().trim()

            if (textToShare.isEmpty()) {
                inputText.error = "Введите текст для отправки!"
                return@setOnClickListener
            }

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, textToShare)
            }

            val shareIntent = Intent.createChooser(sendIntent, "Поделиться через...")

            if (sendIntent.resolveActivity(packageManager) != null) {
                startActivity(shareIntent)
            } else {
                Toast.makeText(this, "Нет приложений для отправки текста", Toast.LENGTH_SHORT).show()
            }
        }
    }
}