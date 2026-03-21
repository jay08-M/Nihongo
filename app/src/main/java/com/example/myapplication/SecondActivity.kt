package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // --- Navigation Intents ---
        val createIntent = Intent(this, CreateFlashcardActivity::class.java)
        val deckListIntent = Intent(this, DeckListActivity::class.java)
        val alphabetIntent = Intent(this, BasicAlphabetActivity::class.java)
        val lessonIntent = Intent(this, LessonListActivity::class.java)
        val quizIntent = Intent(this, QuizModeActivity::class.java)

        // --- Button Click Listeners (Tools Section) ---
        
        // Flashcard Section
        findViewById<Button>(R.id.btnCreateFlashcard).setOnClickListener { startActivity(createIntent) }
        findViewById<Button>(R.id.openButton1).setOnClickListener { Toast.makeText(this, "Opening Deck...", Toast.LENGTH_SHORT).show() }
        findViewById<Button>(R.id.openButton2).setOnClickListener { Toast.makeText(this, "Opening Deck...", Toast.LENGTH_SHORT).show() }

        // "Show All" TextView in Flashcard Section
        findViewById<TextView>(R.id.showAllDecks).setOnClickListener { 
            startActivity(deckListIntent) 
        }

        // Alphabet Button
        findViewById<Button>(R.id.btnBasicAlphabet).setOnClickListener { 
            startActivity(alphabetIntent) 
        }

        // Lesson Section
        findViewById<Button>(R.id.btnLessonList).setOnClickListener { startActivity(lessonIntent) }
        
        // Quiz Section
        findViewById<Button>(R.id.btnQuizMode).setOnClickListener { startActivity(quizIntent) }
        findViewById<Button>(R.id.viewButton1).setOnClickListener { Toast.makeText(this, "Viewing Result...", Toast.LENGTH_SHORT).show() }
        findViewById<Button>(R.id.viewButton2).setOnClickListener { Toast.makeText(this, "Viewing Result...", Toast.LENGTH_SHORT).show() }
    }
}