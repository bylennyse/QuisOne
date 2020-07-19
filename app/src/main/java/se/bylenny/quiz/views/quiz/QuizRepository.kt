package se.bylenny.quiz.views.quiz

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import se.bylenny.quiz.AppRepository
import se.bylenny.quiz.QuestionRepository
import se.bylenny.quiz.data.Answer
import se.bylenny.quiz.data.Question
import se.bylenny.quiz.data.Result
import se.bylenny.quiz.extensions.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

// TODO Separate logic for each page
@Singleton
class QuizRepository @Inject constructor(
    private val appRepository: AppRepository,
    private val questionRepository: QuestionRepository,
    application: Application
) {
    companion object {
        private const val TIMEOUT_MS: Int = 15 * 1000
        private const val UPDATE_INTERVAL_MS: Long = 50L
        private const val LIFELINE_TIME_MS: Long = 10 * 1000L
        private const val VIBRATING_SECONDS: Int = 5
        private val SHOW_ALL = listOf(true, true, true, true)
    }

    private val vibrator = Vibrator(application)

    private val disposables = CompositeDisposable()


    private var answers: MutableList<Answer> = mutableListOf()
    private var startTime: Long = 0
    private var endTime: Long = 0

    private val moreTime: MutableLiveData<Boolean> = MutableLiveData(true)
    private val removeTwo: MutableLiveData<Boolean> = MutableLiveData(true)

    private val alternativesVisibility: MutableLiveData<List<Boolean>> = MutableLiveData(SHOW_ALL)

    val question: MutableLiveData<Int> = MutableLiveData(0)

    val timeLeft: MutableLiveData<Long> = MutableLiveData(0)

    var questions: List<Question> = emptyList()
        private set

    val isAlternativesEnabled: LiveData<List<Boolean>> = alternativesVisibility

    val hasLifeLineMoreTime: LiveData<Boolean> = moreTime
    val hasLifelineRemoveTwo: LiveData<Boolean> = removeTwo

    fun useLifeLineMoreTime() {
        moreTime.value = false
        endTime += LIFELINE_TIME_MS
    }

    fun useLifeLineRemoveTwo() {
        removeTwo.value = false
        hideTwoAlternatives()
    }

    val result: MutableLiveData<Result> = MutableLiveData()

    fun answer(question: Question, answer: Int) {
        answers.add(Answer(question, answer, getTime() - startTime))
        val page = question.number
        if (page == questions.size) {
            stopTimer()
            result.postValue(Result(answers))
        } else {
            startTime = getTime()
            endTime = startTime + TIMEOUT_MS
        }
        alternativesVisibility.postValue(SHOW_ALL)
        this.question.postValue(page)
    }

    fun startQuiz() {
        questions = questionRepository.getQuestions(count = 10)
        appRepository.startQuiz()
        startTimer()
    }

    fun stopQuiz() {
        appRepository.stopQuiz()
        answers = mutableListOf()
        alternativesVisibility.value = SHOW_ALL
        moreTime.value = true
        removeTwo.value = true
        question.value = 0
    }

    fun getPageNumberText(question: Question): String {
        val page = question.number
        val pages = questions.size
        return "$page/$pages"
    }

    fun dispose() {
        disposables.clear()
    }

    private fun hideTwoAlternatives() {
        val index = question.value ?: return
        val question = questions[index]
        val indices = question.alternatives.indices
        val hiddenIndeces = indices
            .filter { it != question.correctAnswer }
            .shuffled()
            .subList(0, 2)
        alternativesVisibility.value = indices.map { it !in hiddenIndeces }
    }

    private fun update() {
        val currentTime = getTime()
        val timeLeftMs = max(endTime - currentTime, 0)
        timeLeft.postValue(timeLeftMs)
        if (timeLeftMs <= 0L) {
            answer(questions[question.value!!], -1)
        }
    }

    private fun startTimer() {
        startTime = getTime()
        endTime = startTime + TIMEOUT_MS
        disposables += Flowable
            .interval(UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS, Schedulers.io())
            .doOnEach { update() }
            .map { getTimeLeftInSeconds() }
            .distinctUntilChanged()
            .filter { it < VIBRATING_SECONDS }
            .doOnEach { vibrator.vibrate() }
            .subscribe()

    }

    private fun getTimeLeftInSeconds() =
        TimeUnit.MILLISECONDS.toSeconds(endTime - getTime())

    private fun stopTimer() {
        disposables.clear()
    }

    private fun getTime(): Long = SystemClock.elapsedRealtime()

    private val Question.number: Int
        get() = questions.indexOf(this) + 1
}
