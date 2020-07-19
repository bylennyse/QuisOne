package se.bylenny.quiz.views.quiz.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import se.bylenny.quiz.R
import se.bylenny.quiz.adapter.RecyclableItem
import se.bylenny.quiz.data.Result
import se.bylenny.quiz.views.quiz.QuizRepository

class ResultViewModel(
    private val quizRepository: QuizRepository
) : RecyclableItem(R.layout.result) {

    val list: LiveData<List<RecyclableItem>> = quizRepository.result.map { result ->
        listOf(
            getTimeResult(result),
            getCorrectResult(result)
        ).plus(getAnswerList(result))
    }

    private fun getAnswerList(result: Result): List<RecyclableItem> =
        result.answers.mapIndexed { index, answer ->
            val question = answer.question
            val isUnanswered = answer.answered == null
            val givenAnswer = answer.answered?.let { question.alternatives[it] }
            val isCorrect = answer.isCorrect
            val correctAnswer = question.alternatives[question.correctAnswer]
            val text = when {
                isUnanswered -> "Not answered, correct answer is $correctAnswer"
                isCorrect -> "Correct answer; $givenAnswer"
                else -> "Incorrect answer; $givenAnswer, correct answer is $correctAnswer"
            }
            ResultTextViewModel(
                question.id,
                "Question ${index + 1}:\n${text}"
            )
        }

    private fun getCorrectResult(result: Result) = ResultTextViewModel(
        "RATIO",
        "You answered ${result.correctAnswers} / ${result.answers.size} questions correct."
    )

    private fun getTimeResult(result: Result) = ResultTextViewModel(
        "TIME",
        "It took you an average of ${"%.2f".format(result.responseTimeMs.div(1000f))} seconds to answer a question."
    )

    fun onContinueClicked() {
        quizRepository.stopQuiz()
    }

    private val Result.correctAnswers: Int
        get() = answers.count { it.question.correctAnswer == it.answered }

    private val Result.responseTimeMs: Long
        get() = answers.sumByDouble { it.timeMs.toDouble() }.div(answers.size).toLong()
}