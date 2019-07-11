package by.yazazzello.forum.client.ui

import by.yazazzello.forum.client.features.ForumThreadMvpView
import by.yazazzello.forum.client.features.thread.ForumThreadPresenter
import by.yazazzello.forum.client.utils.MockTools
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

/**
 * Created by yazazzello on 5/19/17.
 */
class ForumOnePageThreadTest {

    private val view: ForumThreadMvpView = mock()
    private val presenter: ForumThreadPresenter = ForumThreadPresenter(MockTools.mockDataManager(), MockTools.testSchedulers())

    @Before
    fun setUp() {
        presenter.attachView(view)
        presenter.threadId = 111
    }

    @Test
    @Throws(Exception::class)
    fun shouldNotBeNulls() {
        assertNotNull(presenter)
        assertNotNull(presenter.mvpView)
    }

    @Test
    @Throws(Exception::class)
    fun shouldSmartLoadExist() {
        presenter.smartLoadPage()
        verify(view).renderItems(any(), eq(true), eq(false))
        verify(view).scrollToStart()
    }

}