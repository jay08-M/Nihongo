package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
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

        dbHelper = DatabaseHelper.getInstance(this)
        userId = intent.getIntExtra("USER_ID", -1)

        // Top Settings Button
        findViewById<ImageButton>(R.id.btnSettingsTop).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

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
        
        loadDataFromDb()
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDb()
    }

    private fun loadDataFromDb() {
        if (userId != -1) {
            loadDecksFromDb()
            loadQuizResultsFromDb()
            updateRecentDecks()
            updateRecentQuizzes()
        }
    }

    private fun loadDecksFromDb() {
        try {
            val decks = dbHelper.getDecksForUser(userId)
            DeckManager.savedDecks.clear()
            for (deck in decks) {
                val cards = dbHelper.getFlashcardsForDeck(deck.id)
                deck.cards.clear()
                deck.cards.addAll(cards)
                DeckManager.savedDecks.add(deck)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadQuizResultsFromDb() {
        try {
            val results = dbHelper.getQuizResultsForUser(userId)
            DeckManager.quizHistory.clear()
            DeckManager.quizHistory.addAll(results)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateRecentDecks() {
        // Take last 2 added decks for this user
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
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }
            
            // Second recent deck
            if (recentDecks.size > 1) {
                layout2.visibility = View.VISIBLE
                tvName2.text = recentDecks[1].name
                btnOpen2.setOnClickListener {
                    val intent = Intent(this, ViewDeckActivity::class.java)
                    intent.putExtra("DECK_DATA", recentDecks[1])
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                }
            } else {
                layout2.visibility = View.GONE
            }
        }
    }

    private fun updateRecentQuizzes() {
        // Fetch up to 4 most recent quizzes for the logged-in user
        val recentQuizzes = DeckManager.quizHistory.take(4) // Already sorted by date DESC in DB helper
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
                        intent.putExtra("USER_ID", userId)
                        // Note: If retrying from here, we might need the original Deck object
                        // but since history only stores results, we'd need to fetch deck from DB or
                        // pass it if available. For now, viewing results is prioritized.
                        startActivity(intent)
                    }
                } else {
                    layouts[i].visibility = View.GONE
                }
            }
        }
    }
}
