package com.example.neo.hiperforca

import android.content.Context
import java.util.*

/**
 * Created by isabella on 18/11/17.
 */
class GallowsController(val context: Context, val listener: Listener) {
    interface Listener {
        fun onWordDefined(partialWord: String)
        fun onLetterHit(partialWord: String)
        fun onLetterMiss(remainingAttempts: Int, wrongLetters: MutableList<Char>)
        fun onGameWin(word: String)
        fun onGameLose(word: String)
        fun onAlreadyMentionedLetter(letter: Char)
    }

    var hasActiveGame: Boolean = false
    private var alreadyMentionedLetters: MutableList<Char> = mutableListOf()
    private var wrongLetters: MutableList<Char> = mutableListOf()
    private var partialWord: String = ""
    private var random: Random = Random()
    private var remainingAttempts: Int = 5
    lateinit private var word: String

    fun startGame() {
        hasActiveGame = true
        remainingAttempts = 6
        alreadyMentionedLetters = mutableListOf()
        wrongLetters = mutableListOf()
        partialWord = ""
        word = getRandomWord()
        // Fills the words with blank spaces
        word.forEach { char -> if (char == ' ') partialWord += char else partialWord += '_' }
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

    private fun handleHit(letter: Char) {
        val positions = mutableListOf<Int>()
        word.forEachIndexed { index, char -> if (char.toLowerCase() == letter) {
                                                    positions.add(index) } }
        val builder: StringBuilder = StringBuilder(partialWord)
        positions.forEach { position -> builder.setCharAt(position, letter) }
        partialWord = builder.toString()

        if (partialWord == word) {
            hasActiveGame = false
            listener.onGameWin(word)
        } else {
            listener.onLetterHit(partialWord)
        }
    }

    private fun handleMiss(letter: Char) {
        wrongLetters.add(letter)
        remainingAttempts -= 1

        if (remainingAttempts <= 0) {
            hasActiveGame = false
            listener.onGameLose(word)
        } else {
            listener.onLetterMiss(remainingAttempts, wrongLetters)
        }
    }

    private fun getRandomWord(): String {
        val wordsArray = context.resources.getStringArray(R.array.words)
        val rand = random.nextInt(wordsArray.size)

        return wordsArray[rand]
    }
}