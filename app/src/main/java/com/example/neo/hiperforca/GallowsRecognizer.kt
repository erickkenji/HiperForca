package com.example.neo.hiperforca

import android.content.Context
import com.example.neo.hiperforca.core.SpeechRecognizerService
import org.apache.commons.lang3.StringUtils

/**
 * Created by isabella on 17/11/17.
 */
class GallowsRecognizer(private val context: Context, private val listener: GallowsRecognizer.Listener) : SpeechRecognizerService.Listener {
    var shouldRecognizeLettersOrWord: Boolean = false
    private var speechRecognizerService: SpeechRecognizerService? = null

    interface Listener {
        fun onLetterRecognized(letter: Char)
        fun onWordRecognized(word: String)
        fun onBeginRecognized()
        fun onError(text: String)
        fun onPermissionNeeded()
        fun onBeginningOfSpeech()
        fun onEndOfSpeech()
        fun onReadyForSpeech()
    }

    init {
        speechRecognizerService = SpeechRecognizerService(context, this)
    }

    override fun onSpeechRecognized(text: String) {
        if (shouldRecognizeLettersOrWord) {
            recognizeLettersOrWord(text)
        } else {
            recognizeBeginningOfGame(text)
        }
    }

    override fun onError(text: String) {
        listener.onError(text)
    }

    override fun onPermissionNeeded() {
        listener.onPermissionNeeded()
    }

    override fun onBeginningOfSpeech() {
        listener.onBeginningOfSpeech()
    }

    override fun onEndOfSpeech() {
        listener.onEndOfSpeech()
    }

    override fun onReadyForSpeech() {
        listener.onReadyForSpeech()
    }

    fun listen() {
        speechRecognizerService?.listen()
    }

    fun destroy() {
        speechRecognizerService?.destroy()
        speechRecognizerService = null
    }

    private fun recognizeLettersOrWord(text: String) {
        val splittedText = text.split(" ", limit = 2)
        val firstWord = splittedText[0].toLowerCase()
        if ((firstWord != context.resources.getString(R.string.letter) && firstWord != context.resources.getString(R.string.word)) || splittedText.size == 1) {
            sendInvalidSpeechError()
            return
        }

        if (firstWord == context.resources.getString(R.string.letter)) {
            recognizeLetters(splittedText)
        } else if (firstWord == context.resources.getString(R.string.word)) {
            recognizeWord(splittedText)
        }
    }

    private fun recognizeLetters(splittedText: List<String>) {
        val letter = splittedText[1].toLowerCase()
        if (context.resources.getStringArray(R.array.letters).contains(letter)) {
            listener.onLetterRecognized(letter[0])
        } else {
            sendInvalidSpeechError()
        }
    }

    private fun recognizeWord(splittedText: List<String>) {
        val word = StringUtils.stripAccents(splittedText[1].toLowerCase())
        listener.onWordRecognized(word)
    }

    private fun recognizeBeginningOfGame(text: String) {
        val splittedText = text.split(" ")
        val firstWord = splittedText[0].toLowerCase()

        if (firstWord != context.resources.getString(R.string.start)) {
            sendInvalidSpeechError()
        } else {
            shouldRecognizeLettersOrWord = true
            listener.onBeginRecognized()
        }
    }

    private fun sendInvalidSpeechError() {
        listener.onError(context.resources.getString(R.string.error_invalid_entry))
    }
}