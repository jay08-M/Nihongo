package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class QuizSelectionActivity : AppCompatActivity() {
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_selection)

        userId = intent.getIntExtra("USER_ID", -1)

        findViewById<Button>(R.id.btnMultipleChoice).setOnClickListener {
            val intent = Intent(this, QuizDeckSelectionActivity::class.java)
            intent.putExtra("QUIZ_TYPE", "MULTIPLE_CHOICE")
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnIdentification).setOnClickListener {
            val intent = Intent(this, QuizDeckSelectionActivity::class.java)
            intent.putExtra("QUIZ_TYPE", "IDENTIFICATION")
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_quiz

        bottomNavigation.setOnItemSelectedListener { item ->
            val targetActivity = when (item.itemId) {
                R.id.nav_home -> SecondActivity::class.java
                R.id.nav_lessons -> LessonListActivity::class.java
                R.id.nav_quiz -> QuizModeActivity::class.java
                R.id.nav_alphabet -> BasicAlphabetActivity::class.java
                R.id.nav_decks -> DeckListActivity::class.java
                R.id.nav_settings -> SettingsActivity::class.java
                else -> null
            }

            if (targetActivity != null) {
                val intent = Intent(this, targetActivity)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
                true
            } else {
                false
            }
        }
    }
}
