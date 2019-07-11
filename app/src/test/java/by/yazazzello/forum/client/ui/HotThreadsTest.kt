package by.yazazzello.forum.client.ui

import by.yazazzello.forum.client.data.models.ForumThreadSimple
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.helpers.mappers.HotTopicsMapper
import by.yazazzello.forum.client.utils.TestTools
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by yazazzello on 11/26/17.
 */
class HotThreadsTest {

    @Test
    fun `parsing correctly`() {
        val list: List<Bindable<ForumThreadSimple>> = HotTopicsMapper.map(ResponseBody.create(MediaType.parse("text/html"),
                TestTools.readFile("index_main.html")).string())
        with(list) {
            assertEquals("Клуб любителей игры World of Tanks", get(0).model.title)
            assertEquals("178,8K", get(0).model.totalMsg)
            assertEquals(size, 7)
        }
    }

    @Test
    fun `parsing correctly featured`() {
        val list: List<Bindable<ForumThreadSimple>> = HotTopicsMapper.mapFeatured(ResponseBody.create(MediaType.parse("text/html"),
            TestTools.readFile("index_main.html")).string())
        with(list) {
            assertEquals("Пришла зима. Как завестись?", get(0).model.title)
            assertEquals("34 454 сообщения", get(0).model.totalMsg)
            assertEquals("https://content.onliner.by/mainpage/forum/teaserbanner/eec5864a00797bfa2a5348bb04d49d94.jpeg", get(0).model.imgUrl)
            assertEquals(5, size)
        }
    }

    @Test
    fun `parsing correctly featured with preview`() {
        val list: List<Bindable<ForumThreadSimple>> = HotTopicsMapper
                .mapFeaturedWithPreview(ResponseBody.create(MediaType.parse("text/html"),
                TestTools.readFile("index_main.html")).string())

        assertEquals(3, list.size)
    }

    @Test
    fun `extract image url`(){
        val inputImageUrl = "background-image:url(https://content.onliner.by/mainpage/forum/teaserbanner/c7369e84ef1d0740fced49a6762df131.jpeg)"
        val clearStr = HotTopicsMapper.extractImageUrl(inputImageUrl)
        assertEquals("https://content.onliner.by/mainpage/forum/teaserbanner/c7369e84ef1d0740fced49a6762df131.jpeg", clearStr)
    }
}