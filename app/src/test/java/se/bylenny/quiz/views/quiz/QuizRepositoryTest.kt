package se.bylenny.quiz.views.quiz

import android.os.Looper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import se.bylenny.quiz.AppRepository
import se.bylenny.quiz.LiveDataExtension
import se.bylenny.quiz.QuestionRepository
import se.bylenny.quiz.data.Question
import java.util.*

@ExtendWith(
    value = [
        LiveDataExtension::class
    ]
)
internal class QuizRepositoryTest {

    companion object {
        val ALTERNATIVES: List<String> = listOf("A", "B", "C", "D")
    }

    @MockK(relaxed = true)
    private lateinit var appRepository: AppRepository

    @MockK
    private lateinit var questionRepository: QuestionRepository

    @MockK(relaxed = true)
    private lateinit var vibrator: Vibrator

    @MockK
    private lateinit var timeKeeper: TimeKeeper

    @MockK
    private lateinit var mainLooper: Looper

    private val testScheduler = TestScheduler()

    private fun createClassUnderTest(): QuizRepository =
        QuizRepository(appRepository, questionRepository, vibrator, timeKeeper)

    private fun mockQuestion(correctAnswer: Int = 1): Question = mockk<Question>().also {
        every { it.id } returns UUID.randomUUID().toString()
        every { it.correctAnswer } returns correctAnswer
        every { it.alternatives } returns ALTERNATIVES
    }

    init {
        MockKAnnotations.init(this)
    }

    @Before
    fun before() {
        RxJavaPlugins.setSingleSchedulerHandler { testScheduler }
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { testScheduler }
    }

    @After
    fun after() {
        RxJavaPlugins.reset()
    }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        mockkStatic(Looper::class)
        every { timeKeeper.getTime() } returns System.currentTimeMillis()
        every { Looper.getMainLooper() } returns mainLooper
        every { mainLooper.isCurrentThread } returns true
    }

    @Test
    fun `Starting quiz gives first question of 10`() {
        // given
        val pages = (0..9).map { mockQuestion() }
        every { questionRepository.getQuestions(10, any()) } returns pages
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()

        // then
        assertEquals(0, underTest.question.value)
        underTest.stopQuiz()
    }

    @Test
    fun `Starting quiz should load 10 pages`() {
        // given
        val pages = (0..9).map { mockQuestion() }
        every { questionRepository.getQuestions(10, any()) } returns pages
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()

        // then
        assertEquals(pages, underTest.pages.value)
        underTest.stopQuiz()
    }

    @Test
    fun `Starting quiz should have all lifelines`() {
        // given
        val pages = (0..9).map { mockQuestion() }
        every { questionRepository.getQuestions(10, any()) } returns pages
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()

        // then
        assertEquals(true, underTest.hasLifeLineReplaceQuestion.value)
        assertEquals(true, underTest.hasLifeLineMoreTime.value)
        assertEquals(true, underTest.hasLifelineRemoveTwo.value)
        underTest.stopQuiz()
    }

    @Test
    fun `getPageNumberText should give the correct text`() {
        // given
        val pages = (0..9).map { mockQuestion() }
        every { questionRepository.getQuestions(10, any()) } returns pages
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()
        val text = underTest.getPageNumberText(pages.first())

        // then
        assertEquals("1/10", text)
        underTest.stopQuiz()
    }

    @Test
    fun `Using lifeline replace question should replace the current question`() {
        // given
        val pagesBefore = (0..9).map { mockQuestion() }
        val newPage: List<Question> = listOf(mockQuestion())
        val pagesAfter = newPage + pagesBefore.subList(1, 10)
        every { questionRepository.getQuestions(10, any()) } returns pagesBefore
        every { questionRepository.getQuestions(1, any()) } returns newPage
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()
        underTest.useLifeLineReplaceQuestion(pagesBefore.first())

        // then
        assertEquals(pagesAfter, underTest.pages.value)
        underTest.stopQuiz()
    }

    @Test
    fun `Using lifeline replace question should disable that lifeline`() {
        // given
        val pagesBefore = (0..9).map { mockQuestion() }
        val newPage: List<Question> = listOf(mockQuestion())
        val pagesAfter = newPage + pagesBefore.subList(1, 10)
        every { questionRepository.getQuestions(10, any()) } returns pagesBefore
        every { questionRepository.getQuestions(1, any()) } returns newPage
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()
        underTest.useLifeLineReplaceQuestion(pagesBefore.first())

        // then
        assertEquals(false, underTest.hasLifeLineReplaceQuestion.value)
        underTest.stopQuiz()
    }

    @Test
    fun `Using lifeline more time should disable that lifeline`() {
        // given
        val pages = (0..9).map { mockQuestion() }
        every { questionRepository.getQuestions(10, any()) } returns pages
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()
        underTest.useLifeLineMoreTime()

        // then
        assertEquals(false, underTest.hasLifeLineMoreTime.value)
        underTest.stopQuiz()
    }

    @Test
    fun `Using lifeline remove two should disable that lifeline`() {
        // given
        val pages = (0..9).map { mockQuestion() }
        every { questionRepository.getQuestions(10, any()) } returns pages
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()
        underTest.useLifeLineRemoveTwo()

        // then
        assertEquals(false, underTest.hasLifelineRemoveTwo.value)
        underTest.stopQuiz()
    }

    @Test
    fun `Using lifeline remove two should disable two alternatives`() {
        // given
        val pages = (0..9).map { mockQuestion() }
        every { questionRepository.getQuestions(10, any()) } returns pages
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()
        underTest.useLifeLineRemoveTwo()

        // then
        assertEquals(2, underTest.isAlternativesEnabled.value?.count { it })
        underTest.stopQuiz()
    }

    @Test
    fun `Using lifeline remove two should not disable the answer`() {
        // given
        val pages = (0..9).map { mockQuestion() }
        val page = pages.first()
        val correctAnswer = 3
        every { page.correctAnswer } returns correctAnswer
        every { questionRepository.getQuestions(10, any()) } returns pages
        val underTest = createClassUnderTest()

        // when
        underTest.startQuiz()
        underTest.useLifeLineRemoveTwo()

        // then
        assertEquals(true, underTest.isAlternativesEnabled.value?.get(correctAnswer))
        underTest.stopQuiz()
    }

    //TODO test consecutive starts
    //TODO test time runs out
    //TODO test more time increment
    //TODO test timer
}