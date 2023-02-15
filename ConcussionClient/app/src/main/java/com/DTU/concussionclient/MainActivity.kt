package com.DTU.concussionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEyeDetection = findViewById<Button>(R.id.button_eye_detection)
        btnEyeDetection.setOnClickListener {
            val intent = Intent(this@MainActivity, EyeDetection::class.java)
            startActivity(intent)
        }
    }
}