package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class QuizSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_selection)

        findViewById<Button>(R.id.btnMultipleChoice).setOnClickListener {
            val intent = Intent(this, QuizDeckSelectionActivity::class.java)
            intent.putExtra("QUIZ_TYPE", "MULTIPLE_CHOICE")
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnIdentification).setOnClickListener {
            val intent = Intent(this, QuizDeckSelectionActivity::class.java)
            intent.putExtra("QUIZ_TYPE", "IDENTIFICATION")
            startActivity(intent)
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_quiz

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, SecondActivity::class.java)); finish(); true }
                R.id.nav_lessons -> { startActivity(Intent(this, LessonListActivity::class.java)); finish(); true }
                R.id.nav_quiz -> { startActivity(Intent(this, QuizModeActivity::class.java)); finish(); true }
                R.id.nav_alphabet -> { startActivity(Intent(this, BasicAlphabetActivity::class.java)); finish(); true }
                R.id.nav_decks -> { startActivity(Intent(this, CreateFlashcardActivity::class.java)); finish(); true }
                else -> false
            }
        }
    }
}
