package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditDeckActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var deck: Deck
    private lateinit var etDeckName: EditText
    private lateinit var cardContainer: LinearLayout
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_deck)

        dbHelper = DatabaseHelper.getInstance(this)
        userId = intent.getIntExtra("USER_ID", -1)
        val deckId = intent.getIntExtra("DECK_ID", -1)

        // Find the deck in memory
        val foundDeck = DeckManager.savedDecks.find { it.id == deckId }
        if (foundDeck == null) {
            Toast.makeText(this, "Deck not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        deck = foundDeck

        etDeckName = findViewById(R.id.etEditDeckName)
        cardContainer = findViewById(R.id.cardContainerEdit)
        
        etDeckName.setText(deck.name)

        // Load existing cards
        deck.cards.forEach { card ->
            addCardInputRow(card.front, card.back)
        }

        findViewById<Button>(R.id.btnAddCardEdit).setOnClickListener {
            addCardInputRow("", "")
        }

        findViewById<Button>(R.id.btnSaveDeckEdit).setOnClickListener {
            saveChanges()
        }
    }

    private fun addCardInputRow(front: String, back: String) {
        val inflater = LayoutInflater.from(this)
        val row = inflater.inflate(R.layout.item_flashcard_input, cardContainer, false)
        
        val etFront = row.findViewById<EditText>(R.id.cardFrontInput)
        val etBack = row.findViewById<EditText>(R.id.cardBackInput)
        val btnRemove = row.findViewById<View>(R.id.btnRemoveCard)

        etFront.setText(front)
        etBack.setText(back)

        btnRemove.setOnClickListener {
             AlertDialog.Builder(this)
                .setMessage("Remove this card?")
                .setPositiveButton("Yes") { _, _ ->
                    cardContainer.removeView(row)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        cardContainer.addView(row)
    }

    private fun saveChanges() {
        val newName = etDeckName.text.toString().trim()
        if (newName.isEmpty()) {
            etDeckName.error = "Name required"
            return
        }

        val db = dbHelper.writableDatabase
        
        db.beginTransaction()
        try {
            // 1. Update deck name
            val deckValues = android.content.ContentValues().apply {
                put(DatabaseHelper.COL_DECK_NAME, newName)
            }
            db.update(DatabaseHelper.TABLE_DECKS, deckValues, "${DatabaseHelper.COL_ID} = ?", arrayOf(deck.id.toString()))

            // 2. Clear old cards
            db.delete(DatabaseHelper.TABLE_FLASHCARDS, "${DatabaseHelper.COL_DECK_ID_FK} = ?", arrayOf(deck.id.toString()))

            val newCards = mutableListOf<Flashcard>()
            // 3. Add current cards from UI
            for (i in 0 until cardContainer.childCount) {
                val row = cardContainer.getChildAt(i)
                val front = row.findViewById<EditText>(R.id.cardFrontInput).text.toString().trim()
                val back = row.findViewById<EditText>(R.id.cardBackInput).text.toString().trim()

                if (front.isNotEmpty() && back.isNotEmpty()) {
                    val values = android.content.ContentValues().apply {
                        put(DatabaseHelper.COL_DECK_ID_FK, deck.id)
                        put(DatabaseHelper.COL_FRONT, front)
                        put(DatabaseHelper.COL_BACK, back)
                    }
                    val cardId = db.insert(DatabaseHelper.TABLE_FLASHCARDS, null, values)
                    if (cardId != -1L) {
                        newCards.add(Flashcard(cardId.toInt(), deck.id, front, back))
                    }
                }
            }
            
            db.setTransactionSuccessful()
            
            // Update in-memory data
            deck.name = newName
            deck.cards.clear()
            deck.cards.addAll(newCards)
            
            Toast.makeText(this, "Deck updated successfully!", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to update deck", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }
    }
}
