package se.bylenny.quiz

import android.app.Application
import se.bylenny.quiz.data.Question
import se.bylenny.quiz.data.Quiz
import se.bylenny.quiz.extensions.parseJson
import se.bylenny.quiz.extensions.readRaw
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val application: Application
) {
    companion object {
        const val QUIZ_RES = R.raw.question_set_1
    }

    // TODO  fetch sets from server or database
    private fun loadQuestions(): List<Question> =
        QUIZ_RES.readRaw(application).parseJson<Quiz>().questions.shuffled()

    // TODO Make optimized loading that only loads partial set
    fun getQuestions(count: Int, excludeIds: List<String> = emptyList()): List<Question> =
        loadQuestions().shuffled().filterNot { it.id in excludeIds }.subList(0, count)
}