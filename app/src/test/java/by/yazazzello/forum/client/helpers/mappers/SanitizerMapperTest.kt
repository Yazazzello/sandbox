package by.yazazzello.forum.client.helpers.mappers

import by.yazazzello.forum.client.utils.TestTools
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by mikhail on 12/03/2018.
 */
class SanitizerMapperTest {
    @Test
    fun `page should be zero`() {
        val document = Jsoup.parse(TestTools.readFile("one_page_thread.html"))
        val page = SanitizerMapper.getIntPageFromElemWithCalculation(document as Element?)
        assertEquals(0, page)
    }




}