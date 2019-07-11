package by.yazazzello.forum.client.helpers

import retrofit2.HttpException

object LottieErrorMapper {
    fun getLottieByError(it: Throwable?): String {
        return when (it) {
            is HttpException -> "error_message.json"
            else -> "empty_box.json"
        }
    }
}