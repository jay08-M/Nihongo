package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class IdentificationQuizActivity : AppCompatActivity() {

    private lateinit var deck: Deck
    private var currentQuestionIndex = 0
    private var score = 0
    private lateinit var shuffledCards: List<Flashcard>
    private val userAnswers = mutableListOf<UserAnswer>()

    private lateinit var tvQuestionCount: TextView
    private lateinit var tvQuestionText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var etAnswer: TextInputEditText
    private lateinit var btnSubmit: Button
    private lateinit var btnFinishQuiz: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identification_quiz)

        deck = intent.getSerializableExtra("SELECTED_DECK") as Deck
        shuffledCards = deck.cards.shuffled()

        tvQuestionCount = findViewById(R.id.tvQuestionCount)
        tvQuestionText = findViewById(R.id.tvQuestionText)
        progressBar = findViewById(R.id.quizProgressBar)
        etAnswer = findViewById(R.id.etAnswer)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnFinishQuiz = findViewById(R.id.btnFinishQuiz)

        progressBar.max = shuffledCards.size

        btnSubmit.setOnClickListener {
            val userAnswerText = etAnswer.text.toString().trim()
            if (userAnswerText.isNotEmpty()) {
                checkAnswer(userAnswerText)
            }
        }

        btnFinishQuiz.setOnClickListener {
            navigateToResults()
        }

        updateQuestion()
    }

    private fun updateQuestion() {
        if (currentQuestionIndex >= shuffledCards.size) {
            findViewById<View>(R.id.tilAnswer).visibility = View.GONE
            btnSubmit.visibility = View.GONE
            btnFinishQuiz.visibility = View.VISIBLE
            return
        }

        val currentCard = shuffledCards[currentQuestionIndex]
        tvQuestionCount.text = "Question ${currentQuestionIndex + 1}/${shuffledCards.size}"
        tvQuestionText.text = currentCard.front
        progressBar.progress = currentQuestionIndex + 1
        etAnswer.setText("")
    }

    private fun checkAnswer(userAnswerText: String) {
        val correctAnswer = shuffledCards[currentQuestionIndex].back
        
        // Case-insensitive comparison with trimmed spaces
        val isCorrect = userAnswerText.equals(correctAnswer, ignoreCase = true)
        
        if (isCorrect) score++
        
        userAnswers.add(UserAnswer(
            question = shuffledCards[currentQuestionIndex].front,
            selectedAnswer = userAnswerText,
            correctAnswer = correctAnswer,
            isCorrect = isCorrect
        ))

        currentQuestionIndex++
        updateQuestion()
    }

    private fun navigateToResults() {
        val result = QuizResult(
            deckName = deck.name,
            totalQuestions = shuffledCards.size,
            correctAnswers = score,
            wrongAnswers = shuffledCards.size - score,
            answersList = userAnswers
        )
        val intent = Intent(this, QuizResultActivity::class.java)
        intent.putExtra("QUIZ_RESULT", result)
        intent.putExtra("SELECTED_DECK", deck)
        intent.putExtra("QUIZ_TYPE", "IDENTIFICATION")
        startActivity(intent)
        finish()
    }
}
