package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class LessonListActivity : AppCompatActivity() {
    
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_list)

        userId = intent.getIntExtra("USER_ID", -1)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_lessons

        // Link buttons to their respective lesson layouts
        setupLessonButtons()

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_lessons -> true
                R.id.nav_quiz -> {
                    val intent = Intent(this, QuizModeActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_alphabet -> {
                    val intent = Intent(this, BasicAlphabetActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
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

    private fun setupLessonButtons() {
        val lessonButtons = mapOf(
            R.id.btnStartLesson1 to R.layout.fragment_lesson_hiragana_part_one,
            R.id.btnStartLesson2 to R.layout.fragment_lesson_hiragana_part_two,
            R.id.btnStartLesson3 to R.layout.fragment_lesson_hiragana_part_three,
            R.id.btnStartLesson4 to R.layout.fragment_lesson_special_characters,
            R.id.btnStartLesson5 to R.layout.fragment_lesson_katakana,
            R.id.btnStartLesson6 to R.layout.fragment_lesson_katakana_part_two,
            R.id.btnStartLesson7 to R.layout.fragment_lesson_pronunciation,
            R.id.btnStartLesson8 to R.layout.fragment_lesson_reading_basics
        )

        for ((buttonId, layoutId) in lessonButtons) {
            findViewById<Button>(buttonId).setOnClickListener {
                val intent = Intent(this, LessonDetailActivity::class.java)
                intent.putExtra("LAYOUT_ID", layoutId)
                startActivity(intent)
            }
        }
    }
}