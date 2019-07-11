package by.yazazzello.forum.client.features.thread

import by.yazazzello.forum.client.data.DataManager
import by.yazazzello.forum.client.features.BasePresenter
import by.yazazzello.forum.client.features.ForumThreadMvpView
import by.yazazzello.forum.client.helpers.LottieErrorMapper
import by.yazazzello.forum.client.helpers.RxSchedulers
import by.yazazzello.forum.client.helpers.mappers.PostsMapper
import by.yazazzello.forum.client.network.responses.ForumPageResponse
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by yazazzello on 7/28/16.
 */

class ForumThreadPresenter
@Inject
constructor(private val dataManager: DataManager, rxSchedulers: RxSchedulers) : BasePresenter<ForumThreadMvpView>(rxSchedulers) {

    private val POSTS_PER_PAGE = 20

    private var headPosts: Int = 0

    private var tailPosts: Int = 0

    private var maxPosts: Int = 0

    var threadId: Int = 0

    var postId: Int? = null

    val maxPage: Int
        get() = maxPosts / POSTS_PER_PAGE


    fun loadItems(startPost: Int, onNext: (ForumPageResponse) -> Unit, showProgress: Boolean = false,
                  forceNetwork: Boolean = false, allowOfflineCache: Boolean = true) {
        checkViewAttached()
        addDisposable(dataManager.getThread(threadId, startPost, forceNetwork, allowOfflineCache)//.delay(3, TimeUnit.SECONDS, Schedulers.io())
                .map { responseBody -> PostsMapper.mapToForumPageResponse(Jsoup.parse(responseBody.string())) }
                .subscribeOn(rxSchedulers.ioScheduler)
                .observeOn(rxSchedulers.mainThreadScheduler)
                .doOnSubscribe { if (showProgress) {
                    mvpView?.flipProgress(true)
                    mvpView?.showErrorScreen(false)
                } }
                .doFinally { if (showProgress) mvpView?.flipProgress(false) }
                .subscribe(onNext,
                        {
                            Timber.e(it)
                            mvpView?.showErrorScreen(true, LottieErrorMapper.getLottieByError(it))
                            mvpView?.showError(it.message as String)
                        }))
    }


    fun canLoadForwardMore(): Boolean = headPosts < maxPosts

    fun canLoadBackwardMore(): Boolean = tailPosts > 0

    fun smartLoadPage() {
        if (threadId != 0) {
            addDisposable {
                dataManager.getThreadInfoFromDb(threadId)
                        .subscribeOn(rxSchedulers.ioScheduler)
                        .observeOn(rxSchedulers.mainThreadScheduler)
                        .subscribe(
                                {
                                    loadSpecificPage(it.postNumber, it.scrolledPosition)
                                },
                                {
                                    Timber.e(it)
                                    loadFirstPage()
                                },
                                {
                                    loadFirstPage()
                                }
                        )
            }
        } else if (postId != null) {
            val postNumb: Int = postId as Int
            addDisposable {
                dataManager.getThreadByPost(postNumb)
                        .map { responseBody: ResponseBody ->
                            PostsMapper.mapToForumPageResponse(Jsoup.parse(responseBody.string()))
                        }
                        .subscribeOn(rxSchedulers.ioScheduler)
                        .observeOn(rxSchedulers.mainThreadScheduler)
                        .doOnSubscribe { mvpView?.flipProgress(true) }
                        .doFinally { mvpView?.flipProgress(false) }
                        .subscribe(
                                {
                                    threadId = it.threadId ?: 0
                                    loadPost(it, postNumb)
                                },
                                {
                                    Timber.e(it)
                                    loadFirstPage()
                                }
                        )
            }
        }
    }

    private fun loadPost(response: ForumPageResponse, postId: Int) {
        val scrollTo = response.postList.indexOfFirst { bindable -> bindable.model.id == postId }
        pageAction(scrollTo).invoke(response)
    }

    fun loadNextPage() {
        loadItems(headPosts + POSTS_PER_PAGE, forwardAction())
    }

    fun loadFirstPage() {
        loadItems(headPosts, firstPageAction(), showProgress = true)
    }

    fun loadSpecificPage(startPost: Int, positionToScroll: Int = 0) {
        loadItems(startPost, pageAction(positionToScroll), true)
    }

    fun loadPreviousPage() {
        loadItems(tailPosts - POSTS_PER_PAGE, backwardAction(), showProgress = true)
    }

    fun reloadLastPage(lastPostId: Int) {
        mvpView?.setShowItemProgress(true)
        mvpView?.invalidateLastItem()
        loadItems(headPosts, reloadAction(lastPostId), false, true, false)
    }

    private fun reloadAction(lastPostId: Int): (ForumPageResponse) -> Unit {
        return { pageResponse ->
            val newMsgsList = pageResponse.postList.dropWhile { bindable -> bindable.model.id <= lastPostId }
            if (newMsgsList.isNotEmpty()) {
                pageResponse.postList = newMsgsList
                mvpView?.renderItems(pageResponse, attachToEnd = true)
                maxPosts = pageResponse.currentPost
                mvpView?.showToast("there are ${newMsgsList.size} new posts")
            } else {
                mvpView?.showToast("there are no new posts")
            }
            if (canLoadForwardMore()) {
                mvpView?.setShowItemProgress(true)
                loadNextPage()
            } else {
                mvpView?.setShowItemProgress(false)
            }
            mvpView?.invalidateLastItem()
        }
    }

    private fun backwardAction(): (ForumPageResponse) -> Unit {
        return {
            mvpView?.renderItems(it, attachToEnd = false)
            tailPosts = it.currentPost
            mvpView?.scrollAbitUp()
        }
    }

    private fun forwardAction(): (ForumPageResponse) -> Unit {
        return {
            mvpView?.renderItems(it)
            maxPosts = it.maxPosts
            headPosts = it.currentPost
        }
    }

    private fun firstPageAction(): (ForumPageResponse) -> Unit {
        return {
            mvpView?.renderItems(it)
            maxPosts = it.maxPosts
            headPosts = it.currentPost
            mvpView?.scrollToStart()
        }
    }

    private fun pageAction(itemToScroll: Int): (ForumPageResponse) -> Unit {
        return {
            Timber.d("pageAction $itemToScroll")
            mvpView?.renderItems(it, replace = true)
            maxPosts = it.maxPosts
            headPosts = it.currentPost
            tailPosts = headPosts
            mvpView?.setPageCounter(headPosts)
            mvpView?.enableBackwardAction()
            mvpView?.scrollToItem(itemToScroll)
        }
    }

    fun saveStateInDb(currentPostNumber: Int, scrolledPosition: Int, title: String) {
        dataManager.saveThreadInfoToDb(threadId, currentPostNumber, scrolledPosition, title)
    }

}

