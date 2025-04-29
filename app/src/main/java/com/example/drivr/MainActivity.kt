package com.example.drivr

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val backgroundImage = findViewById<ImageView>(R.id.backgroundImage)
        val tapToContinue = findViewById<TextView>(R.id.tapToContinue)

        // Load and start the blink animation
        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        tapToContinue.startAnimation(blinkAnimation)

        // Detect screen touch to move to the next screen
        backgroundImage.setOnTouchListener { _, _ ->
            val intent = Intent(this, StartupActivity::class.java)
            startActivity(intent)
            true
        }

        // 🔥 Load and play user's selected background music
        loadUserMusic()
    }

    private fun loadUserMusic() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    val selectedMusic = document.getString("selectedMusic") ?: "bmg1"
                    val musicResId = resources.getIdentifier(selectedMusic, "raw", packageName)

                    if (musicResId != 0) {
                        mediaPlayer = MediaPlayer.create(this, musicResId)
                        mediaPlayer.isLooping = true
                        mediaPlayer.setVolume(0.5f, 0.5f)
                        mediaPlayer.start()
                    } else {
                        Toast.makeText(this, "Music file not found!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load music preference!", Toast.LENGTH_SHORT).show()
                }
        } else {
            // No user logged in? Default to bmg1
            mediaPlayer = MediaPlayer.create(this, R.raw.bmg1)
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(0.5f, 0.5f)
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}
