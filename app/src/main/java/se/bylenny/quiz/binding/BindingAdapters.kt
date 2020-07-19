package se.bylenny.quiz.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("srcUrl")
fun loadImage(view: ImageView, srcUrl: String?) {
    srcUrl ?: return
    // TODO preload images
    Glide.with(view.context)
        .load(srcUrl)
        .into(view)
}