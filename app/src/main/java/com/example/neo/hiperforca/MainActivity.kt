package com.example.neo.hiperforca

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import rules.RulesActivity

class MainActivity : AppCompatActivity(), GallowsRecognizer.Listener, GallowsController.Listener {
    // https://stackoverflow.com/questions/26781436/modify-speech-recognition-without-popup
    private var gallowsWord: TextView? = null
    private var speechStatus: TextView? = null
    private var gallowsGuide: TextView? = null
    private var gallowsWrongLetters: TextView? = null
    private var gallowsTimer: TextView? = null
    private var speechButton: ImageView? = null
    private var gallowsImage: ImageView? = null
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
        gallowsWord?.letterSpacing = 0.3f
        gallowsWrongLetters = activity_main_gallows_wrong_letters
        gallowsTimer = activity_main_gallows_timer
        speechStatus = activity_main_speech_status_text
        gallowsGuide = activity_main_gallows_guide
        speechButton = activity_main_speak_button
        gallowsImage = activity_main_gallows_image
        activityContainer = activity_main_container

        // hide the action bar
        // actionBar?.hide()
        setSupportActionBar(activity_main_toolbar)
        speechButton?.setOnClickListener { gallowsRecognizer?.listen() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_help -> {
                intent = Intent(this, RulesActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    override fun onWordRecognized(word: String) {
        // open modal
        val builder = AlertDialog.Builder(this)
        builder.setMessage(String.format(resources.getString(R.string.should_recognize_word), word))
                .setPositiveButton(R.string.yes, { dialog, id -> gallowsController?.checkWord(word) })
                .setNegativeButton(R.string.no, { dialog, id ->
                    // User cancelled the dialog
                })
                .create()
                .show()
    }
    // endregion


    // region controller
    override fun onWordDefined(partialWord: String) {
        gallowsWord?.text = partialWord
        gallowsWrongLetters?.text = ""
        gallowsImage?.setImageResource(R.drawable.ico_gallow)
        gallowsGuide?.text = resources.getString(R.string.say_letter)
        setTimerTextAndColor(GallowsController.SECONDS_TO_PLAY)
    }

    override fun onLetterHit(partialWord: String) {
        gallowsWord?.text = partialWord
    }

    override fun onLetterMiss(remainingAttempts: Int, wrongLetters: MutableList<Char>) {
        val resId = when (remainingAttempts) {
                        5 -> R.drawable.ico_gallow_head
                        4 -> R.drawable.ico_gallow_torso
                        3 -> R.drawable.ico_gallow_rarm
                        2 -> R.drawable.ico_gallow_larm
                        1 -> R.drawable.ico_gallow_rleg
                        else -> R.drawable.ico_gallow_head
                    }
        gallowsImage?.setImageResource(resId)
        gallowsWrongLetters?.text = formatWrongLetter(wrongLetters)
    }

    override fun onAlreadyMentionedLetter(letter: Char) {
        val text = String.format(resources.getString(R.string.already_used_letter), letter)
        Snackbar.make(activityContainer as View, text, Snackbar.LENGTH_LONG).show()
    }

    override fun onGameWin(word: String, newScore: Int) {
        gallowsRecognizer?.shouldRecognizeLettersOrWord = false
        gallowsWord?.text = word
        gallowsGuide?.text = resources.getString(R.string.say_start_again)
        gallowsImage?.setImageResource(R.drawable.ico_gallow_win)
    }

    override fun onGameLose(word: String, wrongLetters: MutableList<Char>, lostByTime: Boolean, newScore: Int) {
        gallowsRecognizer?.shouldRecognizeLettersOrWord = false
        gallowsWord?.text = word
        gallowsGuide?.text = resources.getString(R.string.say_start_again)
        gallowsWrongLetters?.text = formatWrongLetter(wrongLetters)
        gallowsImage?.setImageResource(R.drawable.ico_gallow_body)

        if (lostByTime) {
            setTimerTextAndColor(0)
        }
    }

    override fun onTimerTick(remainingSeconds: Long) {
        setTimerTextAndColor(remainingSeconds)
    }
    // endregion

    //region private
    private fun formatWrongLetter(wrongLetters: MutableList<Char>): String {
        var formattedLetters = ""
        wrongLetters.forEach { char -> formattedLetters += if (formattedLetters.isBlank()) char else ", " + char }
        return formattedLetters
    }

    private fun setTimerTextAndColor(remainingSeconds: Long) {
        val totalSeconds = GallowsController.SECONDS_TO_PLAY
        gallowsTimer?.text = remainingSeconds.toString()
        // Extracting the colorId on a val and setting later when using "when" is not working
        // for a reason that goes beyond my comprehension
        // The code bellow doesn't work either
        //        when (remainingSeconds) {
        //            in totalSeconds..(totalSeconds * 0.75).toInt() ->
        //            in ((totalSeconds * 0.75).toInt() - 1)..(totalSeconds * 0.5).toInt() -> gallowsTimer?.setTextColor(ContextCompat.getColor(this, R.color.color_alert))
        //            in ((totalSeconds * 0.5).toInt() - 1)..(totalSeconds * 0.25).toInt() -> gallowsTimer?.setTextColor(ContextCompat.getColor(this, R.color.color_alert_2))
        //            in ((totalSeconds * 0.25).toInt() - 1)..0 -> gallowsTimer?.setTextColor(ContextCompat.getColor(this, R.color.color_danger))
        //        }
        val colorId = if (totalSeconds >= remainingSeconds && remainingSeconds >= (totalSeconds * 0.75).toInt()) {
                        R.color.white
                    } else if (((totalSeconds * 0.75).toInt() - 1) >= remainingSeconds && remainingSeconds >= (totalSeconds * 0.5).toInt()) {
                        R.color.color_alert
                    } else if (((totalSeconds * 0.5).toInt() - 1) >= remainingSeconds && remainingSeconds >= (totalSeconds * 0.25).toInt()) {
                        R.color.color_alert_2
                    } else if (((totalSeconds * 0.25).toInt() - 1) >= remainingSeconds && remainingSeconds >= 0) {
                        R.color.color_danger
                    } else {
                        R.color.white
                    }

        gallowsTimer?.setTextColor(ContextCompat.getColor(this, colorId))
    }
    //endregion
}