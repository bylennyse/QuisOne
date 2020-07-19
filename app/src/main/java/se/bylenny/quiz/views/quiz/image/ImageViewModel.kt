package se.bylenny.quiz.views.quiz.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import se.bylenny.quiz.R
import se.bylenny.quiz.adapter.RecyclableItem
import se.bylenny.quiz.data.Question
import se.bylenny.quiz.views.quiz.QuizRepository
import java.util.concurrent.TimeUnit

class ImageViewModel(
    val question: Question.Image,
    private val quizRepository: QuizRepository
) : RecyclableItem(R.layout.image_question) {

    override fun getItemId(): Any = question.id

    val timeLeft: LiveData<String> = quizRepository.timeLeft.map { timeLeftMs ->
        "${TimeUnit.MILLISECONDS.toSeconds(timeLeftMs) + 1}s"
    }

    val interactionEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    val pageNumberText = quizRepository.getPageNumberText(question)

    val moreTimeEnabled: LiveData<Boolean> = quizRepository.hasLifeLineMoreTime
    val removeTwoEnabled: LiveData<Boolean> = quizRepository.hasLifelineRemoveTwo

    val isAlternativeEnabled: LiveData<List<Boolean>> = quizRepository.isAlternativesEnabled

    fun onMoreTimeClicked() {
        quizRepository.useLifeLineMoreTime()
    }

    fun onRemoveTwoClicked() {
        quizRepository.useLifeLineRemoveTwo()
    }

    fun onAnswerClicked(answer: Int) {
        interactionEnabled.value = false
        quizRepository.answer(question, answer)
    }
}