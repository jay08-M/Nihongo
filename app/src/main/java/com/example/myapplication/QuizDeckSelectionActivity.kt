package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class QuizDeckSelectionActivity : AppCompatActivity() {
    
    private lateinit var adapter: DeckListAdapter
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_deck_selection)

        dbHelper = DatabaseHelper.getInstance(this)
        userId = intent.getIntExtra("USER_ID", -1)
        val quizType = intent.getStringExtra("QUIZ_TYPE") ?: "MULTIPLE_CHOICE"
        
        val rvDeckList = findViewById<RecyclerView>(R.id.rvQuizDeckList)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.selectedItemId = R.id.nav_quiz

        if (DeckManager.savedDecks.isEmpty()) {
            Toast.makeText(this, "No decks available. Please create one first!", Toast.LENGTH_LONG).show()
        }

        adapter = DeckListAdapter(
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
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                }
            },
            onEditClick = { deck ->
                val intent = Intent(this, EditDeckActivity::class.java)
                intent.putExtra("DECK_ID", deck.id)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            },
            onDeleteClick = { deck -> confirmDelete(deck) }
        )

        rvDeckList.layoutManager = LinearLayoutManager(this)
        rvDeckList.adapter = adapter

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { 
                    val intent = Intent(this, SecondActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true 
                }
                R.id.nav_lessons -> { 
                    val intent = Intent(this, LessonListActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true 
                }
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
                    val intent = Intent(this, CreateFlashcardActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true 
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    private fun confirmDelete(deck: Deck) {
        AlertDialog.Builder(this)
            .setTitle("Delete Deck")
            .setMessage("Are you sure you want to delete '${deck.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                val success = dbHelper.deleteDeck(deck.id)
                if (success) {
                    DeckManager.savedDecks.remove(deck)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Deck deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
