package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class DeckListActivity : AppCompatActivity() {
    
    private lateinit var adapter: DeckListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)

        val rvDeckList = findViewById<RecyclerView>(R.id.rvDeckList)
        val btnAddDeck = findViewById<Button>(R.id.btnAddDeck)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.selectedItemId = R.id.nav_decks

        adapter = DeckListAdapter(
            DeckManager.savedDecks,
            onViewClick = { deck -> 
                val intent = Intent(this, ViewDeckActivity::class.java)
                intent.putExtra("DECK_DATA", deck)
                startActivity(intent)
            },
            onDeleteClick = { deck -> confirmDelete(deck) }
        )

        rvDeckList.layoutManager = LinearLayoutManager(this)
        rvDeckList.adapter = adapter

        btnAddDeck.setOnClickListener {
            startActivity(Intent(this, CreateFlashcardActivity::class.java))
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

    private fun confirmDelete(deck: Deck) {
        AlertDialog.Builder(this)
            .setTitle("Delete Deck")
            .setMessage("Are you sure you want to delete '${deck.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                DeckManager.savedDecks.remove(deck)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Deck deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
