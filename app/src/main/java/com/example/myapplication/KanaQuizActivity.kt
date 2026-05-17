package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class KanaQuizActivity : AppCompatActivity() {

    private var userId: Int = -1
    private lateinit var mode: String // "HIRAGANA" or "KATAKANA"
    
    private var currentQuestionIndex = 0
    private var score = 0
    private var isIdentificationSection = true
    
    private lateinit var identificationQuestions: List<KanaQuestion>
    private lateinit var multipleChoiceQuestions: List<KanaQuestion>
    private lateinit var currentQuestions: List<KanaQuestion>
    
    private val userAnswers = mutableListOf<UserAnswer>()
    private lateinit var dbHelper: DatabaseHelper

    // UI Elements
    private lateinit var tvSectionLabel: TextView
    private lateinit var tvQuestionCount: TextView
    private lateinit var tvQuestionText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutIdentification: View
    private lateinit var layoutMultipleChoice: View
    private lateinit var etAnswer: TextInputEditText
    private lateinit var btnSubmitId: Button
    private lateinit var btnOptions: List<Button>
    private lateinit var layoutFeedback: View
    private lateinit var tvFeedbackStatus: TextView
    private lateinit var tvTranslation: TextView
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kana_quiz)

        dbHelper = DatabaseHelper.getInstance(this)
        userId = intent.getIntExtra("USER_ID", -1)
        mode = intent.getStringExtra("KANA_MODE") ?: "HIRAGANA"

        setupUI()
        prepareQuestions()
        updateQuestion()
    }

    private fun setupUI() {
        tvSectionLabel = findViewById(R.id.tvSectionLabel)
        tvQuestionCount = findViewById(R.id.tvQuestionCount)
        tvQuestionText = findViewById(R.id.tvQuestionText)
        progressBar = findViewById(R.id.quizProgressBar)
        layoutIdentification = findViewById(R.id.layoutIdentification)
        layoutMultipleChoice = findViewById(R.id.layoutMultipleChoice)
        etAnswer = findViewById(R.id.etAnswer)
        btnSubmitId = findViewById(R.id.btnSubmitId)
        layoutFeedback = findViewById(R.id.layoutFeedback)
        tvFeedbackStatus = findViewById(R.id.tvFeedbackStatus)
        tvTranslation = findViewById(R.id.tvTranslation)
        btnNext = findViewById(R.id.btnNext)

        btnOptions = listOf(
            findViewById(R.id.btnOption1),
            findViewById(R.id.btnOption2),
            findViewById(R.id.btnOption3),
            findViewById(R.id.btnOption4)
        )

        btnSubmitId.setOnClickListener {
            val answerText = etAnswer.text.toString().trim()
            if (answerText.isNotEmpty()) {
                checkIdentificationAnswer(answerText)
            }
        }

        btnNext.setOnClickListener {
            currentQuestionIndex++
            if (isIdentificationSection && currentQuestionIndex >= identificationQuestions.size) {
                startMultipleChoiceSection()
            } else if (!isIdentificationSection && currentQuestionIndex >= multipleChoiceQuestions.size) {
                navigateToResults()
            } else {
                updateQuestion()
            }
        }
    }

    private fun prepareQuestions() {
        identificationQuestions = (KanaQuizData.identificationHiragana + KanaQuizData.identificationKatakana).shuffled()
        multipleChoiceQuestions = if (mode == "HIRAGANA") {
            KanaQuizData.hiraganaCharacters.shuffled().take(10)
        } else {
            KanaQuizData.katakanaCharacters.shuffled().take(10)
        }
        
        currentQuestions = identificationQuestions
        progressBar.max = 20
    }

    private fun updateQuestion() {
        layoutFeedback.visibility = View.INVISIBLE
        btnNext.visibility = View.GONE
        etAnswer.setText("")
        
        val currentQuestion = currentQuestions[currentQuestionIndex]
        tvQuestionText.text = currentQuestion.prompt
        tvQuestionCount.text = "Item ${currentQuestionIndex + (if (isIdentificationSection) 1 else 11)}/20"
        progressBar.progress = if (isIdentificationSection) currentQuestionIndex + 1 else currentQuestionIndex + 11

        if (isIdentificationSection) {
            layoutIdentification.visibility = View.VISIBLE
            layoutMultipleChoice.visibility = View.GONE
            btnSubmitId.isEnabled = true
        } else {
            layoutIdentification.visibility = View.GONE
            layoutMultipleChoice.visibility = View.VISIBLE
            setupMultipleChoiceOptions(currentQuestion)
        }
    }

    private fun setupMultipleChoiceOptions(question: KanaQuestion) {
        val options = mutableListOf<String>()
        options.add(question.answer)
        
        val allPossible = if (mode == "HIRAGANA") KanaQuizData.hiraganaCharacters else KanaQuizData.katakanaCharacters
        val wrongOptions = allPossible.filter { it.answer != question.answer }.shuffled().take(3).map { it.answer }
        options.addAll(wrongOptions)
        options.shuffle()

        for (i in 0 until 4) {
            btnOptions[i].text = options[i]
            btnOptions[i].isEnabled = true
            btnOptions[i].setOnClickListener {
                checkMultipleChoiceAnswer(options[i])
            }
        }
    }

    private fun checkIdentificationAnswer(userAnswer: String) {
        val current = currentQuestions[currentQuestionIndex]
        val isCorrect = userAnswer.equals(current.answer, ignoreCase = true)
        showFeedback(isCorrect, current, userAnswer)
        btnSubmitId.isEnabled = false
    }

    private fun checkMultipleChoiceAnswer(selected: String) {
        val current = currentQuestions[currentQuestionIndex]
        val isCorrect = selected == current.answer
        showFeedback(isCorrect, current, selected)
        btnOptions.forEach { it.isEnabled = false }
    }

    private fun showFeedback(isCorrect: Boolean, question: KanaQuestion, userAnswer: String) {
        if (isCorrect) {
            score++
            tvFeedbackStatus.text = "Correct!"
            tvFeedbackStatus.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            tvFeedbackStatus.text = "Incorrect. It was: ${question.answer}"
            tvFeedbackStatus.setTextColor(Color.parseColor("#F44336"))
        }
        
        tvTranslation.text = "Meaning: ${question.translation}"
        layoutFeedback.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE
        
        userAnswers.add(UserAnswer(
            question = question.prompt,
            selectedAnswer = userAnswer,
            correctAnswer = question.answer,
            isCorrect = isCorrect
        ))
    }

    private fun startMultipleChoiceSection() {
        isIdentificationSection = false
        currentQuestionIndex = 0
        currentQuestions = multipleChoiceQuestions
        tvSectionLabel.text = "Section 2: Multiple Choice ($mode)"
        updateQuestion()
    }

    private fun navigateToResults() {
        val result = QuizResult(
            deckName = "$mode Proficiency Test",
            totalQuestions = 20,
            correctAnswers = score,
            wrongAnswers = 20 - score,
            answersList = userAnswers
        )
        
        if (userId != -1) {
            dbHelper.saveQuizResult(userId, result)
        }

        val intent = Intent(this, QuizResultActivity::class.java)
        intent.putExtra("QUIZ_RESULT", result)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("QUIZ_TYPE", "KANA_EXAM")
        intent.putExtra("KANA_MODE", mode)
        startActivity(intent)
        finish()
    }
}
