package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlashcardAdapter(private val flashcards: List<Flashcard>) :
    RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {

    class FlashcardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFront: TextView = view.findViewById(R.id.tvFrontText)
        val tvBack: TextView = view.findViewById(R.id.tvBackText)
        val frontLayout: LinearLayout = view.findViewById(R.id.frontSideLayout)
        val backLayout: LinearLayout = view.findViewById(R.id.backSideLayout)
        val btnFront: Button = view.findViewById(R.id.btnShowFront)
        val btnBack: Button = view.findViewById(R.id.btnShowBack)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flashcard_view, parent, false)
        return FlashcardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        val card = flashcards[position]
        holder.tvFront.text = card.front
        holder.tvBack.text = card.back

        // Ensure initial state
        holder.frontLayout.visibility = View.VISIBLE
        holder.backLayout.visibility = View.GONE

        holder.btnBack.setOnClickListener {
            flipCard(holder.frontLayout, holder.backLayout)
        }

        holder.btnFront.setOnClickListener {
            flipCard(holder.backLayout, holder.frontLayout)
        }
    }

    private fun flipCard(visibleView: View, hiddenView: View) {
        if (visibleView.visibility == View.GONE) return

        visibleView.animate()
            .rotationY(90f)
            .setDuration(200)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                visibleView.visibility = View.GONE
                hiddenView.visibility = View.VISIBLE
                hiddenView.rotationY = -90f
                hiddenView.animate()
                    .rotationY(0f)
                    .setDuration(200)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }.start()
    }

    override fun getItemCount() = flashcards.size
}
