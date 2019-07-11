package by.yazazzello.forum.client.ui

import by.yazazzello.forum.client.features.ForumMainMvpView
import by.yazazzello.forum.client.features.main.ForumMainPresenter
import by.yazazzello.forum.client.utils.MockTools
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


/**
 * Created by yazazzello on 2/25/17.
 * test for ForumMainPresenter
 */
class ForumMainPageTest {
    private val TECH = "Технологии"
    private val AUTO = "Аuto Onliner"
    private val MONEY = "Деньги"   //4
    private val ESTATE = "Недвижимость" //3
    private val OFFLINE = "Offline" //13
    private val ONLINER = "Onliner" //1
    private val BARAHOLKA = "Барахолка" //0

    private val view: ForumMainMvpView = mock()
    private val presenter: ForumMainPresenter = ForumMainPresenter(MockTools.mockDataManager(), MockTools.testSchedulers())
    private val invalidPresenter: ForumMainPresenter = ForumMainPresenter(MockTools.mockInvalidDataManager(), MockTools.testSchedulers())

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
    fun shouldProperlyLoadMainPage() {
        presenter.loadContent()
        verify(view, times(2)).flipProgress(any())
        verify(view).loadContent(com.nhaarman.mockito_kotlin.check {

            assertTrue((it[0].model as String) == TECH, sectionIsAbsent(TECH))
            assertTrue((it[8].model as String) == AUTO, sectionIsAbsent(AUTO))
            assertTrue((it[14].model as String) == MONEY, sectionIsAbsent(MONEY))
            assertTrue((it[19].model as String) == ESTATE, sectionIsAbsent(ESTATE))
            assertTrue((it[23].model as String) == OFFLINE, sectionIsAbsent(OFFLINE))
            assertTrue((it[37].model as String) == ONLINER, sectionIsAbsent(ONLINER))
            assertEquals(39, it.size, "wrong size")

        })
    }

    @Test
    @Throws(Exception::class)
    fun shouldDetach() {
        presenter.detachView()
        assertNull(presenter.mvpView, "not detached")
    }

    @Test
    @Throws(Exception::class)
    fun shouldShowError() {
        invalidPresenter.attachView(view)
        invalidPresenter.loadContent()
        verify(view, times(2)).flipProgress(any())
        verify(view, times(0)).loadContent(any())
        verify(view).showError(any())
        verify(view).showErrorScreen(eq(false), eq(null))
        verify(view).showErrorScreen(eq(true), any())
        verifyNoMoreInteractions(view)
    }

    private fun sectionIsAbsent(name: String) = "$name is absent"
}