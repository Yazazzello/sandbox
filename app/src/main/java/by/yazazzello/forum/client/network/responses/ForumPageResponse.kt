package by.yazazzello.forum.client.network.responses

import by.yazazzello.forum.client.data.models.MsgPost
import by.yazazzello.forum.client.features.Bindable

/**
 * Created by yazazzello on 7/31/16.
 */

data class ForumPageResponse(var postList: List<Bindable<MsgPost>>,
                             var firstPost: MsgPost?,
                             var currentPost: Int,
                             val maxPosts: Int,
                             val threadId: Int? = null)
