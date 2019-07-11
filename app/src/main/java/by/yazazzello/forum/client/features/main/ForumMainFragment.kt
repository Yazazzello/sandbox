package by.yazazzello.forum.client.features.main

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.features.BaseFragment
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.features.ForumMainMvpView
import by.yazazzello.forum.client.features.KEY_CURR_POS
import by.yazazzello.forum.client.features.topic.ForumTopicsFragment
import by.yazazzello.forum.client.helpers.ext.instanceOf
import by.yazazzello.forum.client.injection.rxbus.NavigationBus
import by.yazazzello.forum.client.navigation.FragmentNavigationEvent
import by.yazazzello.forum.client.navigation.KEY_EXTRA_FORUM_CATEGORY
import by.yazazzello.forum.client.views.decorations.DividerItemDecoration
import kotlinx.android.synthetic.main.recycler_view_with_swipe_refresh.*
import javax.inject.Inject

/**
 * Created by yazazzello on 1/20/17.
 */
class ForumMainFragment : BaseFragment<ForumMainPresenter, ForumMainMvpView>(), ForumMainMvpView {

    private lateinit var mainAdapter: MainAdapter

    override fun getLayoutId() = R.layout.recycler_view_with_swipe_refresh

    override fun getMenuItemId(): Int = R.id.navigation_forum

    @Inject
    lateinit var navigationBus: NavigationBus

    private var lastScrolledPosition: Int = 0

    private lateinit var layoutManager: LinearLayoutManager

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_CURR_POS, layoutManager.findFirstVisibleItemPosition())
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastScrolledPosition = savedInstanceState?.getInt(KEY_CURR_POS) ?: 0
    }

    override fun loadContent(content: List<Bindable<*>>) {
        mainAdapter.items.clear()
        mainAdapter.items.addAll(content)
        mainAdapter.notifyDataSetChanged()
    }

    override fun initiateViews() {
        mainAdapter = MainAdapter(mutableListOf()) { clickedForumCategory ->
            navigationBus.navigate(FragmentNavigationEvent(context,
                            instanceOf<ForumTopicsFragment>(KEY_EXTRA_FORUM_CATEGORY to clickedForumCategory?.model as Parcelable)))
        }
        layoutManager = LinearLayoutManager(activity)
        recycler_view.layoutManager = layoutManager
        recycler_view.addItemDecoration(DividerItemDecoration(context, R.drawable.divider))
        recycler_view.adapter = mainAdapter
        swipe_refresh.setOnRefreshListener { presenter.loadContent(true) }
        updateToolbar()
    }

    override fun updateToolbar() {
        toolbarTitle = getString(R.string.toolbar_main)
        disableHomeAsUp()
    }

    override fun readyToCall() {
        presenter.loadContent()
    }

    override fun onBottomBtnTapped() {
        recycler_view?.smoothScrollToPosition(0)
    }
}