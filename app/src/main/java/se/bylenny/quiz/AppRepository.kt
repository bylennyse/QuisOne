package se.bylenny.quiz

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor() {
    companion object {
        // TODO select between different sets
        // TODO fetch sets from server
        const val QUIZ_RES = R.raw.question_set_1
    }

    enum class AppState {
        Start,
        Quiz
    }

    val appState: MutableLiveData<AppState> = MutableLiveData(AppState.Start)
    val quizRes: Int = QUIZ_RES

    fun startQuiz() {
        appState.postValue(AppState.Quiz)
    }

    fun stopQuiz() {
        appState.postValue(AppState.Start)
    }
}