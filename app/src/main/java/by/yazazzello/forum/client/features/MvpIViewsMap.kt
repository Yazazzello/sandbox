package by.yazazzello.forum.client.features

import by.yazazzello.forum.client.network.responses.ForumPageResponse

/**
 * Created by yazazzello on 1/20/17.
 */
interface BaseMvpView //marker

interface LoadingMvpView : BaseMvpView {

    fun flipProgress(isRefreshing: Boolean)

    fun showError(msg: String)

    fun showErrorScreen(shouldShow: Boolean, lottieJson: String? = null)
}

interface ForumMainMvpView : LoadingMvpView {

    fun loadContent(content: List<Bindable<*>>)
}

interface ForumTopicsMvpView : LoadingMvpView {
    
    fun loadContent(forumThreads: List<Bindable<*>>)

    fun addItems(forumThreadList: List<Bindable<*>>)

    fun noMoreToLoad()

    fun couldntLoadNextPage()
}

interface ForumThreadMvpView : LoadingMvpView {

    fun renderItems(pageResponse: ForumPageResponse, attachToEnd: Boolean = true, replace: Boolean = false)

    fun setPageCounter(currentPage: Int)

    fun scrollToStart()

    fun enableBackwardAction()

    fun scrollAbitUp()

    fun showToast(message: String)

    fun setShowItemProgress(showProgress: Boolean)

    fun invalidateLastItem()
    
    fun scrollToItem(positionToScroll: Int)
}

interface ForumFeaturedTopicsMvpView : LoadingMvpView {

    fun loadContent(list: List<Bindable<*>>)
    
}

interface ForumHistoryThreadsMvpView : LoadingMvpView {

    fun loadContent(list: List<Bindable<*>>)

}

