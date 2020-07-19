package se.bylenny.quiz.data

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val questions: List<Question>
)

