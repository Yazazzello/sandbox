package by.yazazzello.forum.client.features.thread

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.data.models.MsgPost
import by.yazazzello.forum.client.databinding.ItemEndRetryBinding
import by.yazazzello.forum.client.databinding.ItemMsgPostBinding
import by.yazazzello.forum.client.features.BasicItemVH
import by.yazazzello.forum.client.features.Bindable
import by.yazazzello.forum.client.helpers.ext.resolveAndLaunchUrl
import by.yazazzello.forum.client.network.ApiService
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_msg_post.view.*
import org.jetbrains.anko.toast
import timber.log.Timber


class MsgItemVH constructor(val item: ItemMsgPostBinding,
                            val picasso: Picasso,
                            private val scrollSubject: PublishSubject<Int>) : BasicItemVH(item.root) {

    var disposable: Disposable? = null

    init {
        setupWebView(itemView)
    }

    override fun bind(bindable: Bindable<*>) {
        item.picasso = picasso
        val msgPost = bindable.model as MsgPost
        item.viewModel = msgPost
        item.postProgress.isVisible = true
        item.htmlText.loadDataWithBaseURL(ApiService.ENDPOINT,
                WrapperHTML(msgPost.content).formattedWebviewFallback(),
                "text/html", "utf-8", null)
        item.executePendingBindings()
        
        disposable = scrollSubject.subscribe { //WTF? why in bind?
            itemView.invalidate()
        }
    }

    companion object {
        fun setupWebView(itemView: View) {
            with(itemView.html_text) {
                setBackgroundColor(Color.TRANSPARENT)
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                settings.domStorageEnabled = true
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = true
                settings.useWideViewPort = false
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        itemView.post_progress.visibility = View.GONE
                    }

                    //
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        Timber.d("shouldOverrideUrlLoading Depr $url")
                        if (url != null) {
                            if (Uri.parse(url).lastPathSegment?.contains(".png") == true) {
                                Timber.d("shouldOverrideUrlLoading image")
                            }
                            context.resolveAndLaunchUrl(url, { context.toast("Cannot be opened") })
                        }
                        return true

                    }

                    //
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Timber.d("shouldOverrideUrlLoading " + request?.url)
                        }
                        return super.shouldOverrideUrlLoading(view, request)
                    }
                }
            }
        }
    }

}


class ProgressItemVH(val binding: ItemEndRetryBinding,
                     private val reload: ((lastPostId: Int) -> Unit)? = null,
                     val messages: MutableList<Bindable<*>>) : BasicItemVH(binding.root) {



    override fun bind(bindable: Bindable<*>) {
        (bindable.model as? ProgressableItem)?.apply {
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tapToRetryTxt.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.tapToRetryTxt.visibility = View.VISIBLE
            }
            itemView.setOnClickListener {
                if (!isLoading) {
                    if (messages.isNotEmpty() && messages.size > 2) {
                        (messages.takeLast(2)[0].model as? MsgPost)?.let {
                            reload?.invoke(it.id)
                        }
                    } else {
                        Timber.w("could not invoke reloadFunc")
                    }
                }
            }
        }
    }
}


object ThreadAdapterVh {

    inline fun <reified VH : BasicItemVH>
            createVh(parent: ViewGroup, adapter: ThreadAdapter): BasicItemVH {
        return when (VH::class) {
            MsgItemVH::class -> {
                val postBinding = DataBindingUtil.inflate<ItemMsgPostBinding>(LayoutInflater.from(parent.context),
                        R.layout.item_msg_post, parent, false)
                MsgItemVH(postBinding, adapter.picasso, adapter.scrollSubject)
            }
            ProgressItemVH::class -> {
                val binding = DataBindingUtil.inflate<ItemEndRetryBinding>(LayoutInflater.from(parent.context),
                        R.layout.item_end_retry, parent, false)
                ProgressItemVH(binding, adapter.reloadFunc, adapter.messages)
            }
            else -> throw IllegalArgumentException("unknown class " + VH::class)
        }
    }
}

open class ProgressableItem(var isLoading: Boolean)