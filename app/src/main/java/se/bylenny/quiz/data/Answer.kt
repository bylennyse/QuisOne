package se.bylenny.quiz.data

import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    val question: Question,
    val option: Int,
    val time: Long
)