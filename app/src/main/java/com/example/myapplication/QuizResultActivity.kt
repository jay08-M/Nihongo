package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuizResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        val result = intent.getSerializableExtra("QUIZ_RESULT") as QuizResult
        val deck = intent.getSerializableExtra("SELECTED_DECK") as Deck
        val quizType = intent.getStringExtra("QUIZ_TYPE") ?: "MULTIPLE_CHOICE"

        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvStats = findViewById<TextView>(R.id.tvStats)
        val rvReview = findViewById<RecyclerView>(R.id.rvReview)
        val btnRetry = findViewById<Button>(R.id.btnRetry)
        val btnBackToDecks = findViewById<Button>(R.id.btnBackToDecks)

        tvScore.text = "Score: ${result.correctAnswers}/${result.totalQuestions}"
        tvStats.text = "Correct: ${result.correctAnswers} | Wrong: ${result.wrongAnswers}"

        rvReview.layoutManager = LinearLayoutManager(this)
        rvReview.adapter = QuizReviewAdapter(result.answersList)

        btnRetry.setOnClickListener {
            val intent = if (quizType == "MULTIPLE_CHOICE") {
                Intent(this, MultipleChoiceQuizActivity::class.java)
            } else {
                Intent(this, IdentificationQuizActivity::class.java)
            }
            intent.putExtra("SELECTED_DECK", deck)
            startActivity(intent)
            finish()
        }

        btnBackToDecks.setOnClickListener {
            val intent = Intent(this, QuizSelectionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    class QuizReviewAdapter(private val answers: List<UserAnswer>) :
        RecyclerView.Adapter<QuizReviewAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvQuestion: TextView = view.findViewById(R.id.tvQuestion)
            val tvUserAnswer: TextView = view.findViewById(R.id.tvUserAnswer)
            val tvCorrectAnswer: TextView = view.findViewById(R.id.tvCorrectAnswer)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quiz_review, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val answer = answers[position]
            holder.tvQuestion.text = answer.question
            holder.tvUserAnswer.text = "Your Answer: ${answer.selectedAnswer}"
            holder.tvCorrectAnswer.text = "Correct Answer: ${answer.correctAnswer}"

            if (answer.isCorrect) {
                holder.tvUserAnswer.setTextColor(Color.parseColor("#4CAF50")) // Green
            } else {
                holder.tvUserAnswer.setTextColor(Color.parseColor("#F44336")) // Red
            }
        }

        override fun getItemCount() = answers.size
    }
}
