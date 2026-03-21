package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeckListAdapter(
    private var decks: List<Deck>,
    private val onViewClick: (Deck) -> Unit,
    private val onDeleteClick: (Deck) -> Unit
) : RecyclerView.Adapter<DeckListAdapter.DeckViewHolder>() {

    class DeckViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDeckName: TextView = view.findViewById(R.id.tvDeckName)
        val tvCardCount: TextView = view.findViewById(R.id.tvCardCount)
        val btnViewDeck: Button = view.findViewById(R.id.btnViewDeck)
        val btnDeleteDeck: ImageButton = view.findViewById(R.id.btnDeleteDeck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_deck, parent, false)
        return DeckViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = decks[position]
        holder.tvDeckName.text = deck.name
        holder.tvCardCount.text = "${deck.cards.size} cards"
        holder.btnViewDeck.setOnClickListener { onViewClick(deck) }
        holder.btnDeleteDeck.setOnClickListener { onDeleteClick(deck) }
    }

    override fun getItemCount() = decks.size

    fun updateData(newDecks: List<Deck>) {
        this.decks = newDecks
        notifyDataSetChanged()
    }
}
