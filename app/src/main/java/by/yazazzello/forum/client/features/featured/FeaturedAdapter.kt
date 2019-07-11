package by.yazazzello.forum.client.features.featured

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import by.yazazzello.forum.client.data.models.ForumThreadSimple
import by.yazazzello.forum.client.data.models.ForumThreadSimple.Companion.IMAGE_AND_TEXT
import by.yazazzello.forum.client.data.models.ForumThreadSimple.Companion.IMAGE_AND_TITLE
import by.yazazzello.forum.client.data.models.ForumThreadSimple.Companion.SECTION_TITLE
import by.yazazzello.forum.client.data.models.ForumThreadSimple.Companion.SIMPLE_TITLE
import by.yazazzello.forum.client.features.BasicItemVH
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.features.main.HeaderItemVH
import by.yazazzello.forum.client.features.main.MainVhFactory
import com.squareup.picasso.Picasso

/**
 * Created by yazazzello on 10/3/17.
 */
class FeaturedAdapter(val picasso: Picasso, val items: MutableList<Bindable<*>>,
                      private val itemClicked: (item: Bindable<*>?) -> Unit) : RecyclerView.Adapter<BasicItemVH>() {

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        val model = items[position].model
        return when (model) {
            is ForumThreadSimple -> model.type
            is String -> SECTION_TITLE
            else -> -1
        }
    }

    override fun onBindViewHolder(holder: BasicItemVH, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicItemVH {

        return when (viewType) {
            SIMPLE_TITLE -> FeaturedVhFactory.createVh<ForumThreadSimpleVH>(picasso, parent, itemClicked)
            IMAGE_AND_TITLE -> FeaturedVhFactory.createVh<ForumThreadFeaturedVH>(picasso, parent, itemClicked)
            IMAGE_AND_TEXT -> FeaturedVhFactory.createVh<ForumThreadFeaturedPreviewVH>(picasso, parent, itemClicked)
            SECTION_TITLE -> MainVhFactory.createVh<HeaderItemVH>(parent, itemClicked)
            else -> throw IllegalStateException("unknown viewType $viewType")
        }
    }
}