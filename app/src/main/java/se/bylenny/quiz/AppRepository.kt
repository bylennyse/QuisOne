package se.bylenny.quiz

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor() {

    enum class AppState {
        Start,
        Quiz
    }

    val appState: MutableLiveData<AppState> = MutableLiveData(AppState.Start)

    fun startQuiz() {
        appState.postValue(AppState.Quiz)
    }

    fun stopQuiz() {
        appState.postValue(AppState.Start)
    }
}