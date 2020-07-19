package se.bylenny.quiz.data

import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    val question: Question,
    val answered: Int?, // TODO Make more verbose
    val isCorrect: Boolean,
    val timeMs: Long
)