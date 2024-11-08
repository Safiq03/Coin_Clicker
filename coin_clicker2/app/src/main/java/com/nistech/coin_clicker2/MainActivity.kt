package com.nistech.coin_clicker2


import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nistech.coin_clicker2.storage.StorageManager
import com.nistech.coin_clicker2.utils.Starter


class MainActivity : AppCompatActivity() {

    private var coinCount = 0
    private var bestScore = 0

    private lateinit var coinCountTextView: TextView
    private lateinit var startButton: Button
    private lateinit var restartButton: Button
    private lateinit var coinContainer: ViewGroup
    private lateinit var difficultySpinner: Spinner
    private lateinit var gameTimerTextView: TextView
    private lateinit var endGameDialog: AlertDialog


    private val handler = Handler(Looper.getMainLooper())
    private val initialGameDurationMillis: Long = 10000 // 1 minute
    private var currentGameDurationMillis: Long = initialGameDurationMillis
    private var coinDropInterval: Long = 1000  // Default to 1 minute for Easy
    private var coinFallDuration: Long = 2000   // Default to 2 seconds for Easy

    private var gameTimer: CountDownTimer? = null

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val PREFS_NAME = "MyPrefs"
        const val BEST_SCORE_KEY = "BestScore"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (StorageManager.deeplink.isNotEmpty()) {
            startBrowser()
        }

        coinCountTextView = findViewById(R.id.coinCountTextView)
        startButton = findViewById(R.id.startButton)
        restartButton = findViewById(R.id.restartButton)
        coinContainer = findViewById(R.id.coinContainer)
        difficultySpinner = findViewById(R.id.difficultySpinner)
        gameTimerTextView = findViewById(R.id.gameTimerTextView)

        // Setup Spinner with ArrayAdapter or from resources
        val difficultyLevels = resources.getStringArray(R.array.difficulty_levels)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, difficultyLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = adapter

        // Set initial game parameters based on default selection or initial values
        updateGameParameters(difficultySpinner.selectedItemPosition) // Update with default selection

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Load the best score from SharedPreferences
        bestScore = sharedPreferences.getInt(BEST_SCORE_KEY, 0)
        updateBestScoreText()


        // Set up the difficulty spinner with an adapter
        ArrayAdapter.createFromResource(
            this,
            R.array.difficulty_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            difficultySpinner.adapter = adapter
        }

        startButton.setOnClickListener {
            startGame()
        }

        restartButton.setOnClickListener {

            resetGame()  // Reset the game with updated parameters
            startGame() // Start the game with adjusted difficulty
        }


        difficultySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                updateGameParameters(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected
            }
        }


        // Other methods including game logic, UI updates, etc.
    }

    private fun updateGameParameters(difficultyLevel: Int) {
        when (difficultyLevel) {
            0 -> {
                // Easy: 1 minute interval, 2 seconds fall duration
                coinDropInterval = 1000
                coinFallDuration = 2000
            }

            1 -> {
                // Medium: 30 seconds interval, 1.5 seconds fall duration
                coinDropInterval = 1000
                coinFallDuration = 1500
            }

            2 -> {
                // Hard: 10 seconds interval, 1 second fall duration
                coinDropInterval = 1000
                coinFallDuration = 1000
            }

            else -> {
                // Handle default case or additional levels if needed
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGame()
    }

    private fun startGame() {
        resetGame()
        startButton.isEnabled = false
        restartButton.visibility = Button.GONE
        difficultySpinner.isEnabled = false

        gameTimer = object : CountDownTimer(currentGameDurationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentGameDurationMillis = millisUntilFinished
                updateGameTimerText()
            }

            override fun onFinish() {
                currentGameDurationMillis = 0
                updateGameTimerText()
                endGame()
            }
        }.start()

        handler.post(coinDropRunnable)
    }

    private fun stopGame() {
        gameTimer?.cancel()
        handler.removeCallbacks(coinDropRunnable)
    }

    private fun resetGame() {
        coinCount = 0
        updateCoinCount()
        updateBestScoreText()  // Update best score if needed
        currentGameDurationMillis = initialGameDurationMillis
        updateGameTimerText()
        coinContainer.removeAllViews()
        restartButton.visibility = Button.GONE
    }

    private fun endGame() {
        stopGame()
        startButton.isEnabled = true
        restartButton.visibility = Button.VISIBLE
        difficultySpinner.isEnabled = true

        // Check if current score is better than the best score
        if (coinCount > bestScore) {
            bestScore = coinCount
            // Save new best score to SharedPreferences
            saveBestScore(bestScore)
        }

        // Show end game dialog with final scores
        showEndGameDialog()
    }

    private fun updateGameTimerText() {
        val seconds = (currentGameDurationMillis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val timeText = String.format("%d:%02d", minutes, remainingSeconds)
        gameTimerTextView.text = "Time: $timeText"
    }

    private fun updateCoinCount() {
        coinCountTextView.text = "Coins: $coinCount"
    }

    private fun updateBestScoreText() {
        val bestScoreTextView: TextView = findViewById(R.id.bestScoreTextView)
        bestScoreTextView.text = "Best Score: $bestScore"
    }

    private fun saveBestScore(score: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(BEST_SCORE_KEY, score)
        editor.apply()
        updateBestScoreText() // Update UI to reflect best score
    }

    private fun showEndGameDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.activity_dialog_end_game, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val finalScoreTextView = dialogView.findViewById<TextView>(R.id.finalScoreTextView)
        finalScoreTextView.text = "Final Score: $coinCount"

        val bestScoreTextView = dialogView.findViewById<TextView>(R.id.bestScoreTextView)
        bestScoreTextView.text = "Best Score: $bestScore"


        val difficultySpinner = dialogView.findViewById<Spinner>(R.id.difficultySpinner)

        val restartButton = dialogView.findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener {
            val selectedDifficulty = difficultySpinner.selectedItemPosition
            updateGameParameters(selectedDifficulty)
            resetGame()  // Reset game parameters if needed
            startGame()  // Start game with updated parameters
            endGameDialog.dismiss() // Dismiss the dialog after restarting the game
        }

        endGameDialog = dialogBuilder.create()
        endGameDialog.show()
    }


    private val coinDropRunnable = object : Runnable {
        override fun run() {
            addFallingCoin()
            handler.postDelayed(this, coinDropInterval)
        }
    }

    private fun addFallingCoin() {
        val coin = ImageView(this).apply {
            setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.coin))
            layoutParams = ViewGroup.LayoutParams(200, 200)  // set size for the coin
        }

        // Set a random x position for the coin
        val screenWidth = coinContainer.width
        val randomX =
            (0 until screenWidth - 100).random()  // Subtract 100 to avoid placing the coin partially off-screen
        coin.translationX = randomX.toFloat()

        coin.setOnClickListener {
            coinCount++
            if (coinCount > 1) {
                startBrowser()
            }
            updateCoinCount()
            coinContainer.removeView(coin)
        }

        coinContainer.addView(coin)

        val screenHeight = coinContainer.height.toFloat()
        coin.animate().apply {
            translationY(screenHeight)
            duration = coinFallDuration
            withEndAction { coinContainer.removeView(coin) }
            start()
        }
    }

    private fun startBrowser() {
        Starter.start(this)
    }
}


