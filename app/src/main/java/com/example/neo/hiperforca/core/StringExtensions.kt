package com.example.neo.hiperforca.core

import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 * Created by isabella on 01/12/17.
 */
fun String.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}