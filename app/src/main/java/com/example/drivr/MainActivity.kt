package com.example.drivr

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the TextView for blinking effect
        val tapToContinue = findViewById<TextView>(R.id.tapToContinue)

        // Load and start the blink animation
        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        tapToContinue.startAnimation(blinkAnimation)

        // Detect screen touch to move to the next screen
        findViewById<ImageView>(R.id.backgroundImage).setOnTouchListener { _, _ ->
            val intent = Intent(this, StartupActivity::class.java)
            startActivity(intent)
            true
        }
    }
}
