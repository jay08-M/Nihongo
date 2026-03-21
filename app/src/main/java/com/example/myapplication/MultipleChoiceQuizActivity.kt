package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MultipleChoiceQuizActivity : AppCompatActivity() {

    private lateinit var deck: Deck
    private var currentQuestionIndex = 0
    private var score = 0
    private lateinit var shuffledCards: List<Flashcard>
    private val userAnswers = mutableListOf<UserAnswer>()

    private lateinit var tvQuestionCount: TextView
    private lateinit var tvQuestionText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnOptions: List<Button>
    private lateinit var btnFinishQuiz: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_choice_quiz)

        deck = intent.getSerializableExtra("SELECTED_DECK") as Deck
        shuffledCards = deck.cards.shuffled()

        tvQuestionCount = findViewById(R.id.tvQuestionCount)
        tvQuestionText = findViewById(R.id.tvQuestionText)
        progressBar = findViewById(R.id.quizProgressBar)
        btnFinishQuiz = findViewById(R.id.btnFinishQuiz)
        
        btnOptions = listOf(
            findViewById(R.id.btnOption1),
            findViewById(R.id.btnOption2),
            findViewById(R.id.btnOption3),
            findViewById(R.id.btnOption4)
        )

        progressBar.max = shuffledCards.size

        btnFinishQuiz.setOnClickListener {
            navigateToResults()
        }

        updateQuestion()
    }

    private fun updateQuestion() {
        if (currentQuestionIndex >= shuffledCards.size) {
            // All questions answered, show finish button and hide options
            findViewById<View>(R.id.optionsContainer).visibility = View.GONE
            btnFinishQuiz.visibility = View.VISIBLE
            return
        }

        val currentCard = shuffledCards[currentQuestionIndex]
        tvQuestionCount.text = "Question ${currentQuestionIndex + 1}/${shuffledCards.size}"
        tvQuestionText.text = currentCard.front
        progressBar.progress = currentQuestionIndex + 1

        val options = mutableListOf<String>()
        options.add(currentCard.back)
        
        val otherBacks = deck.cards.filter { it != currentCard }.map { it.back }.distinct().shuffled()
        options.addAll(otherBacks.take(3))
        
        // If there are not enough unique options, add fillers
        while (options.size < 4) {
            options.add("Option ${options.size + 1}")
        }
        
        options.shuffle()

        for (i in 0 until 4) {
            btnOptions[i].text = options[i]
            btnOptions[i].isEnabled = true
            btnOptions[i].setOnClickListener {
                checkAnswer(options[i], currentCard.back)
            }
        }
    }

    private fun checkAnswer(selected: String, correct: String) {
        val isCorrect = selected == correct
        if (isCorrect) score++
        
        userAnswers.add(UserAnswer(
            question = shuffledCards[currentQuestionIndex].front,
            selectedAnswer = selected,
            correctAnswer = correct,
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
        intent.putExtra("SELECTED_DECK", deck) // Pass deck back for retry
        startActivity(intent)
        finish()
    }
}
