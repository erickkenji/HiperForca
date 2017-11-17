package com.example.neo.hiperforca

import android.content.Context
import com.example.neo.hiperforca.core.SpeechRecognizerService

/**
 * Created by isabella on 17/11/17.
 */
class GallowsRecognizer(val context: Context, val listener: GallowsRecognizer.Listener) : SpeechRecognizerService.Listener {
    private var speechRecognizerService: SpeechRecognizerService? = null

    interface Listener {
        fun onSpeechRecognized(text: String)
        fun onError(text: String)
        fun onPermissionNeeded()
    }

    init {
        speechRecognizerService = SpeechRecognizerService(context, this)
    }

    override fun onSpeechRecognized(text: String) {
        // TODO - param for understanding when to recognize letters or the beginning of the game
        if (true) {
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
            listener.onError("Entrada inválida! Tente novamente")
            return
        }

        val letter = splittedText[1].toLowerCase()
        val letters = context.resources.getStringArray(R.array.letters)
        if (letters.contains(letter)) {
            listener.onSpeechRecognized(letter)
            return
        }
        listener.onError("Entrada inválida! Tente novamente")
    }

    private fun recognizeBeginningOfGame(text: String) {
        val splittedText = text.split(" ")
        if (splittedText[0] != "começar" && splittedText[0] != "begin") {
            listener.onError("Entrada inválida! Tente novamente")
            return
        } else {
            listener.onSpeechRecognized(splittedText[0])
        }
    }
}