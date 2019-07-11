package by.yazazzello.forum.client.features.topic

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import by.yazazzello.forum.client.data.models.ForumThread
import by.yazazzello.forum.client.features.BasicItemVH
import by.yazazzello.forum.client.features.Bindable

/**
 * Created by yazazzello on 10/3/17.
 */
class TopicAdapter(val items: MutableList<Bindable<*>>, private val itemClicked: (item: ForumThread?) -> Unit)
    : RecyclerView.Adapter<BasicItemVH>() {

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BasicItemVH, position: Int) {
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: BasicItemVH) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicItemVH {
        return when (viewType) {
            0 -> TopicVhFactory.createVh<ForumThread>(parent, itemClicked)
            1 -> TopicVhFactory.createVh<String>(parent)
            else -> throw IllegalArgumentException("unknown $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].model) {
            is ForumThread -> 0
            is String -> 1
            else -> RecyclerView.NO_POSITION
        }
    }

    fun noMoreToLoad() {
        @Suppress("UNCHECKED_CAST")
        val bindable = items.lastOrNull() as? Bindable<String>
        bindable?.model = "end of topics"
        notifyItemChanged(items.size - 1)
    }

    fun couldntLoadNextPage() {
        @Suppress("UNCHECKED_CAST")
        val bindable = items.lastOrNull() as? Bindable<String>
        bindable?.model = "failed to load next page"
        notifyItemChanged(items.size - 1)
    }

    fun addProgressIndicator() {
        items.add(Bindable("loading..."))
    }

    fun addItems(threadList: List<Bindable<*>>) {
        val index = items.size - 1
        val filteredList = threadList.minus(items)
        items.addAll(index, filteredList)
        notifyItemRangeInserted(index, filteredList.size)
    }
}