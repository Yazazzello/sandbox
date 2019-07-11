package by.yazazzello.forum.client.data

import android.annotation.SuppressLint
import by.yazazzello.forum.client.data.db.ThreadInfo
import by.yazazzello.forum.client.data.db.ThreadInfoEntity
import by.yazazzello.forum.client.helpers.RxSchedulers
import by.yazazzello.forum.client.helpers.TestOpen
import by.yazazzello.forum.client.network.ApiService
import dagger.Lazy
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.requery.Persistable
import io.requery.kotlin.desc
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import okhttp3.ResponseBody
import timber.log.Timber
import java.util.*
import javax.inject.Singleton


@TestOpen
@Singleton
class DataManager(private val apiService: Lazy<ApiService>,
                  private val dbStore: KotlinReactiveEntityStore<Persistable>,
                  private val rxSchedulers: RxSchedulers) {

    private val disposables = CompositeDisposable()

    /**
     * load specific thread
     * forceNetwork - true if should ignore cache
     */
    fun getThread(threadId: Int, startPost: Int, forceNetwork: Boolean = false, allowOfflineCache: Boolean = true):
            Single<ResponseBody> {
        return apiService.get().getTopic(forceNetwork, allowOfflineCache, threadId, startPost)
    }


    fun getThreadByPost(post: Int, forceNetwork: Boolean = false, allowOfflineCache: Boolean = true): Single<ResponseBody> {
        return apiService.get().getTopicByPost(forceNetwork, allowOfflineCache, post)
    }

    /**
     * load main page
     */
    fun getHomeForumPage(forceNetwork: Boolean = false): Single<ResponseBody> = apiService.get().getMainForum(forceNetwork)

    /**
     * load threads for category
     * forceNetwork - true if should ignore cache
     */
    fun getThreadsList(id: Int, page: Int, forceNetwork: Boolean = false): Single<ResponseBody> {
        return apiService.get().getForum(forceNetwork, id, page)
    }

    fun getMainPage(forceNetwork: Boolean): Single<ResponseBody> {
        return apiService.get().getPageByUrl(forceNetwork, "https://www.onliner.by/")
    }

    fun getLast24hours(page: Int, forceNetwork: Boolean = false): Single<ResponseBody> {
        return apiService.get().getLastPosts(forceNetwork, page)
    }

    @SuppressLint("CheckResult")
    fun saveThreadInfoToDb(threadId: Int, postNumber: Int, scrolledPosition: Int, title: String) {

        dbStore.select(ThreadInfo::class)
                .where(ThreadInfo::threadId eq threadId)
                .get()
                .maybe()
                .subscribeOn(rxSchedulers.ioScheduler)
                .map { Pair(it, true) }
                .defaultIfEmpty(Pair(ThreadInfoEntity().apply {
                    this.threadId = threadId
                    this.title = title
                }, false))
                .flatMap { (threadInfo, isFound) ->
                    threadInfo.postNumber = postNumber
                    threadInfo.scrolledPosition = scrolledPosition
                    threadInfo.date = Date()
                    if (isFound) {//if found - update
                        dbStore.update(threadInfo).toMaybe()
                    } else {
                        dbStore.insert(threadInfo).toMaybe()
                    }
                }
                .doOnSubscribe { disposables.add(it) }
                .doOnDispose {
                    Timber.d("disposed")
                }
                .subscribe(
                        {
                            Timber.d("recorded + ${Thread.currentThread().name}" + it.id)
                            disposables.clear()
                        },
                        { Timber.e(it) },
                        { Timber.d("on Complete ${Thread.currentThread().name}") }
                )
    }

    fun getThreadInfoFromDb(threadId: Int): Maybe<ThreadInfo> {
        return (dbStore.select(ThreadInfo::class) where (ThreadInfo::threadId eq threadId))
                .get()
                .maybe()
    }

    fun getHistoryThreads(): List<ThreadInfo> {
        return dbStore
                .select(ThreadInfo::class)
                .orderBy(ThreadInfo::date.desc())
                .limit(50)
                .get().toList()

    }
}
