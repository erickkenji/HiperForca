package com.example.neo.hiperforca.core

import android.content.Context


/**
 * Created by isabella on 26/11/17.
 */
object Preferences {
    private val GALLOWS_PREFERENCES = "com.example.HiperForca.GALLOWS_PREFERENCES"
    private val SCORE_KEY = "SCORE_KEY"
    private val FIRST_ACCESS_KEY = "FIRST_ACCESS_KEY"

    fun getScore(context: Context): Int {
        val preferences = context.getSharedPreferences(GALLOWS_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getInt(SCORE_KEY, 0)
    }

    fun addScore(context: Context, points: Int): Int {
        val preferences = context.getSharedPreferences(GALLOWS_PREFERENCES, Context.MODE_PRIVATE)
        val currentScore = preferences.getInt(SCORE_KEY, 0)
        val newScore = currentScore + points
        preferences.edit().putInt(SCORE_KEY, newScore).apply()
        return newScore
    }

    fun isFirstAccess(context: Context): Boolean {
        val preferences = context.getSharedPreferences(GALLOWS_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getBoolean(FIRST_ACCESS_KEY, true)
    }

    fun saveUserFirstAccess(context: Context) {
        val preferences = context.getSharedPreferences(GALLOWS_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(FIRST_ACCESS_KEY, false).apply()
    }
}