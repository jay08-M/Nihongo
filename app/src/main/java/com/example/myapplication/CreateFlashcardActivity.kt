package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CreateFlashcardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_flashcard)

        val deckNameInput = findViewById<EditText>(R.id.deckNameInput)
        val flashcardListContainer = findViewById<LinearLayout>(R.id.flashcardListContainer)
        val btnAddCard = findViewById<Button>(R.id.btnAddCard)
        val btnCreateFinal = findViewById<Button>(R.id.btnCreateFinal)
        val btnCreatePractice = findViewById<Button>(R.id.btnCreatePractice)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        
        bottomNavigation.selectedItemId = R.id.nav_decks

        fun addNewCardItem() {
            val inflater = LayoutInflater.from(this)
            val newCardView = inflater.inflate(R.layout.item_flashcard_input, flashcardListContainer, false)
            flashcardListContainer.addView(newCardView)
        }

        btnAddCard.setOnClickListener {
            addNewCardItem()
        }

        fun validateAndGetDeck(): Deck? {
            val deckName = deckNameInput.text.toString().trim()
            if (deckName.isEmpty()) {
                deckNameInput.error = "Deck name is required"
                return null
            }

            val flashcards = mutableListOf<Flashcard>()
            for (i in 0 until flashcardListContainer.childCount) {
                val cardView = flashcardListContainer.getChildAt(i)
                val frontInput = cardView.findViewById<EditText>(R.id.cardFrontInput)
                val backInput = cardView.findViewById<EditText>(R.id.cardBackInput)

                val frontText = frontInput.text.toString().trim()
                val backText = backInput.text.toString().trim()

                if (frontText.isEmpty()) {
                    frontInput.error = "Front side required"
                    return null
                }
                if (backText.isEmpty()) {
                    backInput.error = "Back side required"
                    return null
                }
                flashcards.add(Flashcard(frontText, backText))
            }

            if (flashcards.isEmpty()) {
                Toast.makeText(this, "Add at least one card", Toast.LENGTH_SHORT).show()
                return null
            }

            return Deck(deckName, flashcards)
        }

        btnCreateFinal.setOnClickListener {
            val newDeck = validateAndGetDeck()
            if (newDeck != null) {
                DeckManager.savedDecks.add(newDeck)
                Toast.makeText(this, "Deck '${newDeck.name}' saved!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DeckListActivity::class.java))
                finish()
            }
        }

        btnCreatePractice.setOnClickListener {
            val newDeck = validateAndGetDeck()
            if (newDeck != null) {
                // For now, just save and go to list. Could implement practice mode later.
                DeckManager.savedDecks.add(newDeck)
                startActivity(Intent(this, DeckListActivity::class.java))
                finish()
            }
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, SecondActivity::class.java)); finish(); true }
                R.id.nav_lessons -> { startActivity(Intent(this, LessonListActivity::class.java)); finish(); true }
                R.id.nav_quiz -> { startActivity(Intent(this, QuizModeActivity::class.java)); finish(); true }
                R.id.nav_alphabet -> { startActivity(Intent(this, BasicAlphabetActivity::class.java)); finish(); true }
                R.id.nav_decks -> true
                else -> false
            }
        }
    }
}
