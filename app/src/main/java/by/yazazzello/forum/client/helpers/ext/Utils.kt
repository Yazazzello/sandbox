package by.yazazzello.forum.client.helpers.ext

import android.content.res.Resources


val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
