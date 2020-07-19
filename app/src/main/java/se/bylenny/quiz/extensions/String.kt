package se.bylenny.quiz.extensions

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

@UnstableDefault
inline fun <reified T : Any> String.parseJson(): T {
    @OptIn(ImplicitReflectionSerializer::class)
    return Json.fromJson(Json.parseJson(this))
}