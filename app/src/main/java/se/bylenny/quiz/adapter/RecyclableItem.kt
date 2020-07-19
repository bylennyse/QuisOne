package se.bylenny.quiz.adapter

import androidx.annotation.LayoutRes

open class RecyclableItem(
    @LayoutRes
    val layoutResId: Int
) {
    open fun getItemId(): Any = this::class
    open fun getDataId(): Any = layoutResId
}