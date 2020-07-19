package se.bylenny.quiz.views.start

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import se.bylenny.quiz.views.quiz.QuizRepository

class StartViewModel @ViewModelInject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {
    fun onStartQuizClicked() {
        quizRepository.startQuiz()
    }
}
