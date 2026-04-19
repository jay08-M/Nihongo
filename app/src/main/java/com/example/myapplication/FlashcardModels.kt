package com.example.myapplication

import java.io.Serializable
import java.util.Date

data class User(
    val id: Int,
    val username: String
) : Serializable

data class Flashcard(
    val id: Int = -1,
    val deckId: Int = -1,
    val front: String,
    val back: String
) : Serializable

data class Deck(
    val id: Int = -1,
    val userId: Int = -1,
    var name: String,
    val cards: MutableList<Flashcard> = mutableListOf()
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
    val answersList: List<UserAnswer>,
    val dateTaken: Date = Date()
) : Serializable

object DeckManager {
    val savedDecks = mutableListOf<Deck>()
    val quizHistory = mutableListOf<QuizResult>()
}
