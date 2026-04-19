package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "NihonGo.db"
        private const val DATABASE_VERSION = 6 // Incremented version

        // Table Names
        private const val TABLE_USERS = "users"
        private const val TABLE_DECKS = "decks"
        private const val TABLE_FLASHCARDS = "flashcards"

        // Common Columns
        private const val COL_ID = "id"

        // Users Table Columns
        private const val COL_USERNAME = "username"
        private const val COL_PASSWORD = "password"

        // Decks Table Columns
        private const val COL_USER_ID_FK = "user_id"
        private const val COL_DECK_NAME = "deck_name"

        // Flashcards Table Columns
        private const val COL_DECK_ID_FK = "deck_id"
        private const val COL_FRONT = "front"
        private const val COL_BACK = "back"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Essential to enable Foreign Key constraints for ON DELETE CASCADE
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Users Table
        val createUsersTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE,
                $COL_PASSWORD TEXT
            )
        """.trimIndent()

        // Create Decks Table
        val createDecksTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_DECKS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_ID_FK INTEGER,
                $COL_DECK_NAME TEXT,
                FOREIGN KEY($COL_USER_ID_FK) REFERENCES $TABLE_USERS($COL_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        // Create Flashcards Table
        val createFlashcardsTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_FLASHCARDS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DECK_ID_FK INTEGER,
                $COL_FRONT TEXT,
                $COL_BACK TEXT,
                FOREIGN KEY($COL_DECK_ID_FK) REFERENCES $TABLE_DECKS($COL_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createDecksTable)
        db.execSQL(createFlashcardsTable)
        Log.d("DatabaseHelper", "Database tables created successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // For development, we drop and recreate. In production, use ALTER TABLE.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FLASHCARDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DECKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // --- User Operations ---

    fun registerUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, username)
            put(COL_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun authenticateUser(username: String, password: String): Int {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS, arrayOf(COL_ID),
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username, password), null, null, null
        )
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
        }
        cursor.close()
        return userId
    }

    // --- Deck Operations ---

    fun addDeck(userId: Int, deckName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_ID_FK, userId)
            put(COL_DECK_NAME, deckName)
        }
        return db.insert(TABLE_DECKS, null, values)
    }

    fun deleteDeck(deckId: Int): Boolean {
        val db = writableDatabase
        // Because of ON DELETE CASCADE, deleting a deck will also delete its flashcards
        val result = db.delete(TABLE_DECKS, "$COL_ID = ?", arrayOf(deckId.toString()))
        return result > 0
    }

    fun getDecksForUser(userId: Int): List<Deck> {
        val deckList = mutableListOf<Deck>()
        val db = readableDatabase
        val cursor = db.query(TABLE_DECKS, null, "$COL_USER_ID_FK = ?", arrayOf(userId.toString()), null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_DECK_NAME))
                deckList.add(Deck(id, userId, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return deckList
    }

    // --- Flashcard Operations ---

    fun addFlashcard(deckId: Int, front: String, back: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DECK_ID_FK, deckId)
            put(COL_FRONT, front)
            put(COL_BACK, back)
        }
        return db.insert(TABLE_FLASHCARDS, null, values)
    }

    fun getFlashcardsForDeck(deckId: Int): List<Flashcard> {
        val cardList = mutableListOf<Flashcard>()
        val db = readableDatabase
        val cursor = db.query(TABLE_FLASHCARDS, null, "$COL_DECK_ID_FK = ?", arrayOf(deckId.toString()), null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                val front = cursor.getString(cursor.getColumnIndexOrThrow(COL_FRONT))
                val back = cursor.getString(cursor.getColumnIndexOrThrow(COL_BACK))
                cardList.add(Flashcard(id, deckId, front, back))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return cardList
    }

    // Debug helper
    fun logAllUsers() {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
            val user = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME))
            Log.d("DatabaseHelper", "User ID: $id, Name: $user")
        }
        cursor.close()
    }
}
