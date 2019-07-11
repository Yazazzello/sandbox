package by.yazazzello.forum.client.features.topic

import by.yazazzello.forum.client.data.DataManager
import by.yazazzello.forum.client.data.models.ForumThread
import by.yazazzello.forum.client.features.BasePresenter
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.features.ForumTopicsMvpView
import by.yazazzello.forum.client.helpers.LottieErrorMapper
import by.yazazzello.forum.client.helpers.RxSchedulers
import by.yazazzello.forum.client.helpers.TestOpen
import by.yazazzello.forum.client.helpers.mappers.ThreadMapper
import io.reactivex.functions.Function
import okhttp3.ResponseBody
import org.jetbrains.annotations.TestOnly
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by yazazzello on 8/31/16.
 */
@TestOpen
class ForumTopicsPresenter
@Inject
constructor(internal var dataManager: DataManager, rxSchedulers: RxSchedulers) : BasePresenter<ForumTopicsMvpView>(rxSchedulers) {

    companion object {
        private const val THREADS_PER_PAGE = 50
    }

    var forumMode: Int = TopicsMode.BY_FORUM_ID

    private var currentPage = 0

    private var maxPage: Int = 0

    private var forumId: Int = 0

    fun loadForumThreads(forceNetwork: Boolean = false) {
        checkViewAttached()
        currentPage = 0
        addDisposable {
            if (forumMode == TopicsMode.BY_FORUM_ID) {
                dataManager.getThreadsList(forumId, currentPage, forceNetwork)
            } else {
                dataManager.getLast24hours(currentPage, forceNetwork)

            }
                    .map(MapToThreads())
                    .subscribeOn(rxSchedulers.ioScheduler)
                    .observeOn(rxSchedulers.mainThreadScheduler)
                    .doOnSubscribe {
                        mvpView?.flipProgress(true)
                        mvpView?.showErrorScreen(false)
                    }
                    .doFinally { mvpView?.flipProgress(false) }
                    .subscribe({ threadList ->
                        mvpView?.loadContent(threadList)
                    },
                            {
                                Timber.e(it)
                                mvpView?.showError(it.message as String)
                                mvpView?.showErrorScreen(true, LottieErrorMapper.getLottieByError(it))
                            })
        }
    }

    fun requestNextPages() {

        if (currentPage + THREADS_PER_PAGE > maxPage) {
            mvpView?.noMoreToLoad()
        } else {
            currentPage += THREADS_PER_PAGE
            addDisposable {
                if (forumMode == TopicsMode.BY_FORUM_ID) {
                    dataManager.getThreadsList(id = forumId, page = currentPage)
                } else {
                    dataManager.getLast24hours(currentPage)
                }
                        .map(MapToThreads())
                        .subscribeOn(rxSchedulers.ioScheduler)
                        .observeOn(rxSchedulers.mainThreadScheduler)
                        .subscribe({ mvpView?.addItems(it) },
                                {
                                    mvpView?.showError(it.message as String)
                                    mvpView?.couldntLoadNextPage()
                                })
            }
        }
    }

    fun setForumId(forumId: Int) {
        this.forumId = forumId
    }

    private inner class MapToThreads : Function<ResponseBody, List<Bindable<*>>> {
        override fun apply(responseBody: ResponseBody): List<Bindable<*>> {
            val document: Document = Jsoup.parse(responseBody.string())
            val infoHolder = ThreadMapper.mapToThreads(document)
            maxPage = infoHolder.lastPage
            val threadList = infoHolder.threadList
            val bindableList = mutableListOf<Bindable<ForumThread>>()
            threadList.forEach {
                bindableList.add(Bindable(it))
            }
            return bindableList
        }
    }

    @TestOnly
    fun mockMaxPage(page: Int) {
        maxPage = page
    }
}
