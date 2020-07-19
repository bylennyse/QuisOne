package se.bylenny.quiz.views.quiz

import android.os.SystemClock
import javax.inject.Inject

class TimeKeeper @Inject constructor() {
    fun getTime(): Long = SystemClock.elapsedRealtime()
}