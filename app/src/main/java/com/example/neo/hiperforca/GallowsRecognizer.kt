package com.example.neo.hiperforca

import android.content.Context
import com.example.neo.hiperforca.core.SpeechRecognizerService

/**
 * Created by isabella on 17/11/17.
 */
class GallowsRecognizer(context: Context, val listener: GallowsRecognizer.Listener) : SpeechRecognizerService.Listener {
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
        listener.onSpeechRecognized(text)
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
}