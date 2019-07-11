package by.yazazzello.forum.client.data.models

import android.os.Parcel
import android.os.Parcelable

open class ForumThreadSimple : Parcelable, Comparable<ForumThreadSimple> {
    var title: String
    var totalMsg: String
    var topicId: Int
    var imgUrl: String? = null
    var previewText: String? = null
    var post: Int? = null
    var pages: List<PageHolder>? = null
    var pageHolderClicked: PageHolder? = null

    constructor(source: Parcel) {
        this.title = source.readString()
        this.totalMsg = source.readString()
        this.topicId = source.readInt()
        this.imgUrl = source.readString()
        this.previewText = source.readString()
        this.post = source.readValue(Int::class.java.classLoader) as Int?
        source.readList(pages, PageHolder::class.java.classLoader)
        this.pageHolderClicked = source.readParcelable(PageHolder::class.java.classLoader)
    }
                                                 
    constructor(title: String, totalMsg: String, topicId: Int, imgUrl: String? = null, previewText: String? = null,
                pPages: List<PageHolder>? = null, pPageHolderClicked: PageHolder? = null) {
        this.title = title
        this.totalMsg = totalMsg
        this.topicId = topicId
        this.imgUrl = imgUrl
        this.previewText = previewText
        if (!imgUrl.isNullOrEmpty() && !previewText.isNullOrEmpty()) {
            type = IMAGE_AND_TEXT
        } else if (!imgUrl.isNullOrEmpty()) {
            type = IMAGE_AND_TITLE
        }
        this.pages = pPages
        this.pageHolderClicked = pPageHolderClicked
    }

    override fun compareTo(other: ForumThreadSimple): Int {
        return topicId.compareTo(other.topicId)
    }

    var type: Int = SIMPLE_TITLE

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(totalMsg)
        writeInt(topicId)
        writeString(imgUrl)
        writeString(previewText)
        writeValue(post)
        writeList(pages)
        writeParcelable(pageHolderClicked, flags)
    }

    override fun toString(): String {
        return "ForumThreadSimple(title='$title', totalMsg='$totalMsg', topicId=$topicId, imgUrl=$imgUrl, previewText=$previewText, type=$type)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ForumThreadSimple

        if (topicId != other.topicId) return false

        return true
    }

    override fun hashCode(): Int {
        return topicId
    }

    companion object {
        const val SIMPLE_TITLE: Int = 0
        const val IMAGE_AND_TITLE: Int = 1
        const val IMAGE_AND_TEXT: Int = 2
        const val SECTION_TITLE: Int = 3

        @JvmField
        val CREATOR: Parcelable.Creator<ForumThreadSimple> = object : Parcelable.Creator<ForumThreadSimple> {
            override fun createFromParcel(source: Parcel): ForumThreadSimple = ForumThreadSimple(source)
            override fun newArray(size: Int): Array<ForumThreadSimple?> = arrayOfNulls(size)
        }
    }

}

/**
 * Created by yazazzello on 8/31/16.
 */

class ForumThread(
        title: String, var lastMsgAuthorName: String, totalMsg: String, topicId: Int,
        var isSelected: Boolean = false, var lastPost: Int = 0, pPages: List<PageHolder>? = null, pPageHolderClicked:
        PageHolder? = null
) : Parcelable, ForumThreadSimple(title, totalMsg, topicId, pPages = pPages, pPageHolderClicked = pPageHolderClicked) {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            1 == source.readInt(),
            source.readInt(),
            arrayListOf<PageHolder>().apply {
                source.readList(this, PageHolder::class.java.classLoader)
            },
            source.readParcelable(PageHolder::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(lastMsgAuthorName)
        writeString(totalMsg)
        writeInt(topicId)
        writeInt((if (isSelected) 1 else 0))
        writeInt(lastPost)
        writeList(pages)
        writeParcelable(pageHolderClicked, flags)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ForumThread> = object : Parcelable.Creator<ForumThread> {
            override fun createFromParcel(source: Parcel): ForumThread = ForumThread(source)
            override fun newArray(size: Int): Array<ForumThread?> = arrayOfNulls(size)
        }
    }
}

class PageHolder(public val intParam: Int, public val displayable: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(intParam)
        writeString(displayable)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PageHolder> = object : Parcelable.Creator<PageHolder> {
            override fun createFromParcel(source: Parcel): PageHolder = PageHolder(source)
            override fun newArray(size: Int): Array<PageHolder?> = arrayOfNulls(size)
        }
    }
}

