package by.yazazzello.forum.client.features.topic

import android.os.Bundle
import by.yazazzello.forum.client.R
import kotlinx.android.synthetic.main.recycler_view_with_swipe_refresh.*

class ForumLatestTopicsFragment : ForumTopicsFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.forumMode = TopicsMode.LAST_24_HOURS
    }

    override fun getMenuItemId(): Int = R.id.navigation_latest

    override fun updateToolbar() {
        toolbarTitle = getString(R.string.toolbar_forum_last24)
        disableHomeAsUp()
    }

    override fun onBottomBtnTapped() {
        recycler_view?.smoothScrollToPosition(0)
    }
}