package se.bylenny.quiz.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import se.bylenny.quiz.adapter.PageBindingAdapter
import se.bylenny.quiz.adapter.RecyclableItem

@BindingAdapter("srcUrl")
fun loadImage(view: ImageView, srcUrl: String?) {
    srcUrl ?: return
    // TODO preload images
    Glide.with(view.context)
        .load(srcUrl)
        .into(view)
}

@BindingAdapter("page")
fun pagerPage(view: ViewPager2, page: Int?) {
    view.currentItem = page ?: return
}

@BindingAdapter("pages")
fun pagerPages(view: ViewPager2, pages: List<RecyclableItem>?) {
    pages ?: return
    when (val adapter = view.adapter) {
        null -> view.adapter = PageBindingAdapter(pages)
        is PageBindingAdapter -> adapter.updateItems(pages)
        else -> throw IllegalStateException("Wrong adapter")
    }
}

@BindingAdapter("list")
fun recyclerViewList(view: RecyclerView, list: List<RecyclableItem>?) {
    list ?: return
    when (val adapter = view.adapter) {
        null -> view.adapter = PageBindingAdapter(list)
        is PageBindingAdapter -> adapter.updateItems(list)
        else -> throw IllegalStateException("Wrong adapter")
    }
}