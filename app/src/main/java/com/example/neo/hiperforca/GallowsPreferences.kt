package com.example.neo.hiperforca

import android.content.Context


/**
 * Created by isabella on 26/11/17.
 */
object GallowsPreferences {
    private val SCORE_PREFERENCES = "com.example.HiperForca.SCORE_PREFERENCES"
    private val SCORE_KEY = "com.example.HiperForca.SCORE_PREFERENCES"

    fun getScore(context: Context): Int {
        val preferences = context.getSharedPreferences(SCORE_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getInt(SCORE_KEY, 0)
    }

    fun addScore(context: Context, points: Int): Int {
        val preferences = context.getSharedPreferences(SCORE_PREFERENCES, Context.MODE_PRIVATE)
        val currentScore = preferences.getInt(SCORE_KEY, 0)
        val newScore = currentScore + points
        preferences.edit().putInt(SCORE_KEY, newScore).apply()
        return newScore
    }
}