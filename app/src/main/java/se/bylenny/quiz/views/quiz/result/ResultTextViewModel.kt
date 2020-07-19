package se.bylenny.quiz.views.quiz.result

import se.bylenny.quiz.R
import se.bylenny.quiz.adapter.RecyclableItem

class ResultTextViewModel(
    val id: String,
    val text: CharSequence
) : RecyclableItem(R.layout.result_text) {
    override fun getItemId(): Any = id
    override fun getDataId(): Any = text
}