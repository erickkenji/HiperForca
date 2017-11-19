package com.example.neo.hiperforca

import android.content.Context
import com.example.neo.hiperforca.core.SpeechRecognizerService

/**
 * Created by isabella on 17/11/17.
 */
class GallowsRecognizer(val context: Context, val listener: GallowsRecognizer.Listener) : SpeechRecognizerService.Listener {
    var shouldRecognizeLetters: Boolean = false
    private var speechRecognizerService: SpeechRecognizerService? = null

    interface Listener {
        fun onLetterRecognized(letter: Char)
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
        if (shouldRecognizeLetters) {
            recognizeLetters(text)
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

    private fun recognizeLetters(text: String) {
        val splittedText = text.split(" ")
        val firstWord = splittedText[0].toLowerCase()
        if ((firstWord != "letra" && firstWord != "letter") || splittedText.size == 1) {
            sendInvalidSpeechError()
            return
        }

        val letter = splittedText[1].toLowerCase()
        if (context.resources.getStringArray(R.array.letters).contains(letter)) {
            listener.onLetterRecognized(letter[0])
        } else {
            sendInvalidSpeechError()
        }
    }

    private fun recognizeBeginningOfGame(text: String) {
        val splittedText = text.split(" ")
        val firstWord = splittedText[0].toLowerCase()

        if (firstWord != "começar" && firstWord != "start") {
            sendInvalidSpeechError()
        } else {
            shouldRecognizeLetters = true
            listener.onBeginRecognized()
        }
    }

    private fun sendInvalidSpeechError() {
        listener.onError(context.resources.getString(R.string.error_invalid_entry))
    }
}