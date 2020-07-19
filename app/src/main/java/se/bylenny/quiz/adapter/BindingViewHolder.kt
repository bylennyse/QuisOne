package se.bylenny.quiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.OnRebindCallback
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import se.bylenny.quiz.BR

class BindingViewHolder<B : ViewDataBinding> private constructor(
    private val binding: B,
    rebindCallback: OnRebindCallback<B>
) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {

    companion object {
        fun <T : ViewDataBinding> create(
            parent: ViewGroup,
            @LayoutRes layoutId: Int,
            rebindCallback: OnRebindCallback<T>
        ): BindingViewHolder<T> =
            BindingViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    layoutId,
                    parent,
                    false
                ),
                rebindCallback
            )
    }

    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        binding.lifecycleOwner = this
        binding.addOnRebindCallback(rebindCallback)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun onRebind() {
        binding.executePendingBindings()
    }

    fun onBind(item: RecyclableItem) {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        binding.setVariable(BR.viewModel, item)
    }

    fun onUnbind() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        binding.setVariable(BR.viewModel, null)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}