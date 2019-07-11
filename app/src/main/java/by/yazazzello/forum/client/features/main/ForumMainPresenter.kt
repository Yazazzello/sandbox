package by.yazazzello.forum.client.features.main

import by.yazazzello.forum.client.data.DataManager
import by.yazazzello.forum.client.data.models.ForumCategory
import by.yazazzello.forum.client.features.BasePresenter
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.features.ForumMainMvpView
import by.yazazzello.forum.client.helpers.LottieErrorMapper
import by.yazazzello.forum.client.helpers.RxSchedulers
import by.yazazzello.forum.client.helpers.mappers.ThreadMapper
import org.jsoup.Jsoup
import timber.log.Timber
import java.util.LinkedHashMap
import javax.inject.Inject
import kotlin.collections.ArrayList

class ForumMainPresenter @Inject constructor(private var dataManager: DataManager, schedulers: RxSchedulers)
    : BasePresenter<ForumMainMvpView>(schedulers) {

    internal fun loadContent(forceNetwork: Boolean = false) {

        checkViewAttached()
        addDisposable(dataManager.getHomeForumPage(forceNetwork)
                .map { responseBody -> ThreadMapper.mapToSections(Jsoup.parse(responseBody.string())) }
                .map { linkedHashMap: LinkedHashMap<String, List<ForumCategory>> ->

                    val result = ArrayList<Bindable<*>>()
                    linkedHashMap.forEach { entry ->
                        result.add(Bindable(entry.key))
                        entry.value.forEach { result.add(Bindable(it)) }
                    }
                    result
                }
                .subscribeOn(rxSchedulers.ioScheduler)
                .observeOn(rxSchedulers.mainThreadScheduler)
                .doOnSubscribe {
                    mvpView?.flipProgress(true)
                    mvpView?.showErrorScreen(false)
                }
                .doFinally { mvpView?.flipProgress(false) }
                .subscribe({ content -> mvpView?.loadContent(content) },
                        {
                            Timber.e(it)
                            mvpView?.showErrorScreen(true, LottieErrorMapper.getLottieByError(it))
                            mvpView?.showError(it.message as String)
                        }))
    }
}