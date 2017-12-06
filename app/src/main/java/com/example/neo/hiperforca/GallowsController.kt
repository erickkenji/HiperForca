package com.example.neo.hiperforca

import android.content.Context
import com.example.neo.hiperforca.core.Preferences
import com.example.neo.hiperforca.core.GlobalConstants
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by isabella on 18/11/17.
 */
class GallowsController(private val context: Context, private val listener: Listener): GallowsTimer.Listener {
    interface Listener {
        fun onWordDefined(partialWord: String)
        fun onLetterHit(partialWord: String)
        fun onLetterMiss(remainingAttempts: Int, wrongLetters: MutableList<Char>)
        fun onGameWin(word: String, newScore: Int)
        fun onGameLose(word: String, wrongLetters: MutableList<Char>, lostByTime: Boolean, newScore: Int)
        fun onAlreadyMentionedLetter(letter: Char)
        fun onTimerTick(remainingSeconds: Long)
    }

    var hasActiveGame: Boolean = false
    private var alreadyMentionedLetters: MutableList<Char> = mutableListOf()
    private var wrongLetters: MutableList<Char> = mutableListOf()
    private var partialWord: String = ""
    private var remainingAttempts: Int = GlobalConstants.NUMBER_OF_ATTEMPTS
    private var remainingSeconds: Long = GlobalConstants.SECONDS_TO_PLAY
    private var timer: GallowsTimer? = null
    lateinit private var word: String

    // region timer
    override fun onTimerTick(remainingSeconds: Long) {
        this.remainingSeconds = remainingSeconds
        listener.onTimerTick(remainingSeconds)
    }

    override fun onTimerFinished() {
        hasActiveGame = false
        onGameLose(true, GlobalConstants.TIME_PENALTY)
    }
    // endregion

    // region controller
    fun startGame() {
        hasActiveGame = true
        remainingAttempts = GlobalConstants.NUMBER_OF_ATTEMPTS
        alreadyMentionedLetters = mutableListOf()
        wrongLetters = mutableListOf()
        partialWord = ""
        word = getRandomWord()
        // Fills the words with blank spaces
        word.forEach { char -> if (char == ' ') partialWord += char else partialWord += '_' }
        timer = GallowsTimer((GlobalConstants.SECONDS_TO_PLAY * 1000) + 1, 1000, this)
        timer?.start()
        listener.onWordDefined(partialWord)
    }

    fun checkLetter(letter: Char) {
        if (alreadyMentionedLetters.contains(letter)) {
            listener.onAlreadyMentionedLetter(letter)
            return
        }

        alreadyMentionedLetters.add(letter)
        if (word.contains(letter)) {
            handleHit(letter)
        } else {
            handleMiss(letter)
        }
    }

    fun checkWord(word: String) {
        if (this.word == word) {
            onGameWin(GlobalConstants.WORD_HIT_BONUS)
        } else {
            onGameLose(false, GlobalConstants.WORD_ERROR_PENALTY)
        }
    }
    // endregion

    // region private
    private fun handleHit(letter: Char) {
        val positions = mutableListOf<Int>()
        word.forEachIndexed { index, char -> if (char.toLowerCase() == letter) {
                                                    positions.add(index) } }
        val builder: StringBuilder = StringBuilder(partialWord)
        positions.forEach { position -> builder.setCharAt(position, letter) }
        partialWord = builder.toString()

        if (partialWord == word) {
            onGameWin()
        } else {
            listener.onLetterHit(partialWord)
        }
    }

    private fun handleMiss(letter: Char) {
        wrongLetters.add(letter)
        remainingAttempts -= 1

        if (remainingAttempts <= 0) {
            onGameLose(false)
        } else {
            listener.onLetterMiss(remainingAttempts, wrongLetters)
        }
    }

    private fun getRandomWord(): String {
        val wordsArray = context.resources.getStringArray(R.array.words)
        val rand = ThreadLocalRandom.current().nextInt(0, wordsArray.size)
        return wordsArray[rand]
    }

    private fun onGameWin(bonus: Int = 0) {
        hasActiveGame = false
        timer?.cancel()
        val score = remainingAttempts + calculateTimeBonus(remainingSeconds) + bonus
        val newScore = Preferences.addScore(context, score)
        listener.onGameWin(word, newScore)
    }

    private fun onGameLose(lostByTime: Boolean, penalty: Int = 0) {
        hasActiveGame = false
        timer?.cancel()
        val newScore = Preferences.addScore(context, -penalty)
        listener.onGameLose(word, wrongLetters, lostByTime, newScore)
    }
    // endregion

    companion object {
        fun calculateTimeBonus(remainingSeconds: Long): Int {
            return Math.floor(remainingSeconds.toDouble() / GlobalConstants.REMAINING_TIME_FACTOR).toInt()
        }
    }
}