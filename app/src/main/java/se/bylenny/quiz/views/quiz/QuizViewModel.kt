package se.bylenny.quiz.views.quiz

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import se.bylenny.quiz.adapter.RecyclableItem
import se.bylenny.quiz.data.Question
import se.bylenny.quiz.views.quiz.image.ImageViewModel
import se.bylenny.quiz.views.quiz.result.ResultViewModel
import se.bylenny.quiz.views.quiz.text.TextViewModel

class QuizViewModel @ViewModelInject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    val quizPage: LiveData<Int> = quizRepository.question

    val pages: List<RecyclableItem> = quizRepository.questions
        .map { question ->
            when (question) {
                is Question.Image -> ImageViewModel(question, quizRepository)
                is Question.Text -> TextViewModel(question, quizRepository)
            }
        }
        .plus(ResultViewModel(quizRepository))

    override fun onCleared() {
        quizRepository.dispose()
    }
}
