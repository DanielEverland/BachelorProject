package com.DTU.concussionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var gotoBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gotoBtn = findViewById(R.id.SpeechToText_button)
        gotoBtn.setOnClickListener {
            val intent = Intent(this, SpeechToTextActivity::class.java)
            startActivity(intent)
        }
    }
}