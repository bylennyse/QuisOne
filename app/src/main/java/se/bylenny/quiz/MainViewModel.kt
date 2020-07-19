package se.bylenny.quiz

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import javax.inject.Singleton

class MainViewModel @ViewModelInject constructor(
    @Singleton val appRepository: AppRepository
) : ViewModel() {
    fun onBackPressed(): Boolean =
        (appRepository.appState == AppRepository.AppState.Quiz).also { handle ->
            if (handle) appRepository.stopQuiz()
        }
}
