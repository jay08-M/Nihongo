package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DeckListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)

        val btnAddDeck = findViewById<Button>(R.id.btnAddDeck)
        btnAddDeck.setOnClickListener {
            startActivity(Intent(this, CreateFlashcardActivity::class.java))
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_decks

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
                R.id.nav_alphabet -> {
                    startActivity(Intent(this, BasicAlphabetActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_decks -> true // Already here
                else -> false
            }
        }
    }
}