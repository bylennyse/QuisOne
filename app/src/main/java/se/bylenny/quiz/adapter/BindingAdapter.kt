package se.bylenny.quiz.adapter

import android.view.ViewGroup
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.databinding.OnRebindCallback
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BindingAdapter(
    items: List<RecyclableItem> = emptyList()
) : RecyclerView.Adapter<BindingViewHolder<ViewDataBinding>>() {

    companion object {
        private val LOADED_MARKER = Any()
    }

    private var listItems: List<RecyclableItem> = items.toList()

    private val rebindCallback = object : OnRebindCallback<ViewDataBinding>() {
        override fun onPreBind(binding: ViewDataBinding?): Boolean {
            val recyclerView = (binding?.root?.parent as? RecyclerView) ?: return true

            if (recyclerView.isComputingLayout) {
                return true
            }

            val childAdapterPosition = recyclerView.getChildAdapterPosition(binding.root)
            if (childAdapterPosition == RecyclerView.NO_POSITION) {
                return true
            }

            notifyItemChanged(
                childAdapterPosition,
                LOADED_MARKER
            )
            return false
        }
    }

    @MainThread
    fun updateItems(items: List<RecyclableItem>, diff: DiffUtil.DiffResult) {
        listItems = items.toList()
        diff.dispatchUpdatesTo(this)
    }

    @MainThread
    fun updateItems(items: List<RecyclableItem>) =
        updateItems(items, calculateDiff(items))

    @AnyThread
    fun calculateDiff(items: List<RecyclableItem>): DiffUtil.DiffResult =
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                listItems[oldItemPosition].getItemId() == items[newItemPosition].getItemId()

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                listItems[oldItemPosition].getDataId() == items[newItemPosition].getDataId()

            override fun getOldListSize(): Int = listItems.size
            override fun getNewListSize(): Int = items.size
        })

    override fun getItemCount(): Int = listItems.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ViewDataBinding> =
        BindingViewHolder.create(parent, viewType, rebindCallback)

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) = Unit

    override fun onBindViewHolder(
        holder: BindingViewHolder<ViewDataBinding>,
        position: Int,
        payloads: List<Any>
    ) {
        if (!payloads.contains(LOADED_MARKER)) {
            holder.onBind(listItems[position])
        } else {
            holder.onRebind()
        }
    }

    override fun onViewRecycled(holder: BindingViewHolder<ViewDataBinding>) {
        super.onViewRecycled(holder)
        holder.onUnbind()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        when (val layoutManager = recyclerView.layoutManager!!) {
            is LinearLayoutManager -> layoutManager.recycleChildrenOnDetach = true
            else -> throw IllegalStateException("${layoutManager::class} is not supported")
        }
    }

    override fun getItemViewType(position: Int): Int = listItems[position].layoutResId

}
