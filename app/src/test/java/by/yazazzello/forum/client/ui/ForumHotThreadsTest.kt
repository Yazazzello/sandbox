package by.yazazzello.forum.client.ui

import by.yazazzello.forum.client.data.models.ForumThreadSimple
import by.yazazzello.forum.client.data.models.ForumThreadSimple.Companion.IMAGE_AND_TEXT
import by.yazazzello.forum.client.data.models.ForumThreadSimple.Companion.IMAGE_AND_TITLE
import by.yazazzello.forum.client.data.models.ForumThreadSimple.Companion.SIMPLE_TITLE
import by.yazazzello.forum.client.features.ForumFeaturedTopicsMvpView
import by.yazazzello.forum.client.features.featured.ForumFeaturedTopicsPresenter
import by.yazazzello.forum.client.utils.MockTools
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Created by yazazzello on 5/19/17.
 */
class ForumHotThreadsTest {

    private val view: ForumFeaturedTopicsMvpView = mock()
    private val presenter: ForumFeaturedTopicsPresenter = ForumFeaturedTopicsPresenter(MockTools.mockDataManager(),
            MockTools.testSchedulers())
    private val invalidPresenter: ForumFeaturedTopicsPresenter = ForumFeaturedTopicsPresenter(MockTools.mockInvalidDataManager(),
            MockTools.testSchedulers())

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
    fun shouldProperlyLoadTopicsPage() {
        presenter.loadHot()
        verify(view, times(2)).flipProgress(any())
        verify(view).loadContent(check { list ->
            assertEquals(18, list.size)
            assertEquals("Отмеченное", list[0].model.toString())
            assertEquals("Интересное", list[4].model.toString())
            assertEquals("Обсуждаемое", list[10].model.toString())
            assertEquals(IMAGE_AND_TEXT, (list[1].model as ForumThreadSimple).type)
            assertEquals(IMAGE_AND_TITLE, (list[5].model as ForumThreadSimple).type)
            assertEquals(SIMPLE_TITLE, (list[11].model as ForumThreadSimple).type)
            assertFalse((list[1].model as ForumThreadSimple).previewText.isNullOrEmpty())
            assertTrue((list[5].model as ForumThreadSimple).previewText.isNullOrEmpty())
            assertFalse((list[5].model as ForumThreadSimple).imgUrl.isNullOrEmpty())
            assertTrue((list[11].model as ForumThreadSimple).imgUrl.isNullOrEmpty())
        })
    }
}