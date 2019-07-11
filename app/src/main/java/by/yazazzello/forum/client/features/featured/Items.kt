package by.yazazzello.forum.client.features.featured

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.data.models.ForumThreadSimple
import by.yazazzello.forum.client.databinding.ItemForumFeaturedBinding
import by.yazazzello.forum.client.databinding.ItemForumFeaturedPreviewBinding
import by.yazazzello.forum.client.databinding.ItemForumHotBinding
import by.yazazzello.forum.client.features.BasicItemVH
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.views.decorations.DecorateWithBottomLine
import com.squareup.picasso.Picasso

/**
 * Created by yazazzello on 10/3/17.
 */
class ForumThreadSimpleVH constructor(val item: ItemForumHotBinding,
                                      private var itemClicked: ((item: Bindable<*>?) -> Unit)? = null)
    : BasicItemVH(item.root), DecorateWithBottomLine {

    override fun bind(bindable: Bindable<*>) {
        item.viewModel = (bindable.model as? ForumThreadSimple)
        item.executePendingBindings()
        item.root.setOnClickListener { itemClicked?.invoke(bindable) }
    }
}

class ForumThreadFeaturedVH constructor(val picasso: Picasso, val item: ItemForumFeaturedBinding,
                                        private var itemClicked: ((item: Bindable<*>?) -> Unit)? = null)
    : BasicItemVH(item.root), DecorateWithBottomLine {

    override fun bind(bindable: Bindable<*>) {
        item.viewModel = (bindable.model as? ForumThreadSimple)
        item.picasso = picasso
        item.executePendingBindings()
        item.root.setOnClickListener { itemClicked?.invoke(bindable) }
    }
}

class ForumThreadFeaturedPreviewVH constructor(val picasso: Picasso, val item: ItemForumFeaturedPreviewBinding,
                                               private var itemClicked: ((item: Bindable<*>?) -> Unit)? = null)
    : BasicItemVH(item.root), DecorateWithBottomLine {

    override fun bind(bindable: Bindable<*>) {
        item.viewModel = (bindable.model as? ForumThreadSimple)
        item.picasso = picasso
        item.executePendingBindings()
        item.root.setOnClickListener { itemClicked?.invoke(bindable) }
    }
}

object FeaturedVhFactory {

    inline fun <reified VH : BasicItemVH> createVh(picasso: Picasso, parent: ViewGroup,
                                                   noinline action: ((item: Bindable<*>?) -> Unit)? = null): BasicItemVH {
        return when (VH::class) {
            ForumThreadFeaturedVH::class -> {
                val binding = DataBindingUtil.inflate<ItemForumFeaturedBinding>(LayoutInflater.from(parent.context),
                        R.layout.item_forum_featured, parent, false)
                ForumThreadFeaturedVH(picasso, binding, action)
            }

            ForumThreadSimpleVH::class -> {
                val binding = DataBindingUtil.inflate<ItemForumHotBinding>(LayoutInflater.from(parent.context),
                        R.layout.item_forum_hot, parent, false)
                ForumThreadSimpleVH(binding, action)
            }

            ForumThreadFeaturedPreviewVH::class -> {
                val binding = DataBindingUtil.inflate<ItemForumFeaturedPreviewBinding>(LayoutInflater.from(parent.context),
                        R.layout.item_forum_featured_preview, parent, false)
                ForumThreadFeaturedPreviewVH(picasso, binding, action)
            }
            else -> throw IllegalArgumentException("unknown class " + VH::class)
        }
    }
}