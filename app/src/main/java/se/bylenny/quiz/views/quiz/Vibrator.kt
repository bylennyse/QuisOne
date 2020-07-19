package se.bylenny.quiz.views.quiz

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import javax.inject.Inject

class Vibrator @Inject constructor(
    private val application: Application
) {
    fun vibrate(timeMs: Long = 200L) {
        val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    timeMs,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(timeMs)
        }
    }
}