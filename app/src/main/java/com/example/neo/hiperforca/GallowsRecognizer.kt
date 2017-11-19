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
    }

    init {
        speechRecognizerService = SpeechRecognizerService(context, this)
    }

    override fun onSpeechRecognized(text: String) {
        // TODO - param for understanding when to recognize letters or the beginning of the game
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

    fun listen() {
        speechRecognizerService?.listen()
    }

    fun destroy() {
        speechRecognizerService?.destroy()
        speechRecognizerService = null
    }

    private fun recognizeLetters(text: String) {
        val splittedText = text.split(" ")
        if (splittedText[0] != "letra" && splittedText[0] != "letter" && splittedText.size == 1) {
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
        if (splittedText[0] != "começar" && splittedText[0] != "begin") {
            sendInvalidSpeechError()
        } else {
            shouldRecognizeLetters = true
            listener.onBeginRecognized()
        }
    }

    private fun sendInvalidSpeechError() {
        listener.onError("Entrada inválida! Tente novamente")
    }
}