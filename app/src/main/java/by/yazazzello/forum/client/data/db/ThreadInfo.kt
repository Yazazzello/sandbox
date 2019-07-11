

package by.yazazzello.forum.client.data.db

import android.os.Parcelable
import io.requery.*
import java.util.*

@Entity
interface ThreadInfo : Parcelable, Persistable {
    @get:Key
    @get:Generated
    var id: Int
    
    @get:Column(unique = true)
    var threadId: Int
    var postNumber: Int
    var scrolledPosition: Int
    var title: String
    var date: Date
}
