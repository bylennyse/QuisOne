package se.bylenny.quiz.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Question {
    abstract val id: String
    abstract val correctAnswer: Int
    abstract val alternatives: List<String>

    @Serializable
    @SerialName("image")
    data class Image(
        override val id: String,
        override val correctAnswer: Int,
        override val alternatives: List<String>,
        val image: String
    ) : Question()

    @Serializable
    @SerialName("text")
    data class Text(
        override val id: String,
        override val correctAnswer: Int,
        override val alternatives: List<String>,
        val text: String
    ) : Question()

}