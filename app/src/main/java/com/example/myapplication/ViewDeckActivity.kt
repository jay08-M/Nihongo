package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ViewDeckActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_deck)

        val deck = intent.getSerializableExtra("DECK_DATA") as? Deck
        if (deck == null) {
            finish()
            return
        }

        findViewById<TextView>(R.id.tvViewDeckTitle).text = deck.name

        val rvFlashcards = findViewById<RecyclerView>(R.id.rvFlashcardList)
        rvFlashcards.layoutManager = LinearLayoutManager(this)
        rvFlashcards.adapter = FlashcardAdapter(deck.cards)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_decks
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, SecondActivity::class.java)); finish(); true }
                R.id.nav_lessons -> { startActivity(Intent(this, LessonListActivity::class.java)); finish(); true }
                R.id.nav_quiz -> { startActivity(Intent(this, QuizModeActivity::class.java)); finish(); true }
                R.id.nav_alphabet -> { startActivity(Intent(this, BasicAlphabetActivity::class.java)); finish(); true }
                R.id.nav_decks -> {
                    startActivity(Intent(this, DeckListActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
