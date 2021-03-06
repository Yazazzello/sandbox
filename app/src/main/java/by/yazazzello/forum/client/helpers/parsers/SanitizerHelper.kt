package by.yazazzello.forum.client.helpers.parsers

import android.net.UrlQuerySanitizer
import org.jsoup.nodes.Element

/**
 * Created by yazazzello on 12/30/16.
 */

object SanitizerHelper {

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
}
