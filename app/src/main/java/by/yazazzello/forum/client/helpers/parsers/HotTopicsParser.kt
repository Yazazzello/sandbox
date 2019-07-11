package by.yazazzello.forum.client.helpers.parsers

import android.net.UrlQuerySanitizer
import by.yazazzello.forum.client.data.models.ForumThreadSimple
import by.yazazzello.forum.client.features.Bindable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Pattern


/**
 * Created by yazazzello on 11/26/17.
 */
object HotTopicsParser {

    fun parse(inputString: String): List<Bindable<ForumThreadSimple>> {
        val document: Document = Jsoup.parse(inputString)
        val result = mutableListOf<Bindable<ForumThreadSimple>>()
        val threadsElems = document.select(".b-main-page-news-2__forum-news-list li")
        if (threadsElems.isEmpty()) return emptyList()

        threadsElems.forEach {
            val title = it.select("a").text()
            val messagesCount = it.select("small").text()
            val sanitizer = UrlQuerySanitizer(it.select("a").attr("href"))
            val value = sanitizer.getValue("t")
            val topicId = value?.toInt() ?: 0
            result.add(Bindable(ForumThreadSimple(title, messagesCount, topicId)))
        }

        return result
    }

    fun parseFeatured(inputString: String): List<Bindable<ForumThreadSimple>> {
        val document: Document = Jsoup.parse(inputString)
        val result = mutableListOf<Bindable<ForumThreadSimple>>()
        val threadsElems = document.select(".b-main-forum-block-2 .b-teasers-2__teaser")
        if (threadsElems.isEmpty()) return emptyList()

        threadsElems.forEach {
            val topicId = SanitizerHelper.getThreadIdFromLink(it)
            val title = it.select(".text-i").text()
            val messagesCount = it.select(".comments-number").text()
            val imgUrl = extractImageUrl(it.select("a").attr("data-style"))
            result.add(Bindable(ForumThreadSimple(title, messagesCount, topicId, imgUrl)))
        }

        return result
    }

    fun parseFeaturedWithPreview(inputString: String): List<Bindable<ForumThreadSimple>> {
        val document: Document = Jsoup.parse(inputString)
        val result = mutableListOf<Bindable<ForumThreadSimple>>()
        val threadsElems = document.select(".b-main-forum-block-2 + div > div")
        if (threadsElems.isEmpty() || threadsElems.size != 4) return emptyList()
        threadsElems.removeAt(0)//deleting first 
        var forumThreadSimple: ForumThreadSimple
        threadsElems.forEach {
            val sanitizer = UrlQuerySanitizer(it.select("h2 a").attr("href"))
            var postNumber = 0
            var topicId = 0
            var imgUrl: String?
            if (sanitizer.hasParameter("view")) {
                topicId = sanitizer.getValue("t")?.toInt() ?: 0
            } else {
                val value = sanitizer.getValue("p")?.let { it.split("#p")[0] }
                postNumber = value?.toInt() ?: 0
            }
            imgUrl = it.select("img").attr("src")
            if (imgUrl.isNullOrEmpty()) {
                imgUrl = it.select("img").attr("data-src")
            }
            val previewText: String = it.select("p").text()
            val messagesCount: String = it.select("span").text()
            val title: String = it.select("h2").text()
            forumThreadSimple = ForumThreadSimple(title, messagesCount, topicId, imgUrl, previewText)
                    .apply { post = postNumber }
            result.add(Bindable(forumThreadSimple))
        }

        return result
    }

    fun extractImageUrl(inputImageUrl: String): String {
        val pattern: Pattern = Pattern.compile("background-image:url\\((.*)\\)")
        val matcher = pattern.matcher(inputImageUrl)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return ""
    }
}