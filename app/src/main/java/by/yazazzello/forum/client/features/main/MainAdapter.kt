package by.yazazzello.forum.client.features.main

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import by.yazazzello.forum.client.data.models.ForumCategory
import by.yazazzello.forum.client.features.BasicItemVH
import by.yazazzello.forum.client.features.Bindable

/**
 * Created by yazazzello on 10/3/17.
 */
class MainAdapter(val items: MutableList<Bindable<*>>,
                  private val itemClicked: (item: Bindable<*>?) -> Unit)
    : RecyclerView.Adapter<BasicItemVH>() {

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BasicItemVH, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicItemVH {
        return when (viewType) {
            0 -> MainVhFactory.createVh<HeaderItemVH>(parent, itemClicked)
            1 -> MainVhFactory.createVh<ForumItemVH>(parent, itemClicked)
            else -> throw IllegalStateException("unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].model) {
            is String -> 0
            is ForumCategory -> 1
            else -> RecyclerView.NO_POSITION
        }
    }
}