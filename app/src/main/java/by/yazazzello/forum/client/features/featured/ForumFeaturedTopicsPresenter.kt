package by.yazazzello.forum.client.features.featured

import by.yazazzello.forum.client.data.DataManager
import by.yazazzello.forum.client.features.BasePresenter
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.features.ForumFeaturedTopicsMvpView
import by.yazazzello.forum.client.helpers.LottieErrorMapper
import by.yazazzello.forum.client.helpers.RxSchedulers
import by.yazazzello.forum.client.helpers.TestOpen
import by.yazazzello.forum.client.helpers.mappers.HotTopicsMapper
import timber.log.Timber
import javax.inject.Inject

/**
 * for hot topics
 */
@TestOpen
class ForumFeaturedTopicsPresenter
@Inject
constructor(internal var dataManager: DataManager, rxSchedulers: RxSchedulers)
    : BasePresenter<ForumFeaturedTopicsMvpView>(rxSchedulers) {

    fun loadHot(forceNetwork: Boolean = false) {
        checkViewAttached()
        addDisposable(dataManager.getMainPage(forceNetwork)
                .map({
                    val inputString = it.string()
                    (mutableListOf<Bindable<*>>()).apply {
                        add(Bindable("Отмеченное"))
                        addAll(HotTopicsMapper.mapFeaturedWithPreview(inputString))
                        add(Bindable("Интересное"))
                        addAll(HotTopicsMapper.mapFeatured(inputString))
                        add(Bindable("Обсуждаемое"))
                        addAll(HotTopicsMapper.map(inputString))
                    }
                })
                .subscribeOn(rxSchedulers.ioScheduler)
                .observeOn(rxSchedulers.mainThreadScheduler)
                .doOnSubscribe {
                    mvpView?.flipProgress(true)
                    mvpView?.showErrorScreen(false)
                }
                .doFinally { mvpView?.flipProgress(false) }
                .subscribe({ threadList -> mvpView?.loadContent(threadList) },
                        {
                            Timber.e(it)
                            mvpView?.showErrorScreen(true, LottieErrorMapper.getLottieByError(it))
                            mvpView?.showError(it.message as String)
                        }))
    }

}