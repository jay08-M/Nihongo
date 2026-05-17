package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HiraganaActivity : AppCompatActivity() {
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hiragana)

        userId = intent.getIntExtra("USER_ID", -1)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_alphabet

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
