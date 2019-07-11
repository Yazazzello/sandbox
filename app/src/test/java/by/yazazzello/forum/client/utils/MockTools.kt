package by.yazazzello.forum.client.utils

import by.yazazzello.forum.client.data.DataManager
import by.yazazzello.forum.client.data.db.ThreadInfoEntity
import by.yazazzello.forum.client.helpers.RxSchedulers
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.ResponseBody

private const val textMedia = "text/html"

/**
 * Created by yazazzello on 5/18/17.
 *
 */
object MockTools {

    fun mockDataManager() = mock<DataManager> {
        on {
            getThread(eq(111), eq(0), any(), any())
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia),
                TestTools.readFile("one_page_thread.html"))))
        on {
            getHomeForumPage()
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia),
                TestTools.readFile("main_page.html"))))
        on {
            getThreadsList(any(), any(), any())
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia),
                TestTools.readFile("threads.html"))))
        on {
            getThread(any(), eq(0), any(), any())
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia),
                TestTools.readFile("mts_page_1.html"))))
        on {
            getThreadByPost(eq(563983), any(), any())
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia),
                TestTools.readFile("mts_page_1.html"))))
        on {
            getThread(any(), eq(20), any(), any())
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia),
                TestTools.readFile("mts_page_2.html"))))
        on {
            getThread(any(), eq(-20), any(), any())
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia),
                TestTools.readFile("mts_page_2.html"))))
        on {
            getThread(any(), eq(30), any(), any())
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia),
                TestTools.readFile("mts_page_2.html"))))
        on {
            getThreadInfoFromDb(eq(1))
        }.thenReturn(Maybe.empty())
        on {
            getThreadInfoFromDb(eq(111))
        }.thenReturn(Maybe.empty())

        on {
            getMainPage(false)
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia),
                TestTools.readFile("index_main.html"))))

        on {
            getThreadInfoFromDb(eq(2))
        }.thenReturn(Maybe.error { RuntimeException("test exception") })

        on {
            getThreadInfoFromDb(eq(3))
        }.thenReturn(Maybe.just(ThreadInfoEntity().apply {
            threadId = 3
            postNumber = 20
            scrolledPosition = 10
        }))
    }

    fun mockInvalidDataManager() = mock<DataManager> {
        on {
            getHomeForumPage()
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia), "")))
        on {
            getThread(any(), any(), any(), any())
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia), "")))
        on {
            getThreadsList(any(), any(), any())
        }.thenReturn(Single.just(ResponseBody.create(MediaType.parse(textMedia), "")))

    }

    fun testSchedulers(): RxSchedulers {
        return RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline())
    }
}