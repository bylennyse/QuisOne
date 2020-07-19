package se.bylenny.quiz.data

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val name: String,
    val questions: List<Question>
)

