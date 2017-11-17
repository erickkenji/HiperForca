package com.example.neo.hiperforca

import java.util.Locale
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), RecognitionListener {
    // https://www.androidhive.info/2014/07/android-speech-to-text-tutorial/
    private var txtSpeechInput: TextView? = null
    private var btnSpeak: ImageButton? = null
    private var speech: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    private val REQ_CODE_SPEECH_INPUT = 100
    private val LOG_TAG = "VoiceRecognition"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Locale.setDefault(Locale("pt", "BR"))

        txtSpeechInput = activity_main_text
        btnSpeak = activity_main_speak_button

        speech = SpeechRecognizer.createSpeechRecognizer(this)
        speech?.setRecognitionListener(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "pt")
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)

        // hide the action bar
        actionBar?.hide()
        btnSpeak?.setOnClickListener { speech?.startListening(recognizerIntent) }
    }

    /**
     * Showing google speech input dialog
     */
    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(applicationContext,
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Receiving speech input
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    txtSpeechInput?.text = result[0]
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        if (speech != null) {
            speech?.destroy()
            Log.i(LOG_TAG, "destroy")
        }
    }

    override fun onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer)
    }

    override fun onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech")
    }

    override fun onError(errorCode: Int) {
        val errorMessage = getErrorText(errorCode)
        Log.d(LOG_TAG, "FAILED " + errorMessage)
        txtSpeechInput?.text = errorMessage
    }

    override fun onEvent(arg0: Int, arg1: Bundle) {
        Log.i(LOG_TAG, "onEvent")
    }

    override fun onPartialResults(arg0: Bundle) {
        Log.i(LOG_TAG, "onPartialResults")
    }

    override fun onReadyForSpeech(arg0: Bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech")
    }

    override fun onResults(results: Bundle) {
        Log.i(LOG_TAG, "onResults")
        val matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        for (result in matches!!)
            text += result + "\n"

        txtSpeechInput?.text = text
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB)
    }

    fun getErrorText(errorCode: Int): String {
        val message: String
        when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> message = "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> message = "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> message = "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message = "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> message = "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> message = "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "No speech input"
            else -> message = "Didn't understand, please try again."
        }
        return message
    }
}