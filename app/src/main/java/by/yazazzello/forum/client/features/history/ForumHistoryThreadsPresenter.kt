package by.yazazzello.forum.client.features.history

import by.yazazzello.forum.client.data.DataManager
import by.yazazzello.forum.client.data.models.ForumThread
import by.yazazzello.forum.client.features.BasePresenter
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.features.ForumHistoryThreadsMvpView
import by.yazazzello.forum.client.helpers.LottieErrorMapper
import by.yazazzello.forum.client.helpers.RxSchedulers
import by.yazazzello.forum.client.helpers.TestOpen
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

/**
 * for hot topics
 */
@TestOpen
class ForumHistoryThreadsPresenter
@Inject
constructor(internal var dataManager: DataManager, rxSchedulers: RxSchedulers)
    : BasePresenter<ForumHistoryThreadsMvpView>(rxSchedulers) {

    fun loadHistory() {
        checkViewAttached()
        Single.fromCallable {
            Timber.w("fromCallable current thread is " + Thread.currentThread().name)
            dataManager
                    .getHistoryThreads()
        }
        .flatMapObservable { Observable.fromIterable(it) }
        .map {
            Bindable(ForumThread(it.title, "", "", it.threadId, lastPost = it.postNumber))
        }
        .toList()
        .subscribeOn(rxSchedulers.ioScheduler)
        .doOnSubscribe {
            mvpView?.flipProgress(true)
            mvpView?.showErrorScreen(false)
            addDisposable(it)
        }
        .doFinally { mvpView?.flipProgress(false) }
        .doOnEvent { list, _ ->
            if (list.isEmpty()) throw IllegalArgumentException("no data")
        }
        .observeOn(rxSchedulers.mainThreadScheduler)
        .subscribe({ threadList -> mvpView?.loadContent(threadList) },
                {
                    Timber.e(it)
                    mvpView?.showErrorScreen(true, LottieErrorMapper.getLottieByError(it))
                    mvpView?.showError(it.message as String)
                })
    }

}