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

class MainActivity : Activity(), GallowsRecognizer.Listener {
    // https://www.androidhive.info/2014/07/android-speech-to-text-tutorial/
    private var speechInputText: TextView? = null
    private var speechButton: ImageButton? = null
    private var activityContainer: RelativeLayout? = null
    private var gallowsRecognizer: GallowsRecognizer? = null
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gallowsRecognizer = GallowsRecognizer(this, this)
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

    override fun onSpeechRecognized(text: String) {
        speechInputText?.text = text
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
}