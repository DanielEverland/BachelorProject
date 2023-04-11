package com.DTU.concussionclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var seeSoButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seeSoButton = findViewById(R.id.button)
        seeSoButton.setOnClickListener {
            val intent = Intent(this, SeeSoActivity::class.java)
            startActivity(intent)
        }
    }
}