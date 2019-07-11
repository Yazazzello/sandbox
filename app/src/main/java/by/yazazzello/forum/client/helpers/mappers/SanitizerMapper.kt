package by.yazazzello.forum.client.helpers.mappers

import android.net.UrlQuerySanitizer
import by.yazazzello.forum.client.data.models.PageHolder
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*

/**
 * Created by yazazzello on 12/30/16.
 */

object SanitizerMapper {

    fun getIntPageFromElem(pageElem: Element?): Int {
        if (pageElem == null) return 0
        val sanitizer = android.net.UrlQuerySanitizer(pageElem.attr("href"))
        val start = sanitizer.getValue("start")
        return start?.toInt() ?: 0
    }

    fun getIntPageFromElemWithCalculation(pageElem: Element?): Int {
        if (pageElem == null) return 0
        val elements = pageElem.select(".exppages-ttl")
        if (elements == null || elements.isEmpty()) return 0
        val start = elements[0].text()
        return start?.let { (it.toInt() - 1) * 20 } ?: 0
    }

    fun getThreadIdFromLink(pageElem: Element?): Int {
        if (pageElem == null) return 0
        val sanitizer = UrlQuerySanitizer(pageElem.select("a").attr("href"))
        val value = sanitizer.getValue("t")
        return value?.toInt() ?: 0
    }

    fun getPagesList(pageElems: Elements?): List<PageHolder> {
        if (pageElems == null) return Collections.emptyList()
        val resultList: MutableList<PageHolder> = mutableListOf()
        val elements = pageElems.select("a")
        elements.forEach {
            val sanitizer = android.net.UrlQuerySanitizer(it.attr("href"))
            if (!sanitizer.hasParameter("start")) return@forEach
            val start = sanitizer.getValue("start")?.toInt() ?: 0
            val displayable = it.text()
            resultList.add(PageHolder(start, displayable))
        }
        return resultList
    }
}

