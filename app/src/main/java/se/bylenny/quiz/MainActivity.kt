package se.bylenny.quiz

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import se.bylenny.quiz.AppRepository.AppState.Quiz
import se.bylenny.quiz.AppRepository.AppState.Start
import se.bylenny.quiz.extensions.observe
import se.bylenny.quiz.views.quiz.QuizFragment
import se.bylenny.quiz.views.start.StartFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.hide()
        observe(viewModel.appRepository.appState) { state ->
            setState(state)
        }
    }

    private fun setState(newState: AppRepository.AppState) {
        val tag = newState.name
        val fragment: Fragment = when (newState) {
            Start -> StartFragment.newInstance()
            Quiz -> QuizFragment.newInstance()
        }
        switchFragment(tag, fragment)
    }

    private fun switchFragment(tag: String, fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentFragment, fragment, tag)
            .commitNow()
    }

    override fun onBackPressed() {
        if (!viewModel.onBackPressed()) {
            super.onBackPressed()
        }
    }

}