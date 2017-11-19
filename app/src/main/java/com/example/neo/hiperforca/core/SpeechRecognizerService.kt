package com.example.neo.hiperforca.core

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.content.ContextCompat
import android.util.Log
import com.example.neo.hiperforca.R
import java.util.*

/**
 * Created by isabella on 17/11/17.
 */
class SpeechRecognizerService(val context: Context, val listener: Listener): RecognitionListener {
    private var speech: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    private val LOG_TAG = "VoiceRecognition"

    interface Listener {
        fun onSpeechRecognized(text: String)
        fun onError(text: String)
        fun onPermissionNeeded()
        fun onBeginningOfSpeech()
        fun onEndOfSpeech()
        fun onReadyForSpeech()
    }

    init {
        speech = SpeechRecognizer.createSpeechRecognizer(context)
        speech?.setRecognitionListener(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "pt")
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("pt", "BR"))
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
    }

    fun listen() {
        if (hasAudioPermission()) {
            speech?.startListening(recognizerIntent)
        } else {
            listener.onPermissionNeeded()
        }
    }

    fun destroy() {
        if (speech != null) {
            speech?.destroy()
            Log.i(LOG_TAG, "destroy")
        }
    }

    override fun onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech")
        listener.onBeginningOfSpeech()
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer)
    }

    override fun onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech")
        listener.onEndOfSpeech()
    }

    override fun onError(errorCode: Int) {
        val errorMessage = getErrorText(errorCode)
        Log.d(LOG_TAG, "FAILED " + errorMessage)
        listener.onError(errorMessage)
    }

    override fun onEvent(arg0: Int, arg1: Bundle) {
        Log.i(LOG_TAG, "onEvent")
    }

    override fun onPartialResults(arg0: Bundle) {
        Log.i(LOG_TAG, "onPartialResults")
    }

    override fun onReadyForSpeech(arg0: Bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech")
        listener.onReadyForSpeech()
    }

    override fun onResults(results: Bundle) {
        Log.i(LOG_TAG, "onResults")
        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        listener.onSpeechRecognized(matches[0])
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB)
    }

    private fun getErrorText(errorCode: Int): String {
        val resourceId = when (errorCode) {
                            SpeechRecognizer.ERROR_AUDIO -> R.string.error_recording_audio
                            SpeechRecognizer.ERROR_CLIENT -> R.string.error_client_side
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> R.string.error_permission
                            SpeechRecognizer.ERROR_NETWORK -> R.string.error_network
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> R.string.error_network_timeout
                            SpeechRecognizer.ERROR_NO_MATCH -> R.string.error_no_match
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> R.string.error_recognizer_busy
                            SpeechRecognizer.ERROR_SERVER -> R.string.error_server
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> R.string.error_speech_timeout
                            else -> R.string.error_default }
        return context.resources.getString(resourceId)
    }

    private fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
    }
}