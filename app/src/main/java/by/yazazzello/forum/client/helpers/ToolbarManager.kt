package by.yazazzello.forum.client.helpers

import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.helpers.ext.slideEnter
import by.yazazzello.forum.client.helpers.ext.slideExit

/**
 * Created by yazazzello on 6/2/17.
 * mToolbar
 */
interface ToolbarManager {

    val mToolbar: Toolbar

    var toolbarTitle: String
        get() = mToolbar.title.toString()
        set(value) {
            mToolbar.title = value
        }

    var toolbarSubTitle: String
        get() = mToolbar.subtitle.toString()
        set(value) {
            mToolbar.subtitle = value
        }

    fun setToolbarTextById(textId: Int, title: String) {
        (mToolbar.findViewById<TextView>(textId))?.text = title
    }

    fun initToolbar(menuResId: Int, menuCallbackFun: (item: MenuItem) -> Boolean) {
        mToolbar.inflateMenu(menuResId)
        mToolbar.setOnMenuItemClickListener(menuCallbackFun)
    }

    fun enableHomeAsUp(up: () -> Unit) {
        mToolbar.navigationIcon = createUpDrawable()
        mToolbar.setNavigationOnClickListener { up() }
    }

    fun disableHomeAsUp() {
        mToolbar.setNavigationIcon(R.mipmap.ic_launcher)
        mToolbar.setNavigationOnClickListener(null)
    }

    private fun createUpDrawable() = DrawerArrowDrawable(mToolbar.context).apply {
        progress = 1f
    }

    fun attachToScroll(recyclerView: RecyclerView, view: View? = null) {
        val toolbar = view ?: mToolbar
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(pRecyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0) toolbar.slideExit() else toolbar.slideEnter()
            }
        })
    }
}