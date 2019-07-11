package by.yazazzello.forum.client.features.thread

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import by.yazazzello.forum.client.data.models.MsgPost
import by.yazazzello.forum.client.features.BasicItemVH
import by.yazazzello.forum.client.features.Bindable
import com.squareup.picasso.Picasso
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_msg_post.view.*


/**
 * Created by yazazzello on 5/7/17.
 * thread adapter
 */
class ThreadAdapter(val picasso: Picasso,
                    val scrollSubject: PublishSubject<Int>)
    : RecyclerView.Adapter<BasicItemVH>() {

    var messages: MutableList<Bindable<*>> = mutableListOf()
    
    var reloadFunc: ((lastPostId: Int) -> Unit)? = null

    fun addItemsToHead(itemsToAdd: MutableList<Bindable<MsgPost>>) {
        messages.addAll(0, itemsToAdd)
        notifyItemRangeInserted(0, itemsToAdd.size)
    }

    fun addItems(newItems: List<Bindable<*>>) {
        var index = 0
        if (messages.isEmpty()) {
            messages.add(Bindable(ProgressableItem(isLoading = true)))
        } else {
            index = messages.size - 1
        }
        messages.addAll(index, newItems)
        notifyItemRangeInserted(index, newItems.size)
    }

    fun clear() {
        messages.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicItemVH {
        return when (viewType) {
            1 -> ThreadAdapterVh.createVh<MsgItemVH>(parent, this)
            2 -> ThreadAdapterVh.createVh<ProgressItemVH>(parent, this)
            else -> throw IllegalArgumentException("unknown $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].model) {
            is MsgPost -> 1
            is ProgressableItem -> 2
            else -> 0
        }
    }

    override fun onViewRecycled(holder: BasicItemVH) {
        (holder as? MsgItemVH)?.let {
            it.itemView.html_text.stopLoading()
            it.itemView.html_text.loadData("", "", "")
            it.disposable?.dispose()
        }
    }

    fun setShowProgress(shouldShow: Boolean) {
        (messages.lastOrNull()?.model
                as? ProgressableItem)?.isLoading = shouldShow

        notifyItemChanged(messages.size - 1)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: BasicItemVH, position: Int) {
        holder.bind(messages[position])
    }

    fun getItem(firstVisPosition: Int): MsgPost? = (messages.getOrNull(firstVisPosition))?.model as? MsgPost

    fun invalidateLastItem() {
        notifyItemChanged(messages.size - 1)
    }
}