package com.example.neo.hiperforca

import android.os.CountDownTimer

/**
 * Created by isabella on 26/11/17.
 */
class GallowsTimer(millisInFuture: Long, countDownInterval: Long, val listener: Listener) : CountDownTimer(millisInFuture, countDownInterval) {
    interface Listener {
        fun onTimerTick(remainingSeconds: Long)
        fun onTimerFinished()
    }

    override fun onTick(millisUntilFinished: Long) {
        listener.onTimerTick(millisUntilFinished / 1000)
    }

    override fun onFinish() {
        listener.onTimerFinished()
    }
}