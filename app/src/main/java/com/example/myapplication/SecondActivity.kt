package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

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
        
        updateRecentDecks()
        updateRecentQuizzes()
    }

    override fun onResume() {
        super.onResume()
        updateRecentDecks()
        updateRecentQuizzes()
    }

    private fun updateRecentDecks() {
        val recentDecks = DeckManager.savedDecks.takeLast(2).reversed()
        
        val layout1 = findViewById<View>(R.id.layoutDeck1)
        val layout2 = findViewById<View>(R.id.layoutDeck2)
        val tvNoDecks = findViewById<TextView>(R.id.tvNoDecks)
        
        val tvName1 = findViewById<TextView>(R.id.tvDeckName1)
        val btnOpen1 = findViewById<Button>(R.id.openButton1)
        
        val tvName2 = findViewById<TextView>(R.id.tvDeckName2)
        val btnOpen2 = findViewById<Button>(R.id.openButton2)

        if (recentDecks.isEmpty()) {
            layout1.visibility = View.GONE
            layout2.visibility = View.GONE
            tvNoDecks.visibility = View.VISIBLE
        } else {
            tvNoDecks.visibility = View.GONE
            
            // First recent deck
            layout1.visibility = View.VISIBLE
            tvName1.text = recentDecks[0].name
            btnOpen1.setOnClickListener {
                val intent = Intent(this, ViewDeckActivity::class.java)
                intent.putExtra("SELECTED_DECK", recentDecks[0])
                startActivity(intent)
            }
            
            // Second recent deck
            if (recentDecks.size > 1) {
                layout2.visibility = View.VISIBLE
                tvName2.text = recentDecks[1].name
                btnOpen2.setOnClickListener {
                    val intent = Intent(this, ViewDeckActivity::class.java)
                    intent.putExtra("SELECTED_DECK", recentDecks[1])
                    startActivity(intent)
                }
            } else {
                layout2.visibility = View.GONE
            }
        }
    }

    private fun updateRecentQuizzes() {
        val recentQuizzes = DeckManager.quizHistory.takeLast(4).reversed()
        val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        
        val layouts = listOf(
            findViewById<View>(R.id.layoutQuiz1),
            findViewById<View>(R.id.layoutQuiz2),
            findViewById<View>(R.id.layoutQuiz3),
            findViewById<View>(R.id.layoutQuiz4)
        )
        
        val tvNames = listOf(
            findViewById<TextView>(R.id.tvQuizName1),
            findViewById<TextView>(R.id.tvQuizName2),
            findViewById<TextView>(R.id.tvQuizName3),
            findViewById<TextView>(R.id.tvQuizName4)
        )
        
        val tvDetails = listOf(
            findViewById<TextView>(R.id.tvQuizDetails1),
            findViewById<TextView>(R.id.tvQuizDetails2),
            findViewById<TextView>(R.id.tvQuizDetails3),
            findViewById<TextView>(R.id.tvQuizDetails4)
        )

        val tvNoQuizzes = findViewById<TextView>(R.id.tvNoQuizzes)

        if (recentQuizzes.isEmpty()) {
            layouts.forEach { it.visibility = View.GONE }
            tvNoQuizzes.visibility = View.VISIBLE
        } else {
            tvNoQuizzes.visibility = View.GONE
            
            for (i in layouts.indices) {
                if (i < recentQuizzes.size) {
                    val quiz = recentQuizzes[i]
                    layouts[i].visibility = View.VISIBLE
                    tvNames[i].text = quiz.deckName
                    tvDetails[i].text = "Score: ${quiz.correctAnswers}/${quiz.totalQuestions} | ${dateFormat.format(quiz.dateTaken)}"
                    
                    layouts[i].setOnClickListener {
                        val intent = Intent(this, QuizResultActivity::class.java)
                        intent.putExtra("QUIZ_RESULT", quiz)
                        startActivity(intent)
                    }
                } else {
                    layouts[i].visibility = View.GONE
                }
            }
        }
    }
}