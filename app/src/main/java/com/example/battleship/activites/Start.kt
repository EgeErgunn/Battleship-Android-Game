package com.example.battleship.activites

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.battleship.R

class Start : AppCompatActivity() {
    private lateinit var playerScoreText: TextView
    private lateinit var botScoreText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)

        // Score Texts
        playerScoreText = findViewById(R.id.textView2)
        botScoreText = findViewById(R.id.textView3)

        // Get stored values with SharedPreferences
        val prefs = getSharedPreferences("BATTLESHIP_SCORES", Context.MODE_PRIVATE)
        val playerScore = prefs.getInt("PLAYER_WINS", 0)
        val botScore = prefs.getInt("BOT_WINS", 0)

        playerScoreText.text = "Player Wins: " + playerScore
        botScoreText.text = "Bot Wins: " + botScore

        // Start Button
        val startButton = findViewById<Button>(R.id.button)

        startButton.setOnClickListener {
            val intent = Intent(this, ShipPlacement::class.java)
            startActivity(intent)
            finish()
        }
    }


}