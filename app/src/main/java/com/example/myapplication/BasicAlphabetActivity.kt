package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BasicAlphabetActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_alphabet)

        userId = intent.getIntExtra("USER_ID", -1)

        // Setup Start Buttons
        findViewById<Button>(R.id.btnOpenHiragana).setOnClickListener {
            val intent = Intent(this, HiraganaActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnOpenKatakana).setOnClickListener {
            val intent = Intent(this, KatakanaActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnOpenKanji).setOnClickListener {
            val intent = Intent(this, KanjiActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        // Setup Bottom Navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_alphabet

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_lessons -> {
                    val intent = Intent(this, LessonListActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_quiz -> {
                    val intent = Intent(this, QuizModeActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_alphabet -> true // Already here
                R.id.nav_decks -> {
                    val intent = Intent(this, DeckListActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
