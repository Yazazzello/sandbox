package by.yazazzello.forum.client.features.thread

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.data.models.ForumThreadSimple
import by.yazazzello.forum.client.features.BaseFragment
import by.yazazzello.forum.client.features.ForumThreadMvpView
import by.yazazzello.forum.client.features.KEY_CURR_POS
import by.yazazzello.forum.client.features.KEY_CURR_POST
import by.yazazzello.forum.client.features.thread.dialogs.FirstPostDialog
import by.yazazzello.forum.client.features.thread.dialogs.PageInputDialog
import by.yazazzello.forum.client.helpers.EndlessRecyclerOnScrollListener
import by.yazazzello.forum.client.helpers.ext.beginDelayedFadeIn
import by.yazazzello.forum.client.helpers.ext.resolveAndLaunchUrl
import by.yazazzello.forum.client.injection.rxbus.NavigationBus
import by.yazazzello.forum.client.navigation.KEY_EXTRA_FORUM_THREAD
import by.yazazzello.forum.client.network.responses.ForumPageResponse
import by.yazazzello.forum.client.views.decorations.VerticalSpaceItemDecoration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.perf.metrics.AddTrace
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.cst_toolbar_layout.view.*
import kotlinx.android.synthetic.main.recycler_view_with_swipe_refresh.*
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject


private const val NONE = -1
class ForumThreadFragment : BaseFragment<ForumThreadPresenter, ForumThreadMvpView>(),
        ForumThreadMvpView, PageInputDialog.PageInputCallback {

    @Inject
    internal lateinit var navigationBus: NavigationBus

    @Inject
    internal lateinit var scrollSubject: PublishSubject<Int>

    @Inject
    internal lateinit var fireAnalytics: FirebaseAnalytics

    @Inject
    internal lateinit var threadAdapter: ThreadAdapter

    private lateinit var pageCounter: TickerView

    private lateinit var layoutManager: LinearLayoutManager

    private var mEndScrollListener: EndScrollListener? = null

    private var currentPostNumber: Int = NONE

    private var currentVisPosition: Int = 0

    override fun getLayoutId() = R.layout.recycler_view_with_swipe_refresh
    
    override fun getMenuItemId(): Int  = 0

    private lateinit var forumThread: ForumThreadSimple

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        forumThread = arguments?.getParcelable<Parcelable>(KEY_EXTRA_FORUM_THREAD) as ForumThreadSimple
        if (forumThread.pageHolderClicked != null) {
            currentPostNumber = forumThread.pageHolderClicked?.intParam ?: 0
        }
        if (forumThread.topicId != 0) {
            presenter.threadId = forumThread.topicId
        } else {
            presenter.postId = forumThread.post
        }

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, forumThread.topicId.toString())
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, forumThread.title)
        fireAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        currentVisPosition = (recycler_view.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        outState.putInt(KEY_CURR_POS, currentVisPosition)
        outState.putInt(KEY_CURR_POST, currentPostNumber)
        super.onSaveInstanceState(outState)
        Timber.d("onSaveInstanceState $currentVisPosition")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        currentVisPosition = savedInstanceState?.getInt(KEY_CURR_POS) ?: 0
        currentPostNumber = savedInstanceState?.getInt(KEY_CURR_POST) ?: NONE
        Timber.d("onViewStateRestored $currentVisPosition")
    }

    override fun initiateViews() {
        swipe_refresh.isEnabled = false
        threadAdapter.reloadFunc = { lastPostId ->
            presenter.reloadLastPage(lastPostId)
        }
        layoutManager = LinearLayoutManager(activity)
        mEndScrollListener = EndScrollListener(layoutManager)
        pageCounter = mToolbar.findViewById(R.id.toolbar_page_counter)
        pageCounter.setCharacterList(TickerUtils.getDefaultNumberList())
        mToolbar.findViewById<View>(R.id.page_cntr_container).setOnClickListener { this@ForumThreadFragment.showInputPageDialog() }
        pageCounter.text = "1"
        recycler_view.layoutManager = layoutManager
        recycler_view.addItemDecoration(VerticalSpaceItemDecoration(24))
        recycler_view.adapter = threadAdapter
        recycler_view.addOnScrollListener(mEndScrollListener)

    }

    override fun updateToolbar() {
        initToolbar(R.menu.thread_menu) {
            when (it.itemId) {
                R.id.open_in_browser -> {
                    val link = "http://forum.onliner.by/viewtopic.php?t=${presenter.threadId}&start=$currentPostNumber"
                    context?.resolveAndLaunchUrl(link) { context?.toast("Cannot be opened") }
                }
                else -> context?.toast("Unknown option")
            }
            true
        }
        setToolbarTextById(R.id.cst_title, forumThread.title)
        enableHomeAsUp {
            activity?.finish()
        }
    }

    private fun showInputPageDialog() {
        val pageInputDialog = PageInputDialog.newInstance(presenter.maxPage)
        pageInputDialog.pageInputCallback = this
        pageInputDialog.show(fragmentManager, "InputPage")
    }

    override fun showToast(message: String) {
        context?.toast(message)
    }

    override fun setShowItemProgress(showProgress: Boolean) {
        threadAdapter.setShowProgress(showProgress)
    }

    override fun scrollToStart() {
        recycler_view.scrollToPosition(0)
    }

    override fun scrollAbitUp() {
        recycler_view.scrollBy(0, -50)
    }

    override fun readyToCall() {
        if (currentPostNumber != NONE) {
            presenter.loadSpecificPage(currentPostNumber, currentVisPosition)
        } else {
            presenter.smartLoadPage()
        }
    }

    override fun onStop() {
        currentVisPosition = (recycler_view.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        Timber.d("onStop $currentVisPosition")
        presenter.saveStateInDb(currentPostNumber, currentVisPosition, forumThread.title)
        super.onStop()
    }

    override fun scrollToItem(positionToScroll: Int) {
        recycler_view.postDelayed({
            recycler_view.scrollToPosition(positionToScroll)
        }, 300)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.thread_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.open_in_browser -> {
                Toast.makeText(context, "open in browser", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @AddTrace(name = "renderItems")
    override fun renderItems(pageResponse: ForumPageResponse, attachToEnd: Boolean, replace: Boolean) {
        val itemsToAdd = pageResponse.postList.toMutableList()

        if (replace) {
            threadAdapter.clear()
            mEndScrollListener?.resetPageCount()
            threadAdapter.notifyDataSetChanged()
            recycler_view.scrollToPosition(0)
        }
        if (attachToEnd) {
            threadAdapter.addItems(itemsToAdd)
        } else {
            threadAdapter.addItemsToHead(itemsToAdd)
        }

        if(!pageCounter.isVisible) {
            (pageCounter.parent as ViewGroup).beginDelayedFadeIn {
                pageCounter.isVisible = true
            }
        }

        pageResponse.firstPost?.apply {
            if (mToolbar.cst_title.hasOnClickListeners()) return@apply
            
            val animation = AnimationUtils.loadAnimation(context, R.anim.shake)
            mToolbar.cst_title.startAnimation(animation)
            mToolbar.cst_title.setOnClickListener {
                val postDialog = FirstPostDialog.newInstance()
                postDialog.model = this
                postDialog.show(activity?.supportFragmentManager, "firstPost")
            }
        }

    }

    override fun setPageCounter(currentPage: Int) {
        pageCounter.text = String.format("%1\$s", currentPage / 20 + 1)
    }

    override fun onPageInput(pageNumber: Int) {
        Timber.d("onPageInput() called with: pageNumber = [$pageNumber]")
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, forumThread.topicId.toString())
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, forumThread.title)
        bundle.putString(FirebaseAnalytics.Param.INDEX, pageNumber.toString())
        fireAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        presenter.loadSpecificPage(pageNumber * 20)
    }

    override fun invalidateLastItem() {
        threadAdapter.invalidateLastItem()
    }

    override fun enableBackwardAction() {
        swipe_refresh.isEnabled = true
        swipe_refresh.setOnRefreshListener {
            if (presenter.canLoadBackwardMore()) {
                presenter.loadPreviousPage()
            } else {
                context?.toast("unable to load previous post")
                swipe_refresh.isRefreshing = false
            }
        }
    }

    private inner class EndScrollListener(linearLayoutManager: LinearLayoutManager) : EndlessRecyclerOnScrollListener(linearLayoutManager) {
        override fun onLoadMore() {
            if (presenter.canLoadForwardMore()) {
                presenter.loadNextPage()
                activity?.runOnUiThread {
                    threadAdapter.setShowProgress(true)
                }
            } else {
                threadAdapter.setShowProgress(false)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            scrollSubject.onNext(dy)
            val msgPost = threadAdapter.getItem(mLinearLayoutManager.findFirstVisibleItemPosition())
            msgPost?.let {
                if (currentPostNumber != it.postNumber) {//page has been changed
                    setPageCounter(it.postNumber)
                    currentPostNumber = it.postNumber
                }
            }
        }
    }
}


