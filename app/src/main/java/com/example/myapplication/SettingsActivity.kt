package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        dbHelper = DatabaseHelper.getInstance(this)
        userId = intent.getIntExtra("USER_ID", -1)

        val etUsername = findViewById<EditText>(R.id.etNewUsername)
        val etPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassword)
        val btnUpdate = findViewById<Button>(R.id.btnUpdateProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.selectedItemId = R.id.nav_settings

        btnUpdate.setOnClickListener {
            val newUsername = etUsername.text.toString().trim()
            val newPassword = etPassword.text.toString().trim()
            val confirm = etConfirm.text.toString().trim()

            if (newUsername.isEmpty() && newPassword.isEmpty()) {
                Toast.makeText(this, "Please enter at least one field to update", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.isNotEmpty() && newPassword != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Using the new helper method to encapsulate logic
            val success = dbHelper.updateUserProfile(
                userId,
                if (newUsername.isNotEmpty()) newUsername else null,
                if (newPassword.isNotEmpty()) newPassword else null
            )

            if (success) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                etUsername.text.clear()
                etPassword.text.clear()
                etConfirm.text.clear()
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_lessons -> {
                    val intent = Intent(this, LessonListActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_quiz -> {
                    val intent = Intent(this, QuizModeActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_alphabet -> {
                    val intent = Intent(this, BasicAlphabetActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_decks -> {
                    val intent = Intent(this, DeckListActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}
