package by.yazazzello.forum.client.features.thread.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.setMargins
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.data.models.MsgPost
import by.yazazzello.forum.client.features.thread.MsgItemVH
import by.yazazzello.forum.client.features.thread.WrapperHTML
import by.yazazzello.forum.client.network.ApiService
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.dialog_item_msg_post.view.*
import kotlinx.android.synthetic.main.item_msg_post.view.*
import javax.inject.Inject

/**
 * Created by mikhail on 26/01/2018.
 */

class FirstPostDialog : DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var picasso: Picasso

    lateinit var model: MsgPost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dialog_item_msg_post, container, false)
        val containerView = view.msg_container
        MsgItemVH.setupWebView(containerView)
        with(model) {
            picasso.load(model.author?.imgSrc).into(containerView.user_pic)
            containerView.user_nick.text = model.author?.nick
            containerView.user_level.text = model.author?.lvl
            containerView.post_date.text = model.postDate
            containerView.post_progress.visibility = View.VISIBLE
            containerView.html_text.loadDataWithBaseURL(ApiService.ENDPOINT,
                    WrapperHTML(model.content).formattedWebviewFallback(),
                    "text/html", "utf-8", null)
            (containerView.html_text.layoutParams as ViewGroup.MarginLayoutParams).setMargins(0)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        retainInstance = true
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val window = dialog.window
            if (window != null) {
                window.setLayout(width, height)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
        }
    }

    companion object {

        fun newInstance(): FirstPostDialog {

            val args = Bundle()

            val fragment = FirstPostDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
