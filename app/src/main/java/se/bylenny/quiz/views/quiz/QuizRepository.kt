package se.bylenny.quiz.views.quiz

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
    private val vibrator: Vibrator,
    private val timeKeeper: TimeKeeper
) {
    companion object {
        private const val TIMEOUT_MS: Long = 10 * 1000L
        private const val UPDATE_INTERVAL_MS: Long = 50L
        private const val LIFELINE_TIME_MS: Long = 10 * 1000L
        private const val VIBRATING_SECONDS: Int = 3
        private val SHOW_ALL = listOf(true, true, true, true)
    }

    private val disposables = CompositeDisposable()

    private var answers: MutableList<Answer> = mutableListOf()
    private var startTime: Long = 0
    private var endTime: Long = 0

    private val replaceQuestion: MutableLiveData<Boolean> = MutableLiveData(true)
    private val moreTime: MutableLiveData<Boolean> = MutableLiveData(true)
    private val removeTwo: MutableLiveData<Boolean> = MutableLiveData(true)

    private val alternativesVisibility: MutableLiveData<List<Boolean>> = MutableLiveData(SHOW_ALL)

    val question: MutableLiveData<Int> = MutableLiveData(0)

    val timeLeft: MutableLiveData<Long> = MutableLiveData(0)

    val pages: MutableLiveData<List<Question>> = MutableLiveData()

    private var questions: List<Question> = emptyList()
        set(value) {
            field = value
            pages.value = value
        }

    val isAlternativesEnabled: LiveData<List<Boolean>> = alternativesVisibility

    val hasLifeLineReplaceQuestion: LiveData<Boolean> = replaceQuestion
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

    fun useLifeLineReplaceQuestion(question: Question) {
        replaceQuestion.value = false
        replaceQuestion(question)
    }

    val result: MutableLiveData<Result> = MutableLiveData()

    fun answer(question: Question, answer: Int?) {
        answers.add(
            Answer(
                question = question,
                answered = answer,
                isCorrect = question.correctAnswer == answer,
                timeMs = timeKeeper.getTime() - startTime
            )
        )
        val page = question.number
        if (page == questions.size) {
            stopTimer()
            result.postValue(Result(questions, answers))
        } else {
            startTime = timeKeeper.getTime()
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
        stopTimer()
        appRepository.stopQuiz()
        answers = mutableListOf()
        alternativesVisibility.value = SHOW_ALL
        moreTime.value = true
        removeTwo.value = true
        replaceQuestion.value = true
        question.value = 0
    }

    fun getPageNumberText(question: Question): String {
        val page = question.number
        val pages = questions.size
        return "$page/$pages"
    }

    private fun replaceQuestion(question: Question) {
        val index: Int = questions.indexOf(question)
        val newQuestions = questions.toMutableList()
        val excludeIds = questions.map { it.id }
        newQuestions.removeAt(index)
        val newQuestion = questionRepository.getQuestions(1, excludeIds).first()
        newQuestions.add(index, newQuestion)
        questions = newQuestions
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
        val currentTime = timeKeeper.getTime()
        val timeLeftMs = max(endTime - currentTime, 0)
        timeLeft.postValue(timeLeftMs)
        if (timeLeftMs <= 0L) {
            answer(questions[question.value!!], null)
        }
    }

    private fun startTimer() {
        startTime = timeKeeper.getTime()
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
        TimeUnit.MILLISECONDS.toSeconds(endTime - timeKeeper.getTime())

    private fun stopTimer() {
        disposables.clear()
    }

    private val Question.number: Int
        get() = questions.indexOf(this) + 1
}
