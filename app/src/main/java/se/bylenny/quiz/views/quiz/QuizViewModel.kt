package se.bylenny.quiz.views.quiz

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import se.bylenny.quiz.adapter.RecyclableItem
import se.bylenny.quiz.data.Question
import se.bylenny.quiz.views.quiz.image.ImageViewModel
import se.bylenny.quiz.views.quiz.result.ResultViewModel
import se.bylenny.quiz.views.quiz.text.TextViewModel

class QuizViewModel @ViewModelInject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    val currentPage: LiveData<Int> = quizRepository.question

    val pages: LiveData<List<RecyclableItem>> = quizRepository.pages
        .map { questions ->
            questions
                .mapIndexed { index, question ->
                    when (question) {
                        is Question.Image -> ImageViewModel(question, quizRepository, index)
                        is Question.Text -> TextViewModel(question, quizRepository, index)
                    }
                }
                .plus(ResultViewModel(quizRepository))
        }

    override fun onCleared() {
        quizRepository.dispose()
    }
}
