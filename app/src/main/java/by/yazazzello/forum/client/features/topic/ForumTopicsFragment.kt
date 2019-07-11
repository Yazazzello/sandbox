package by.yazazzello.forum.client.features.topic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.data.models.ForumCategory
import by.yazazzello.forum.client.features.BaseFragment
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.features.ForumTopicsMvpView
import by.yazazzello.forum.client.features.thread.ForumThreadActivity
import by.yazazzello.forum.client.helpers.EndlessRecyclerOnScrollListener
import by.yazazzello.forum.client.injection.rxbus.NavigationBus
import by.yazazzello.forum.client.navigation.ActivityNavigationEvent
import by.yazazzello.forum.client.navigation.KEY_EXTRA_FORUM_CATEGORY
import by.yazazzello.forum.client.navigation.KEY_EXTRA_FORUM_THREAD
import by.yazazzello.forum.client.views.decorations.DividerItemDecoration
import kotlinx.android.synthetic.main.recycler_view_with_swipe_refresh.*
import org.jetbrains.anko.toast
import javax.inject.Inject

/**
 * Created by yazazzello on 8/31/16.
 */

open class ForumTopicsFragment : BaseFragment<ForumTopicsPresenter, ForumTopicsMvpView>(), ForumTopicsMvpView {

    @Inject
    internal lateinit var navigationBus: NavigationBus

    private lateinit var topicAdapter: TopicAdapter

    private var forumCategory: ForumCategory? = null

    override fun getLayoutId() = R.layout.recycler_view_with_swipe_refresh

    override fun getMenuItemId(): Int = R.id.navigation_forum
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forumCategory = arguments?.getParcelable<Parcelable>(KEY_EXTRA_FORUM_CATEGORY) as? ForumCategory
        presenter.setForumId(forumCategory?.forumId ?: 0)
    }

    override fun initiateViews() {
        topicAdapter = TopicAdapter(mutableListOf()) {
            navigationBus
                    .navigate(ActivityNavigationEvent(context, ForumThreadActivity::class.java,
                    KEY_EXTRA_FORUM_THREAD to it as Parcelable))
        }
        val layoutManager = LinearLayoutManager(activity)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = topicAdapter
        recycler_view.addItemDecoration(DividerItemDecoration(context, R.drawable.divider))
        recycler_view.addOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore() {
                Handler(Looper.getMainLooper()).post {
                    presenter.requestNextPages()
                }
            }
        })
        swipe_refresh.setOnRefreshListener { presenter.loadForumThreads(true) }
    }

    override fun updateToolbar() {
        forumCategory?.let {
            toolbarTitle = it.title
            enableHomeAsUp { fragmentManager?.popBackStack() }
        }
    }

    override fun readyToCall() {
        presenter.loadForumThreads()
    }

    override fun loadContent(forumThreads: List<Bindable<*>>) {
        topicAdapter.items.clear()
        topicAdapter.items.addAll(forumThreads)
        topicAdapter.addProgressIndicator()
        topicAdapter.notifyDataSetChanged()
    }

    override fun addItems(forumThreadList: List<Bindable<*>>) {
        topicAdapter.addItems(forumThreadList)
    }

    override fun noMoreToLoad() {
        activity?.toast("no more to load")
        topicAdapter.noMoreToLoad()
    }

    override fun showError(msg: String) {
        activity?.toast("error $msg")
    }

    override fun couldntLoadNextPage() {
        topicAdapter.couldntLoadNextPage()
    }
}
