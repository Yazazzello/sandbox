package by.yazazzello.forum.client.ui

import by.yazazzello.forum.client.data.models.ForumThread
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.features.ForumTopicsMvpView
import by.yazazzello.forum.client.features.topic.ForumTopicsPresenter
import by.yazazzello.forum.client.utils.MockTools
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Created by yazazzello on 5/19/17.
 */
class ForumTopicsTest {

    private val view: ForumTopicsMvpView = mock()
    private val presenter: ForumTopicsPresenter = ForumTopicsPresenter(MockTools.mockDataManager(), MockTools.testSchedulers())
    private val invalidPresenter: ForumTopicsPresenter = ForumTopicsPresenter(MockTools.mockInvalidDataManager(), MockTools.testSchedulers())

    @Before
    fun setUp() {
        presenter.attachView(view)
        presenter.setForumId(1)
        invalidPresenter.setForumId(1)
    }

    @Test
    @Throws(Exception::class)
    fun shouldNotBeNulls() {
        assertNotNull(presenter)
        assertNotNull(presenter.mvpView)
    }

    @Test
    @Throws(Exception::class)
    fun shouldProperlyLoadTopicsPage() {
        presenter.loadForumThreads()
        verify(view, times(2)).flipProgress(any())
        verify(view).loadContent(com.nhaarman.mockito_kotlin.check {
            assertTrue(it.size == 50, "wrong size of list")
            assertTrue((it[0].model as ForumThread).title.contentEquals("Задай вопрос VELCOM"))
            assertTrue((it[49].model as ForumThread).title.contentEquals("VELCOM: расторжение контракта -- плати"))
        })
    }

    @Test
    @Throws(Exception::class)
    fun shouldLoadNextPage() {
        presenter.mockMaxPage(51)
        presenter.requestNextPages()
        verify(view, times(1)).addItems(any())
        presenter.requestNextPages()
        verify(view).noMoreToLoad()
    }

    @Test
    @Throws(Exception::class)
    fun shouldShowError() {
        invalidPresenter.attachView(view)
        invalidPresenter.loadForumThreads()
        verify(view, times(2)).flipProgress(any())
        verify(view).showError(any())
        verify(view).showErrorScreen(eq(false), eq(null))
        verify(view).showErrorScreen(eq(true), any())
        verifyNoMoreInteractions(view)
    }

    @Test
    @Throws(Exception::class)
    fun `should Filter Duplicates for forum thread`() {
        val listOne = mutableListOf(
                Bindable(ForumThread("", "","",0)),
                Bindable(ForumThread("", "","",1)),
                Bindable(ForumThread("", "","",3)),
                Bindable(ForumThread("", "","",4))
        )
        val listSecond = mutableListOf(
                Bindable(ForumThread("", "","",4)),
                Bindable(ForumThread("", "","",5)),
                Bindable(ForumThread("", "","",6)))

        var resultList = listSecond.minus(listOne)

        resultList.forEach { println(it.model) }
        assertEquals(2, resultList.size)

        resultList = listOne.minus(listSecond)
        resultList.forEach { println(it.model) }
        assertEquals(3, resultList.size)
    }
}