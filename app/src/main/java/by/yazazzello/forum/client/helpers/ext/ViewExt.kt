package by.yazazzello.forum.client.helpers.ext

import android.databinding.BindingAdapter
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.transitionseverywhere.Fade
import com.transitionseverywhere.TransitionManager
import timber.log.Timber

/**
 * Created by yazazzello on 6/2/17.
 */

fun View.slideExit() {
    if (translationY == 0f) animate().translationY(-height.toFloat())
}

fun View.slideEnter() {
    if (translationY < 0f) animate().translationY(0f)
}

fun ViewGroup.beginDelayedFadeIn(duration: Long = 1000, revealBlock: () -> Unit) {
    Timber.d("fade in called")
    val fade = Fade()
    fade.duration = duration
    fade.interpolator = FastOutSlowInInterpolator()
    TransitionManager.beginDelayedTransition(this, fade)
    revealBlock()
}

@BindingAdapter("imageUrl", "picasso")
fun ImageView.loadImage(imageUrl: String, picasso: Picasso) {
    if (imageUrl.isNotEmpty())
        picasso.load(imageUrl).fit().into(this)
}
