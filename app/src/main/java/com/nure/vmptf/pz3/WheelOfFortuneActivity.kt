package com.nure.vmptf.pz3

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class WheelOfFortuneActivity : Activity() {
    private val alphabet = listOf(
        'А', 'Б', 'В', 'Г', 'Ґ', 'Д',
        'Е', 'Є', 'Ж', 'З', 'И', 'І',
        'Ї', 'Й', 'К', 'Л', 'М', 'Н',
        'О', 'П', 'Р', 'С', 'Т', 'У',
        'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ',
        'Ь', 'Ю', 'Я'
    )
    private val words = listOf(
        "КОТЛІН",
        "АНДРОЇД",
        "ПРОГРАМА",
        "СТУДЕНТ",
        "АЛГОРИТМ",
        "КАЛЕНДАР"
    )
    private val guessedLetters = mutableSetOf<Char>()

    private lateinit var maskedWordText: TextView
    private lateinit var attemptsText: TextView
    private lateinit var usedLettersText: TextView
    private lateinit var statusText: TextView
    private lateinit var letterInput: EditText
    private lateinit var guessButton: Button
    private lateinit var alphabetContainer: LinearLayout
    private val letterButtons = mutableMapOf<Char, Button>()

    private var currentWord = ""
    private var attemptsLeft = MAX_ATTEMPTS
    private var gameFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wheel_of_fortune)

        maskedWordText = findViewById(R.id.maskedWordText)
        attemptsText = findViewById(R.id.attemptsText)
        usedLettersText = findViewById(R.id.usedLettersText)
        statusText = findViewById(R.id.statusText)
        letterInput = findViewById(R.id.letterInput)
        guessButton = findViewById(R.id.guessButton)
        alphabetContainer = findViewById(R.id.alphabetContainer)

        guessButton.setOnClickListener { checkLetter() }
        findViewById<Button>(R.id.newGameButton).setOnClickListener { startNewGame() }

        createAlphabetButtons()
        startNewGame()
    }

    private fun startNewGame() {
        currentWord = words.random()
        guessedLetters.clear()
        attemptsLeft = MAX_ATTEMPTS
        gameFinished = false
        letterInput.text.clear()
        letterInput.error = null
        guessButton.isEnabled = true
        statusText.text = getString(R.string.wheel_status_start)
        updateViews()
    }

    private fun checkLetter() {
        if (gameFinished) {
            return
        }

        val value = letterInput.text.toString().trim()
        if (value.length != 1) {
            letterInput.error = getString(R.string.wheel_enter_one_letter)
            return
        }

        val letter = value.first().uppercaseChar()
        if (!letter.isLetter()) {
            letterInput.error = getString(R.string.wheel_enter_letter)
            return
        }

        submitLetter(letter)
    }

    private fun submitLetter(letter: Char) {
        if (gameFinished) {
            return
        }

        if (letter in guessedLetters) {
            letterInput.error = getString(R.string.wheel_letter_repeated)
            return
        }

        guessedLetters.add(letter)
        letterInput.text.clear()

        if (letter in currentWord) {
            statusText.text = getString(R.string.wheel_good_guess)
        } else {
            attemptsLeft -= 1
            statusText.text = getString(R.string.wheel_bad_guess)
        }

        when {
            isWordGuessed() -> finishGame(getString(R.string.wheel_win))
            attemptsLeft == 0 -> finishGame(getString(R.string.wheel_lose, currentWord))
            else -> updateViews()
        }
    }

    private fun finishGame(message: String) {
        gameFinished = true
        guessButton.isEnabled = false
        statusText.text = message
        updateViews(showFullWord = attemptsLeft == 0)
    }

    private fun isWordGuessed(): Boolean {
        return currentWord.all { letter -> letter in guessedLetters }
    }

    private fun updateViews(showFullWord: Boolean = false) {
        maskedWordText.text = if (showFullWord) {
            currentWord.toList().joinToString(" ")
        } else {
            currentWord.map { letter ->
                if (letter in guessedLetters) letter else '_'
            }.joinToString(" ")
        }
        attemptsText.text = getString(R.string.wheel_attempts, attemptsLeft)
        usedLettersText.text = if (guessedLetters.isEmpty()) {
            getString(R.string.wheel_used_empty)
        } else {
            getString(R.string.wheel_used_letters, guessedLetters.sorted().joinToString(", "))
        }

        alphabet.forEach { letter ->
            letterButtons[letter]?.isEnabled = !gameFinished && letter !in guessedLetters
        }
    }

    private fun createAlphabetButtons() {
        alphabetContainer.removeAllViews()
        letterButtons.clear()

        alphabet.chunked(6).forEach { rowLetters ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            alphabetContainer.addView(
                row,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8
                }
            )

            rowLetters.forEach { letter ->
                val button = Button(this).apply {
                    text = letter.toString()
                    textSize = 16f
                    isAllCaps = false
                    setOnClickListener {
                        letterInput.error = null
                        submitLetter(letter)
                    }
                }
                row.addView(
                    button,
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    ).apply {
                        leftMargin = 4
                        rightMargin = 4
                    }
                )
                letterButtons[letter] = button
            }
        }
    }

    companion object {
        private const val MAX_ATTEMPTS = 6
    }
}
