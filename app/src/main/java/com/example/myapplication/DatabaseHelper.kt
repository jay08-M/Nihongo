package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class DatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "NihonGo.db"
        private const val DATABASE_VERSION = 7 // Incremented version for new table

        @Volatile
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }

        const val TABLE_USERS = "users"
        const val TABLE_DECKS = "decks"
        const val TABLE_FLASHCARDS = "flashcards"
        const val TABLE_QUIZ_RESULTS = "quiz_results"

        const val COL_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"
        const val COL_USER_ID_FK = "user_id"
        const val COL_DECK_NAME = "deck_name"
        const val COL_DECK_ID_FK = "deck_id"
        const val COL_FRONT = "front"
        const val COL_BACK = "back"

        // Quiz Results Columns
        const val COL_TOTAL_QUESTIONS = "total_questions"
        const val COL_CORRECT_ANSWERS = "correct_answers"
        const val COL_WRONG_ANSWERS = "wrong_answers"
        const val COL_ANSWERS_JSON = "answers_json" // Storing answers list as JSON string
        const val COL_DATE_TAKEN = "date_taken"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE,
                $COL_PASSWORD TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_DECKS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_ID_FK INTEGER,
                $COL_DECK_NAME TEXT,
                FOREIGN KEY($COL_USER_ID_FK) REFERENCES $TABLE_USERS($COL_ID) ON DELETE CASCADE
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_FLASHCARDS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DECK_ID_FK INTEGER,
                $COL_FRONT TEXT,
                $COL_BACK TEXT,
                FOREIGN KEY($COL_DECK_ID_FK) REFERENCES $TABLE_DECKS($COL_ID) ON DELETE CASCADE
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_QUIZ_RESULTS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_ID_FK INTEGER,
                $COL_DECK_NAME TEXT,
                $COL_TOTAL_QUESTIONS INTEGER,
                $COL_CORRECT_ANSWERS INTEGER,
                $COL_WRONG_ANSWERS INTEGER,
                $COL_ANSWERS_JSON TEXT,
                $COL_DATE_TAKEN INTEGER,
                FOREIGN KEY($COL_USER_ID_FK) REFERENCES $TABLE_USERS($COL_ID) ON DELETE CASCADE
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 7) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_QUIZ_RESULTS (
                    $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_USER_ID_FK INTEGER,
                    $COL_DECK_NAME TEXT,
                    $COL_TOTAL_QUESTIONS INTEGER,
                    $COL_CORRECT_ANSWERS INTEGER,
                    $COL_WRONG_ANSWERS INTEGER,
                    $COL_ANSWERS_JSON TEXT,
                    $COL_DATE_TAKEN INTEGER,
                    FOREIGN KEY($COL_USER_ID_FK) REFERENCES $TABLE_USERS($COL_ID) ON DELETE CASCADE
                )
            """)
        } else {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_FLASHCARDS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_DECKS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_QUIZ_RESULTS")
            onCreate(db)
        }
    }

    // --- User Operations ---

    fun registerUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, username)
            put(COL_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, values) != -1L
    }

    fun authenticateUser(username: String, password: String): Int {
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COL_ID), "$COL_USERNAME = ? AND $COL_PASSWORD = ?", arrayOf(username, password), null, null, null)
        var userId = -1
        if (cursor.moveToFirst()) userId = cursor.getInt(0)
        cursor.close()
        return userId
    }

    fun updateUserProfile(userId: Int, newUsername: String?, newPassword: String?): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            if (!newUsername.isNullOrEmpty()) put(COL_USERNAME, newUsername)
            if (!newPassword.isNullOrEmpty()) put(COL_PASSWORD, newPassword)
        }
        return db.update(TABLE_USERS, values, "$COL_ID = ?", arrayOf(userId.toString())) > 0
    }

    // --- Quiz Result Operations ---

    fun saveQuizResult(userId: Int, result: QuizResult): Long {
        val db = writableDatabase
        val gson = Gson()
        val answersJson = gson.toJson(result.answersList)
        
        val values = ContentValues().apply {
            put(COL_USER_ID_FK, userId)
            put(COL_DECK_NAME, result.deckName)
            put(COL_TOTAL_QUESTIONS, result.totalQuestions)
            put(COL_CORRECT_ANSWERS, result.correctAnswers)
            put(COL_WRONG_ANSWERS, result.wrongAnswers)
            put(COL_ANSWERS_JSON, answersJson)
            put(COL_DATE_TAKEN, result.dateTaken.time)
        }
        return db.insert(TABLE_QUIZ_RESULTS, null, values)
    }

    fun getQuizResultsForUser(userId: Int): List<QuizResult> {
        val results = mutableListOf<QuizResult>()
        val db = readableDatabase
        val gson = Gson()
        
        val cursor = db.query(
            TABLE_QUIZ_RESULTS, null, "$COL_USER_ID_FK = ?",
            arrayOf(userId.toString()), null, null, "$COL_DATE_TAKEN DESC"
        )
        
        if (cursor.moveToFirst()) {
            do {
                val deckName = cursor.getString(cursor.getColumnIndexOrThrow(COL_DECK_NAME))
                val total = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL_QUESTIONS))
                val correct = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CORRECT_ANSWERS))
                val wrong = cursor.getInt(cursor.getColumnIndexOrThrow(COL_WRONG_ANSWERS))
                val json = cursor.getString(cursor.getColumnIndexOrThrow(COL_ANSWERS_JSON))
                val dateLong = cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE_TAKEN))
                
                val type = object : TypeToken<List<UserAnswer>>() {}.type
                val answersList: List<UserAnswer> = gson.fromJson(json, type)
                
                results.add(QuizResult(deckName, total, correct, wrong, answersList, Date(dateLong)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return results
    }

    // --- Deck Operations ---

    fun getDecksForUser(userId: Int): List<Deck> {
        val deckList = mutableListOf<Deck>()
        val db = readableDatabase
        val cursor = db.query(TABLE_DECKS, null, "$COL_USER_ID_FK = ?", arrayOf(userId.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            do {
                deckList.add(Deck(cursor.getInt(0), userId, cursor.getString(2)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return deckList
    }

    fun getFlashcardsForDeck(deckId: Int): List<Flashcard> {
        val cardList = mutableListOf<Flashcard>()
        val db = readableDatabase
        val cursor = db.query(TABLE_FLASHCARDS, null, "$COL_DECK_ID_FK = ?", arrayOf(deckId.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            do {
                cardList.add(Flashcard(cursor.getInt(0), deckId, cursor.getString(2), cursor.getString(3)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return cardList
    }

    fun addDeck(userId: Int, deckName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_ID_FK, userId)
            put(COL_DECK_NAME, deckName)
        }
        return db.insert(TABLE_DECKS, null, values)
    }

    fun addFlashcard(deckId: Int, front: String, back: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DECK_ID_FK, deckId)
            put(COL_FRONT, front)
            put(COL_BACK, back)
        }
        return db.insert(TABLE_FLASHCARDS, null, values)
    }

    fun deleteDeck(deckId: Int): Boolean {
        return writableDatabase.delete(TABLE_DECKS, "$COL_ID = ?", arrayOf(deckId.toString())) > 0
    }

    fun logAllUsers() {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
        while (cursor.moveToNext()) {
            Log.d("DatabaseHelper", "User ID: ${cursor.getInt(0)}, Name: ${cursor.getString(1)}")
        }
        cursor.close()
    }
}
