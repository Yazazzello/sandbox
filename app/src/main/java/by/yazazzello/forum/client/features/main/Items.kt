package by.yazazzello.forum.client.features.main

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.data.models.ForumCategory
import by.yazazzello.forum.client.databinding.ItemForumTopicBinding
import by.yazazzello.forum.client.databinding.ItemHeaderBinding
import by.yazazzello.forum.client.features.BasicItemVH
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.views.decorations.DecorateWithBottomLine

/**
 * Created by yazazzello on 10/3/17.
 */
class HeaderItemVH (val item: ItemHeaderBinding) : BasicItemVH(item.root) {

    override fun bind(bindable: Bindable<*>) {
        item.title = (bindable.model as? String)
        item.executePendingBindings()
    }
}

class ForumItemVH (val item: ItemForumTopicBinding,
                   private var itemClicked: (item: Bindable<*>?) -> Unit)
    : BasicItemVH(item.root), DecorateWithBottomLine {

    override fun bind(bindable: Bindable<*>) {
        val forumCategory = bindable.model as? ForumCategory
        item.forum = forumCategory
        item.executePendingBindings()
        item.root.setOnClickListener { itemClicked.invoke(bindable) }
    }
}

object MainVhFactory {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified VH> createVh(parent: ViewGroup, noinline itemClicked: (item: Bindable<*>?) -> Unit): BasicItemVH {
        return when (VH::class) {
            HeaderItemVH::class -> {
                val binding = DataBindingUtil.inflate<ItemHeaderBinding>(LayoutInflater.from(parent.context),
                        R.layout.item_header, parent, false)
                 HeaderItemVH(binding)
            }
            ForumItemVH::class -> {
                val binding = DataBindingUtil.inflate<ItemForumTopicBinding>(LayoutInflater.from(parent.context),
                        R.layout.item_forum_topic, parent, false)
                ForumItemVH(binding, itemClicked)
                
            }
            else -> throw IllegalArgumentException("unknown class " + VH::class)
        }
    }
}