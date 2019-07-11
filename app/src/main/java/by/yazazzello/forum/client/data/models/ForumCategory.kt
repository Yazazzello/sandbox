package by.yazazzello.forum.client.data.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by yazazzello on 8/21/16.
 */

data class ForumCategory @JvmOverloads constructor(
        var title: String = "",
        var totalMsgs: String = "",
        var forumId: Int = 0,
        var description: String = "",
        var url: String = "",
        var totalThreads: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(totalMsgs)
        writeInt(forumId)
        writeString(description)
        writeString(url)
        writeString(totalThreads)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ForumCategory> = object : Parcelable.Creator<ForumCategory> {
            override fun createFromParcel(source: Parcel): ForumCategory = ForumCategory(source)
            override fun newArray(size: Int): Array<ForumCategory?> = arrayOfNulls(size)
        }
    }
}

