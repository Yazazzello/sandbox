package by.yazazzello.forum.client.helpers.parsers

import android.net.UrlQuerySanitizer
import by.yazazzello.forum.client.data.ThreadInfoHolder
import by.yazazzello.forum.client.data.models.ForumCategory
import by.yazazzello.forum.client.data.models.ForumThread
import org.jsoup.nodes.Document
import java.util.*

/**
 * Created by yazazzello on 8/21/16.
 */

object ThreadParser {

    private val baraholkaStr = "Барахолка"

    fun parseToSections(document: Document): LinkedHashMap<String, List<ForumCategory>> {
        val result = LinkedHashMap<String, List<ForumCategory>>()
        val topicsAndItems = document.select(".b-hdtopic, .b-hdtopic+ul")
        topicsAndItems.removeAt(0)
        topicsAndItems.removeAt(0)
        var currentSectionList: MutableList<ForumCategory>? = null
        for (element in topicsAndItems) {
            if (element.hasClass("b-hdtopic")) {
                val currentTitle = element.select("h2").text()
                if (currentTitle.contains(baraholkaStr)) break

                currentSectionList = ArrayList()
                result.put(currentTitle, currentSectionList)

            } else {
                val categories = element.select("li")
                for (category in categories) {
                    val forumCategory = ForumCategory()
                    val h3 = category.select("h3")
                    val countersElem = category.select("li  div:nth-child(3)")
                    val strong = countersElem.select("strong")
                    forumCategory.totalThreads = strong.first().text()
                    strong.remove()
                    forumCategory.totalMsgs = countersElem.text()
                    forumCategory.title = h3.text()
                    forumCategory.description = category.select("p").text()
                    forumCategory.url = h3.select("a").attr("href")
                    val sanitizer = UrlQuerySanitizer(forumCategory.url)
                    val forumId = sanitizer.getValue("f")
                    forumId?.let { forumCategory.forumId = Integer.parseInt(forumId) }

                    if (currentSectionList != null) {
                        currentSectionList.add(forumCategory)
                    }
                }
            }
        }

        return result
    }

    fun parseToThreads(document: Document): ThreadInfoHolder {
        val threadList = ArrayList<ForumThread>()
        val treadsElems = document.select(".b-list-topics li")
        if(treadsElems.isEmpty()) throw IllegalStateException("no threads found")
        for (treadsElem in treadsElems) {
            val isSelected = treadsElem.hasClass("lt-selected")
            val title = treadsElem.select("h3").text()
            val lastMsgAuthor = treadsElem.select(".b-lt-author").text()
            val totalMsgs = treadsElem.select(".total-msg").text()
            val navonSubj = treadsElem.select(".b-navonsubj")
            var lastPost: Int? = null
            if (navonSubj.size > 1) {
                val lastPageElem = navonSubj[navonSubj.size - 1]
                lastPost = SanitizerHelper.getIntPageFromElem(lastPageElem)
            }
            val sanitizer = UrlQuerySanitizer(treadsElem.select("a").attr("href"))
            val value = sanitizer.getValue("t")
            val topicId = value?.toInt()
            val element = ForumThread(title, lastMsgAuthor, totalMsgs, topicId ?: 0)
            element.isSelected = isSelected
            element.lastPost = lastPost ?: 0
            threadList.add(element)
        }

        val elements = document.select(".pages-fastnav li a")
        var last: String? = null
        if(elements.isNotEmpty()) {
            val lastKnownPage = elements[elements.size - 2]
            val hrefLast = lastKnownPage.attr("href")
            val sanitizer = UrlQuerySanitizer(hrefLast)
            last = sanitizer.getValue("start")
        }
        val lastPage = Integer.parseInt(last?:"0")
        return ThreadInfoHolder(lastPage, threadList)
    }
}
