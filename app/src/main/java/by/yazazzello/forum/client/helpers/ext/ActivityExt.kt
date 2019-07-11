package by.yazazzello.forum.client.helpers.ext

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import by.yazazzello.forum.client.R

/**
 * Created by yazazzello on 6/4/17.
 */
fun AppCompatActivity.showSnackBar(text: String, actionText: String? = null,
                                   actionListener: View.OnClickListener? = null, id:Int = R.id.container) {
    findViewById<View>(id)?.let {
        Snackbar.make(it, text, Snackbar.LENGTH_LONG).apply {
            view.setBackgroundColor(ContextCompat.getColor(this@showSnackBar, R.color.colorPrimary))
            if (!actionText.isNullOrEmpty()) setAction(actionText, actionListener)
        }.show()
    }
}