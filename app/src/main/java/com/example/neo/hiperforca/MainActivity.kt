package com.example.neo.hiperforca

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.TextView
import com.example.neo.hiperforca.core.SpeechToText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), SpeechToText.Listener {
    // https://www.androidhive.info/2014/07/android-speech-to-text-tutorial/
    private var speechInputText: TextView? = null
    private var speechButton: ImageButton? = null
    private var speechToText: SpeechToText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speechToText = SpeechToText(this, this)
        speechInputText = activity_main_text
        speechButton = activity_main_speak_button


        // hide the action bar
        actionBar?.hide()
        speechButton?.setOnClickListener { speechToText?.listen() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        speechToText?.destroy()
        speechToText = null
    }

    override fun onTextChanged(text: String) {
        speechInputText?.text = text
    }
}