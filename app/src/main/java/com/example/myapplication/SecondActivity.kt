package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class SecondActivity : AppCompatActivity() {
    private var userId: Int = -1
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)

        // --- Navigation Intents ---
        
        // Flashcard Section
        findViewById<Button>(R.id.btnCreateFlashcard).setOnClickListener { 
            val intent = Intent(this, CreateFlashcardActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent) 
        }

        // "Show All" TextView in Flashcard Section
        findViewById<TextView>(R.id.showAllDecks).setOnClickListener { 
            val intent = Intent(this, DeckListActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent) 
        }

        // Alphabet Button
        findViewById<Button>(R.id.btnBasicAlphabet).setOnClickListener { 
            val intent = Intent(this, BasicAlphabetActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent) 
        }

        // Lesson Section
        findViewById<Button>(R.id.btnLessonList).setOnClickListener { 
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent) 
        }
        
        // Quiz Section
        findViewById<Button>(R.id.btnQuizMode).setOnClickListener { 
            val intent = Intent(this, QuizModeActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent) 
        }
        
        loadDecksFromDb()
        updateRecentDecks()
        updateRecentQuizzes()
    }

    override fun onResume() {
        super.onResume()
        loadDecksFromDb()
        updateRecentDecks()
        updateRecentQuizzes()
    }

    private fun loadDecksFromDb() {
        if (userId != -1) {
            val decks = dbHelper.getDecksForUser(userId)
            DeckManager.savedDecks.clear()
            for (deck in decks) {
                val cards = dbHelper.getFlashcardsForDeck(deck.id)
                deck.cards.clear()
                deck.cards.addAll(cards)
                DeckManager.savedDecks.add(deck)
            }
        }
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
                intent.putExtra("DECK_DATA", recentDecks[0])
                startActivity(intent)
            }
            
            // Second recent deck
            if (recentDecks.size > 1) {
                layout2.visibility = View.VISIBLE
                tvName2.text = recentDecks[1].name
                btnOpen2.setOnClickListener {
                    val intent = Intent(this, ViewDeckActivity::class.java)
                    intent.putExtra("DECK_DATA", recentDecks[1])
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