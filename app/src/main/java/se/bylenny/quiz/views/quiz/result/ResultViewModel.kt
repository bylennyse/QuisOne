package se.bylenny.quiz.views.quiz.result

import android.text.SpannableStringBuilder
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import se.bylenny.quiz.R
import se.bylenny.quiz.adapter.RecyclableItem
import se.bylenny.quiz.data.Result
import se.bylenny.quiz.extensions.Style
import se.bylenny.quiz.extensions.append
import se.bylenny.quiz.extensions.appendLineBreak
import se.bylenny.quiz.views.quiz.QuizRepository
import java.util.concurrent.TimeUnit

class ResultViewModel(
    private val quizRepository: QuizRepository
) : RecyclableItem(R.layout.result) {
    val text: LiveData<CharSequence> = quizRepository.result.map { result ->
        SpannableStringBuilder().let { builder ->
            builder
                .append("You answered", Style.Muted, Style.Large)
                .appendLineBreak()
                .append(
                    "${result.correctAnswers} / ${result.answers.size}",
                    Style.Bold,
                    Style.Italic,
                    Style.Large
                )
                .appendLineBreak()
                .append("correct", Style.Muted, Style.Large)
                .appendLineBreak()
                .appendLineBreak()
                .appendLineBreak()
                .append("It took you an average of", Style.Muted)
                .appendLineBreak()
                .append(
                    "${TimeUnit.MILLISECONDS.toSeconds(result.responseTime)} seconds",
                    Style.Large,
                    Style.Highlighted
                )
                .appendLineBreak()
                .append("to answer a question", Style.Muted)
        }
    }

    fun onContinueClicked() {
        quizRepository.stopQuiz()
    }

    private val Result.correctAnswers: Int
        get() = answers.count { it.question.correctAnswer == it.option }

    private val Result.responseTime: Long
        get() = answers.sumByDouble { it.time.toDouble() }.div(answers.size).toLong()
}