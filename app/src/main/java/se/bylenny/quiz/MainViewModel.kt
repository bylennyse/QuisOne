package se.bylenny.quiz

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import se.bylenny.quiz.views.quiz.QuizRepository

class MainViewModel @ViewModelInject constructor(
    private val quizRepository: QuizRepository,
    val appRepository: AppRepository
) : ViewModel() {
    fun onBackPressed() {
        quizRepository.stopQuiz()
    }

    fun onPause() {
        quizRepository.stopQuiz()
    }
}
