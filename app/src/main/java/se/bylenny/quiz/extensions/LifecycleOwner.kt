package se.bylenny.quiz.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: (data: T) -> Unit) {
    liveData.observe(this, Observer<T> { t -> if (t != null) observer(t) })
}