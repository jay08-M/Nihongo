package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CreateFlashcardActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_flashcard)

        dbHelper = DatabaseHelper.getInstance(this)
        userId = intent.getIntExtra("USER_ID", -1)

        // Safety check for userId
        if (userId == -1) {
            Toast.makeText(this, "Error: User session lost. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val deckNameInput = findViewById<EditText>(R.id.deckNameInput)
        val flashcardListContainer = findViewById<LinearLayout>(R.id.flashcardListContainer)
        val btnAddCard = findViewById<Button>(R.id.btnAddCard)
        val btnCreateFinal = findViewById<Button>(R.id.btnCreateFinal)
        val btnCreatePractice = findViewById<Button>(R.id.btnCreatePractice)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        
        bottomNavigation.selectedItemId = R.id.nav_decks

        fun setupRemoveButton(cardView: View) {
            val btnRemove = cardView.findViewById<View>(R.id.btnRemoveCard)
            btnRemove.setOnClickListener {
                AlertDialog.Builder(this)
                    .setMessage("Remove this character?")
                    .setPositiveButton("Yes") { _, _ ->
                        flashcardListContainer.removeView(cardView)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        fun addNewCardItem() {
            val inflater = LayoutInflater.from(this)
            val newCardView = inflater.inflate(R.layout.item_flashcard_input, flashcardListContainer, false)
            setupRemoveButton(newCardView)
            flashcardListContainer.addView(newCardView)
        }

        if (flashcardListContainer.childCount > 0) {
            setupRemoveButton(flashcardListContainer.getChildAt(0))
        }

        btnAddCard.setOnClickListener {
            addNewCardItem()
        }

        fun saveDeckToDatabase(): Deck? {
            val deckName = deckNameInput.text.toString().trim()
            if (deckName.isEmpty()) {
                deckNameInput.error = "Deck name is required"
                return null
            }

            val flashcardInputs = mutableListOf<Pair<String, String>>()
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
                flashcardInputs.add(Pair(frontText, backText))
            }

            if (flashcardInputs.isEmpty()) {
                Toast.makeText(this, "Add at least one card", Toast.LENGTH_SHORT).show()
                return null
            }

            val db = dbHelper.writableDatabase
            var newDeck: Deck? = null

            db.beginTransaction()
            try {
                val deckValues = android.content.ContentValues().apply {
                    put(DatabaseHelper.COL_USER_ID_FK, userId)
                    put(DatabaseHelper.COL_DECK_NAME, deckName)
                }
                val deckId = db.insert(DatabaseHelper.TABLE_DECKS, null, deckValues)

                if (deckId == -1L) {
                    throw Exception("Database rejected insertion (Check User ID: $userId)")
                }

                val flashcards = mutableListOf<Flashcard>()
                for (input in flashcardInputs) {
                    val cardValues = android.content.ContentValues().apply {
                        put(DatabaseHelper.COL_DECK_ID_FK, deckId.toInt())
                        put(DatabaseHelper.COL_FRONT, input.first)
                        put(DatabaseHelper.COL_BACK, input.second)
                    }
                    val cardId = db.insert(DatabaseHelper.TABLE_FLASHCARDS, null, cardValues)
                    if (cardId == -1L) throw Exception("Failed to insert flashcard")

                    flashcards.add(Flashcard(id = cardId.toInt(), deckId = deckId.toInt(), front = input.first, back = input.second))
                }

                db.setTransactionSuccessful()
                newDeck = Deck(id = deckId.toInt(), userId = userId, name = deckName, cards = flashcards.toMutableList())
                DeckManager.savedDecks.add(newDeck)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to create deck: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                db.endTransaction()
            }

            return newDeck
        }

        btnCreateFinal.setOnClickListener {
            val newDeck = saveDeckToDatabase()
            if (newDeck != null) {
                val intent = Intent(this, DeckListActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
            }
        }

        btnCreatePractice.setOnClickListener {
            val newDeck = saveDeckToDatabase()
            if (newDeck != null) {
                val intent = Intent(this, DeckListActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
            }
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            val targetActivity = when (item.itemId) {
                R.id.nav_home -> SecondActivity::class.java
                R.id.nav_lessons -> LessonListActivity::class.java
                R.id.nav_quiz -> QuizModeActivity::class.java
                R.id.nav_alphabet -> BasicAlphabetActivity::class.java
                R.id.nav_decks -> DeckListActivity::class.java
                else -> null
            }
            if (targetActivity != null) {
                val intent = Intent(this, targetActivity)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
                true
            } else false
        }
    }
}
