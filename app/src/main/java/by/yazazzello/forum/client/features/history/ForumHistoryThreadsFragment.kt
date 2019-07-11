package by.yazazzello.forum.client.features.history

import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.features.BaseFragment
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.features.ForumHistoryThreadsMvpView
import by.yazazzello.forum.client.features.featured.FeaturedAdapter
import by.yazazzello.forum.client.features.thread.ForumThreadActivity
import by.yazazzello.forum.client.injection.rxbus.NavigationBus
import by.yazazzello.forum.client.navigation.ActivityNavigationEvent
import by.yazazzello.forum.client.navigation.KEY_EXTRA_FORUM_THREAD
import by.yazazzello.forum.client.views.decorations.DividerItemDecoration
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_view_with_swipe_refresh.*
import javax.inject.Inject

/**
 * Created by yazazzello on 12/11/17.
 */
class ForumHistoryThreadsFragment : BaseFragment<ForumHistoryThreadsPresenter, ForumHistoryThreadsMvpView>(), ForumHistoryThreadsMvpView {

    private lateinit var featuredAdapter: FeaturedAdapter

    @Inject
    lateinit var navigationBus: NavigationBus

    @Inject
    lateinit var picasso: Picasso

    override fun getLayoutId() = R.layout.recycler_view_with_swipe_refresh

    override fun getMenuItemId(): Int = R.id.navigation_history

    override fun loadContent(list: List<Bindable<*>>) {
        featuredAdapter.items.clear()
        featuredAdapter.items.addAll(list)
        recycler_view.adapter.notifyDataSetChanged()
    }

    override fun initiateViews() {
        featuredAdapter = FeaturedAdapter(picasso, mutableListOf()) { item ->

            navigationBus.navigate(ActivityNavigationEvent(context, ForumThreadActivity::class.java,
                            KEY_EXTRA_FORUM_THREAD to item?.model as Parcelable))
        }
        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.addItemDecoration(DividerItemDecoration(context, R.drawable.divider))
        recycler_view.adapter = featuredAdapter
        swipe_refresh.setOnRefreshListener {
            presenter.loadHistory()
        }
    }


    override fun readyToCall() {
        presenter.loadHistory()
    }

    override fun updateToolbar() {
        toolbarTitle = getString(R.string.toolbar_history)
        disableHomeAsUp()
    }
    
    override fun onBottomBtnTapped() {
        recycler_view?.smoothScrollToPosition(0)
    }
}