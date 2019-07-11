package by.yazazzello.forum.client.ui

import by.yazazzello.forum.client.features.ForumThreadMvpView
import by.yazazzello.forum.client.features.thread.ForumThreadPresenter
import by.yazazzello.forum.client.utils.MockTools
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Created by yazazzello on 5/19/17.
 */
class ForumThreadsTest {

    private val view: ForumThreadMvpView = mock()
    private val presenter: ForumThreadPresenter = ForumThreadPresenter(MockTools.mockDataManager(), MockTools.testSchedulers())
    private val invalidPresenter: ForumThreadPresenter = ForumThreadPresenter(MockTools.mockInvalidDataManager(), MockTools.testSchedulers())

    @Before
    fun setUp() {
        presenter.attachView(view)
    }

    @Test
    @Throws(Exception::class)
    fun shouldNotBeNulls() {
        assertNotNull(presenter)
        assertNotNull(presenter.mvpView)
    }

    @Test
    @Throws(Exception::class)
    fun shouldProperlyLoadFirstTopicsPage() {
        presenter.loadFirstPage()
        verify(view, times(2)).flipProgress(any())
        verify(view).renderItems(check {
            assertTrue(it.postList.size == 20, "wrong size")
            assertTrue(it.postList[0].model.postDate == "# 26 июня 2003 20:52", "wrong date")
            assertTrue(it.postList[0].model.author?.nick == "Порядочная Свинья", "wrong author")
            assertTrue(it.postList[0].model.author?.imgSrc == "http://content.onliner.by/user/avatar/80x80/7718", "wrong url pic")
            assertTrue(it.postList[0].model.author?.lvl == "Senior Member", "wrong lvl")
            assertTrue(it.postList[0].model.id == 563586, "wrong id")
        }, eq(true), eq(false))
        verify(view).scrollToStart()
    }

    @Test
    @Throws(Exception::class)
    fun shouldProperlyLoadSecondTopicsPage() {
        presenter.loadNextPage()
        verify(view, times(0)).flipProgress(any())
        verify(view).renderItems(check {
            assertTrue(it.postList.size == 20, "wrong size")
            assertTrue(it.postList[0].model.postDate == "# 30 июня 2003 00:19", "wrong date")
            assertTrue(it.postList[0].model.author?.nick == "Beerteen", "wrong author")
            assertTrue(it.postList[0].model.author?.imgSrc == "http://content.onliner.by/user/avatar/80x80/4184", "wrong url pic")
            assertTrue(it.postList[0].model.author?.lvl == "Клуб друзей МТС", "wrong lvl")
            assertTrue(it.postList[0].model.id == 567091, "wrong id")
        }, eq(true), eq(false))
    }

    @Test
    @Throws(Exception::class)
    fun shouldPreviousPage() {
        presenter.loadPreviousPage()
        verify(view, times(2)).flipProgress(any())
        verify(view).renderItems(any(), eq(false), eq(false))
        verify(view).scrollAbitUp()
    }

    @Test
    @Throws(Exception::class)
    fun shouldLoadSpecificPage() {
        presenter.loadSpecificPage(20)
        verify(view, times(2)).flipProgress(any())
        verify(view).renderItems(any(), eq(true), eq(true))
        verify(view).setPageCounter(any())
        verify(view).enableBackwardAction()
        verify(view).scrollToItem(eq(0))
    }

    @Test
    @Throws(Exception::class)
    fun shouldLoadNewPosts() {
        presenter.reloadLastPage(565899)
        verify(view).renderItems(check {
            assertTrue(it.postList.size == 2, "wrong count of new items")
        }, eq(true), eq(false))
        verify(view).showToast(any())
        verify(view, times(2)).setShowItemProgress(any())
        verify(view, times(2)).invalidateLastItem()
    }

    @Test
    @Throws(Exception::class)
    fun shouldNotLoadNewPosts() {
        presenter.reloadLastPage(566948)
        verify(view, times(0)).renderItems(any(),any(), any())
        verify(view).showToast(any())
        verify(view, times(2)).setShowItemProgress(any())
        verify(view, times(2)).invalidateLastItem()
    }

    @Test
    @Throws(Exception::class)
    fun shouldSmartLoadEmpty() {
        presenter.threadId = 1
        presenter.smartLoadPage()
        verify(view, times(2)).flipProgress(any())
        verify(view).renderItems(any(), eq(true), eq(false))
        verify(view).scrollToStart()
    }

    @Test
    @Throws(Exception::class)
    fun shouldSmartLoadError() {
        presenter.threadId = 2
        presenter.smartLoadPage()
        verify(view, times(2)).flipProgress(any())
        verify(view).renderItems(any(), eq(true), eq(false))
        verify(view).scrollToStart()
    }

    @Test
    @Throws(Exception::class)
    fun shouldSmartLoadToPost() {
        presenter.threadId = 0
        presenter.postId = 563983
        presenter.smartLoadPage()
        verify(view).renderItems(any(), eq(true), eq(true))
        verify(view).setPageCounter(any())
        verify(view).enableBackwardAction()
        verify(view).scrollToItem(eq(5))
    }

    @Test
    @Throws(Exception::class)
    fun shouldSmartLoadExist() {
        presenter.threadId = 3
        presenter.smartLoadPage()
        verify(view).renderItems(any(), eq(true), eq(true))
        verify(view).setPageCounter(any())
        verify(view).enableBackwardAction()
        verify(view).scrollToItem(eq(10))
    }

    @Test
    @Throws(Exception::class)
    fun shouldShowError() {
        invalidPresenter.attachView(view)
        invalidPresenter.loadFirstPage()
        verify(view, times(2)).flipProgress(any())
        verify(view).showError(any())
        verify(view).showErrorScreen(eq(false), eq(null))
        verify(view).showErrorScreen(eq(true), any())
        verifyNoMoreInteractions(view)
    }
}