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
import android.widget.RelativeLayout

class MainActivity : Activity(), GallowsRecognizer.Listener, GallowsController.Listener {
    // https://stackoverflow.com/questions/26781436/modify-speech-recognition-without-popup
    private var speechInputText: TextView? = null
    private var speechButton: ImageButton? = null
    private var activityContainer: RelativeLayout? = null
    private var gallowsRecognizer: GallowsRecognizer? = null
    private var gallowsController: GallowsController? = null
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 100

    // region lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gallowsRecognizer = GallowsRecognizer(this, this)
        gallowsController = GallowsController(this)
        speechInputText = activity_main_text
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
    override fun onBeginRecognized() {
        this.gallowsController?.startGame()
    }

    override fun onLetterRecognized(letter: Char) {
        this.gallowsController?.checkLetter(letter)
    }

    override fun onError(text: String) {
        Snackbar.make(activityContainer as View, text, Snackbar.LENGTH_LONG).show()
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
                    speechInputText?.text = "Sem permissão!"
                }
            }
        }
    }
    // endregion

    override fun onWordDefined(partialWord: String) {
        this.speechInputText?.text = partialWord
    }

    override fun onLetterHit(partialWord: String) {
        this.speechInputText?.text = partialWord
    }

    override fun onLetterMiss(remainingAttempts: Int, wrongLetters: MutableList<Char>) {
        // TODO - change image according to number of remaining attempts, reproduce audio
        // TODO - add to list of wrong letters
    }

    override fun onAlreadyMentionedLetter(letter: Char) {
        Snackbar.make(activityContainer as View, "A letra $letter já foi utilizada!", Snackbar.LENGTH_LONG).show()
    }

    override fun onGameWin(word: String) {
        this.gallowsRecognizer?.shouldRecognizeLetters = false
        this.speechInputText?.text = word
        // TODO - reproduce audio
    }

    override fun onGameLose(word: String) {
        this.gallowsRecognizer?.shouldRecognizeLetters = false
        this.speechInputText?.text = word
        // TODO - update image, reproduce audio
    }
    // endregion
}