package com.example.myapplication

import java.io.Serializable

data class Flashcard(
    val front: String,
    val back: String
) : Serializable

data class Deck(
    val name: String,
    val cards: List<Flashcard>
) : Serializable

data class UserAnswer(
    val question: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean
) : Serializable

data class QuizResult(
    val deckName: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val answersList: List<UserAnswer>
) : Serializable

object DeckManager {
    val savedDecks = mutableListOf<Deck>()
}
