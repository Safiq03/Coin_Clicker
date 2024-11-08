package com.nistech.coin_clicker2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog


class EndGameActivity : AppCompatActivity() {


    private lateinit var endGameDialog: AlertDialog  // Declare dialog at class level
    private lateinit var finalScoreTextView: TextView
    private lateinit var bestScoreTextView: TextView
    private lateinit var restartButton: Button
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_end_game)

        finalScoreTextView = findViewById(R.id.finalScoreTextView)
        bestScoreTextView = findViewById(R.id.bestScoreTextView)

        restartButton = findViewById(R.id.restartButton)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)

        // Get final score from intent
        val finalScore = intent.getIntExtra("finalScore", 0)
        finalScoreTextView.text = "Final Score: $finalScore"

        // Load and display best score from SharedPreferences
        val bestScore = sharedPreferences.getInt(MainActivity.BEST_SCORE_KEY, 0)
        bestScoreTextView.text = "Best Score: $bestScore"

        // Restart game button click listener
        restartButton.setOnClickListener {
            // Clear any existing best score (optional)
            clearBestScore()
            // Restart MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    private fun clearBestScore() {
        // Optionally, clear best score from SharedPreferences
        val editor = sharedPreferences.edit()
        editor.remove(MainActivity.BEST_SCORE_KEY)
        editor.apply()
    }
}

class EndGameDialog(
    private val context: Context,
    private val coinCount: Int,
    private val bestScore: Int,
    private val restartAction: () -> Unit
) {

    private lateinit var dialog: AlertDialog

    fun showDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_dialog_end_game, null)

        val finalScoreTextView = dialogView.findViewById<TextView>(R.id.finalScoreTextView)
        finalScoreTextView.text = "Final Score: $coinCount"
        val bestScoreTextView = dialogView.findViewById<TextView>(R.id.bestScoreTextView)
        bestScoreTextView.text = "Best Score: $bestScore"

        val restartButton = dialogView.findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener {
            dialog.dismiss()
            // Perform restart action
            restartAction.invoke()
        }

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        dialog = dialogBuilder.create()
        dialog.show()
    }

    fun dismissDialog() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }
}

