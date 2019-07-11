package by.yazazzello.forum.client.features.topic

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.data.models.ForumThread
import by.yazazzello.forum.client.data.models.PageHolder
import by.yazazzello.forum.client.databinding.ItemForumThreadBinding
import by.yazazzello.forum.client.databinding.ItemLoadingBinding
import by.yazazzello.forum.client.features.BasicItemVH
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.views.decorations.DecorateWithBottomLine

class ForumThreadItemVH(val item: ItemForumThreadBinding) : BasicItemVH(item.root),
        DecorateWithBottomLine {

    lateinit var itemClicked: (item: ForumThread?) -> Unit

    override fun bind(bindable: Bindable<*>) {
        item.forumThread = bindable.model as? ForumThread
        item.pageClickListener  = object: OnPageClickedListener{
            override fun onPageClick(page: PageHolder) {
                item.forumThread?.pageHolderClicked = page
                itemClicked.invoke(item.forumThread)
            }
        }
        item.executePendingBindings()
        item.root.setOnClickListener { itemClicked.invoke(item.forumThread) }
    }

    override fun recycle() {
        item.pages.removeAllViews()
    }
}

class ProgressItemVH constructor(val item: ItemLoadingBinding) : BasicItemVH(item.root) {

    override fun bind(bindable: Bindable<*>) {
        item.title = (bindable.model as? String)
        item.executePendingBindings()
    }
}

object TopicVhFactory {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified MODEL> createVh(parent: ViewGroup, noinline action: ((item: MODEL?) -> Unit)? = null): BasicItemVH {
        when (MODEL::class) {
            String::class -> {
                val binding = DataBindingUtil.inflate<ItemLoadingBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_loading, parent, false
                )
                return ProgressItemVH(binding)
            }
            ForumThread::class -> {
                val binding = DataBindingUtil.inflate<ItemForumThreadBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_forum_thread, parent, false
                )
                return ForumThreadItemVH(binding).also {
                    it.itemClicked = action as (item: ForumThread?) -> Unit
                }
            }
            else -> throw IllegalArgumentException("unknown ${MODEL::class}")
        }
    }
}