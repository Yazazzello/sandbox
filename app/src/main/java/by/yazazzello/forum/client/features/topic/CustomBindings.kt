package by.yazazzello.forum.client.features.topic

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isGone
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.data.models.PageHolder
import by.yazazzello.forum.client.databinding.ThreadPartPageButtonBinding


@BindingAdapter("pages", "listener")
fun LinearLayout.renderPageButtons(list: List<PageHolder>?, listener: OnPageClickedListener) {
    if (list?.isEmpty() == true) this.isGone = true
    
    list?.forEach { pageHolder ->
        val binding = DataBindingUtil.inflate<ThreadPartPageButtonBinding>(LayoutInflater.from(this.context),
                R.layout.thread_part_page_button, this, false)

        binding.label = pageHolder.displayable
        binding.root.setOnClickListener { listener.onPageClick(pageHolder) }
        this.addView(binding.root)
    }
}
