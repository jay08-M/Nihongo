package com.example.myapplication

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class LessonDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the layout resource ID passed from the list
        val layoutId = intent.getIntExtra("LAYOUT_ID", R.layout.fragment_lesson_hiragana_part_one)
        setContentView(layoutId)

        // Setup Back Button
        findViewById<ImageButton>(R.id.btnBackLesson)?.setOnClickListener {
            finish()
        }
    }
}
