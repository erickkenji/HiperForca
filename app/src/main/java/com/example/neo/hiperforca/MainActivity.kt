package com.example.neo.hiperforca

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout

class MainActivity : Activity(), GallowsRecognizer.Listener, GallowsController.Listener {
    // https://stackoverflow.com/questions/26781436/modify-speech-recognition-without-popup
    private var gallowsWord: TextView? = null
    private var speechStatus: TextView? = null
    private var gallowsDebug: TextView? = null
    private var gallowsGuide: TextView? = null
    private var speechButton: ImageView? = null
    private var activityContainer: RelativeLayout? = null
    private var gallowsRecognizer: GallowsRecognizer? = null
    private var gallowsController: GallowsController? = null
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 100

    // region lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gallowsRecognizer = GallowsRecognizer(this, this)
        gallowsController = GallowsController(this, this)
        gallowsWord = activity_main_gallows_word
        speechStatus = activity_main_speech_status_text
        // TODO - remove this debug. The error/hit treatment will be done with messages/images/sounds
        gallowsDebug = activity_main_gallows_debug
        gallowsGuide = activity_main_gallows_guide
        speechButton = activity_main_speak_button
        activityContainer = activity_main_container

        // hide the action bar
        actionBar?.hide()
        speechButton?.setOnClickListener { gallowsRecognizer?.listen() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        gallowsRecognizer?.destroy()
        gallowsRecognizer = null
    }
    // endregion

    // region recognizer
    override fun onError(text: String) {
        Snackbar.make(activityContainer as View, text, Snackbar.LENGTH_LONG).show()
    }

    override fun onBeginningOfSpeech() {
        speechStatus?.text = resources.getString(R.string.listening)
    }

    override fun onEndOfSpeech() {
        speechStatus?.text = ""
    }

    override fun onReadyForSpeech() {
        speechStatus?.text = resources.getString(R.string.ready_for_listening)
    }

    override fun onPermissionNeeded() {
        ActivityCompat.requestPermissions(this,
                                           arrayOf(Manifest.permission.RECORD_AUDIO),
                                           MY_PERMISSIONS_REQUEST_RECORD_AUDIO)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gallowsRecognizer?.listen()
                } else {
                    onError(resources.getString(R.string.error_permission))
                }
            }
        }
    }

    override fun onBeginRecognized() {
        gallowsController?.startGame()
    }

    override fun onLetterRecognized(letter: Char) {
        gallowsController?.checkLetter(letter)
    }
    // endregion


    // region controller
    override fun onWordDefined(partialWord: String) {
        gallowsWord?.text = partialWord
        // TODO - reset image to the first one
        gallowsDebug?.text = ""
        gallowsGuide?.text = resources.getString(R.string.say_letter)
    }

    override fun onLetterHit(partialWord: String) {
        gallowsDebug?.text = ""
        gallowsWord?.text = partialWord
    }

    override fun onLetterMiss(remainingAttempts: Int, wrongLetters: MutableList<Char>) {
        gallowsDebug?.text = "ERROOOOU! Tentativas restantes: $remainingAttempts"
        // TODO - change image according to number of remaining attempts, reproduce audio
        // TODO - show list of wrong letters
    }

    override fun onAlreadyMentionedLetter(letter: Char) {
        gallowsDebug?.text = ""
        val text = String.format(resources.getString(R.string.already_used_letter), letter)
        Snackbar.make(activityContainer as View, text, Snackbar.LENGTH_LONG).show()
    }

    override fun onGameWin(word: String) {
        gallowsRecognizer?.shouldRecognizeLetters = false
        gallowsWord?.text = word
        gallowsDebug?.text = "GANHOOOU"
        gallowsGuide?.text = resources.getString(R.string.say_start_again)
        // TODO - reproduce audio
    }

    override fun onGameLose(word: String) {
        gallowsRecognizer?.shouldRecognizeLetters = false
        gallowsWord?.text = word
        gallowsDebug?.text = "PERDEEEU"
        gallowsGuide?.text = resources.getString(R.string.say_start_again)
        // TODO - update image, reproduce audio
    }
    // endregion
}