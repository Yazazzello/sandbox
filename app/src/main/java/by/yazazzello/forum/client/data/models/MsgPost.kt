package by.yazazzello.forum.client.data.models

/**

 */

class MsgPost(id: String, val postDate: String, val content: String, val isFirstPost: Boolean = false)  {

    val id: Int = Integer.valueOf(id)

    var postNumber: Int = 0
        private set

    var author: Author? = null

    fun setPosts(page: Int) {
        this.postNumber = page
    }

    override fun toString(): String {
        return "MsgPost(postDate='$postDate', content='$content', id=$id, postNumber=$postNumber, author=$author)"
    }

}
