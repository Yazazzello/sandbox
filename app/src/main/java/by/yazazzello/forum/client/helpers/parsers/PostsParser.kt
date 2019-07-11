package by.yazazzello.forum.client.helpers.parsers

import by.yazazzello.forum.client.data.models.Author
import by.yazazzello.forum.client.data.models.MsgPost
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.network.responses.ForumPageResponse
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**

 */

object PostsParser {

    fun parseAuthor(author: org.jsoup.select.Elements): Author {
        val userId = author.attr("data-user_id")
        val imgSrc = author.select(".ava-box a img").attr("src")
        val lvl = author.select(".mtauthor-nickname .sts-prof")[0].text()
        val nick = author.select(".mtauthor-nickname a").attr("title")
        return Author(userId, nick, lvl, imgSrc)
    }

    fun parseMsgPost(element: Element): MsgPost {
        val isFirstPost = element.hasClass("msgfirst")
        val postDate = element.select(".msgpost-date").text()
        val elementPost = element.select(".content")
//        val spoilers = elementPost.select(".msgpost-spoiler-hd")
//        if(spoilers.isNotEmpty()){
//            spoilers.forEach {
//                it.removeAttr("href")
//                it.attr("onclick", "$(this).parents('.msgpost-spoiler').find('.msgpost-spoiler-txt').toggle();" +
//                        "$(this).closest('.msgpost-spoiler').toggleClass('msgpost-spoiler-open');")
//            }
//        }
        val content = elementPost.html()
        val id = element.select(".msgpost-date").attr("id")

        return MsgPost(id, postDate, content, isFirstPost)
    }

    fun parseToMsgList(document: Document, startPosts: Int): List<Bindable<MsgPost>> {
        val resultList = ArrayList<Bindable<MsgPost>>()

        val elements = document.select(".msgpost")
        elements.forEach {
            val authorElement = it.select(".b-mtauthor")
            val author = parseAuthor(authorElement)
            val post = parseMsgPost(it)
            post.setPosts(startPosts)
            post.author = author
            resultList.add(Bindable(post))
        }

        return resultList
    }

    fun toForumPageResponse(document: Document): ForumPageResponse {
        val highlightedPages = document.select(".pages-fastnav li:not(.page-next) .hr")
        val currentPageElem = if (highlightedPages.isNotEmpty()) highlightedPages[0] else null
        val startPost = if (currentPageElem != null )
            SanitizerHelper.getIntPageFromElem(currentPageElem)
        else
            SanitizerHelper.getIntPageFromElemWithCalculation(document)

        val msgPostsList = parseToMsgList(document, startPost)
        if(msgPostsList.isEmpty()) throw IllegalStateException("no messages")
        val pageElements = document.select(".pages-fastnav li:not(.page-next) a")
        var maxPosts = 0
        var topicId = 0
        if (!pageElements.isEmpty()) {
            val pageElem = pageElements[pageElements.size - 1]
            maxPosts = SanitizerHelper.getIntPageFromElem(pageElem)
            topicId = SanitizerHelper.getThreadIdFromLink(pageElem)
        }
        var firstPost: MsgPost? = null
        val resultList: MutableList<Bindable<MsgPost>> = ArrayList()
        resultList.addAll(msgPostsList)
        if (msgPostsList.isNotEmpty() && msgPostsList[0].model.isFirstPost) {
            firstPost = resultList.removeAt(0).model
        }
        return ForumPageResponse(resultList, firstPost, startPost, maxPosts, topicId)
    }
}
