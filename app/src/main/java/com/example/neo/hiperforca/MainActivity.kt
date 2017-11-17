package com.example.neo.hiperforca

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.TextView
import com.example.neo.hiperforca.core.SpeechRecognizerService
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

class MainActivity : Activity(), SpeechRecognizerService.Listener {
    // https://www.androidhive.info/2014/07/android-speech-to-text-tutorial/
    private var speechInputText: TextView? = null
    private var speechButton: ImageButton? = null
    private var speechRecognizerService: SpeechRecognizerService? = null
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speechRecognizerService = SpeechRecognizerService(this, this)
        speechInputText = activity_main_text
        speechButton = activity_main_speak_button

        // hide the action bar
        actionBar?.hide()
        speechButton?.setOnClickListener { speechRecognizerService?.listen() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizerService?.destroy()
        speechRecognizerService = null
    }

    override fun onTextChanged(text: String) {
        speechInputText?.text = text
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
                    speechRecognizerService?.listen()
                } else {
                    speechInputText?.text = "Sem permissão!"
                }
            }
        }
    }
}