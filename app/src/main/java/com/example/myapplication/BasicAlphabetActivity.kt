package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BasicAlphabetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_alphabet)

        // Setup Start Buttons
        findViewById<Button>(R.id.btnOpenHiragana).setOnClickListener {
            startActivity(Intent(this, HiraganaActivity::class.java))
        }

        findViewById<Button>(R.id.btnOpenKatakana).setOnClickListener {
            startActivity(Intent(this, KatakanaActivity::class.java))
        }

        findViewById<Button>(R.id.btnOpenKanji).setOnClickListener {
            startActivity(Intent(this, KanjiActivity::class.java))
        }

        // Setup Bottom Navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_alphabet

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, SecondActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_lessons -> {
                    startActivity(Intent(this, LessonListActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_quiz -> {
                    startActivity(Intent(this, QuizModeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_alphabet -> true // Already here
                R.id.nav_decks -> {
                    startActivity(Intent(this, CreateFlashcardActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
