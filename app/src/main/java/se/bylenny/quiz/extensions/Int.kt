package se.bylenny.quiz.extensions

import android.content.Context

fun Int.readRaw(context: Context): String =
    context.resources.openRawResource(this).bufferedReader().readText()
