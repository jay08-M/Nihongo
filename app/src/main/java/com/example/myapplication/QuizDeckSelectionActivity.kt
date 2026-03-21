package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class QuizDeckSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_deck_selection)

        val quizType = intent.getStringExtra("QUIZ_TYPE") ?: "MULTIPLE_CHOICE"
        val rvDeckList = findViewById<RecyclerView>(R.id.rvQuizDeckList)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.selectedItemId = R.id.nav_quiz

        if (DeckManager.savedDecks.isEmpty()) {
            Toast.makeText(this, "No decks available. Please create one first!", Toast.LENGTH_LONG).show()
        }

        rvDeckList.layoutManager = LinearLayoutManager(this)
        rvDeckList.adapter = DeckListAdapter(
            DeckManager.savedDecks,
            onViewClick = { deck ->
                if (quizType == "MULTIPLE_CHOICE" && deck.cards.size < 4) {
                    Toast.makeText(this, "Deck needs at least 4 cards for a multiple choice quiz.", Toast.LENGTH_SHORT).show()
                } else if (deck.cards.isEmpty()) {
                    Toast.makeText(this, "Deck is empty.", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = if (quizType == "MULTIPLE_CHOICE") {
                        Intent(this, MultipleChoiceQuizActivity::class.java)
                    } else {
                        Intent(this, IdentificationQuizActivity::class.java)
                    }
                    intent.putExtra("SELECTED_DECK", deck)
                    startActivity(intent)
                }
            },
            onDeleteClick = { /* No delete here */ }
        )

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
