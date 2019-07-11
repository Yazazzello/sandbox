package by.yazazzello.forum.client.features

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import by.yazazzello.forum.client.BuildConfig
import by.yazazzello.forum.client.helpers.ToolbarManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.recycler_view_with_swipe_refresh.*
import kotlinx.android.synthetic.main.toolbar_simple_layout.*
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by yazazzello on 7/28/16.
 * basic fragment
 */

abstract class BaseFragment<T : BasePresenter<V>, V : BaseMvpView> : Fragment(), ToolbarManager, LoadingMvpView {

    override val mToolbar: Toolbar by lazy { (activity as? BaseActivity)?.toolbar!! }

    @Inject
    lateinit var presenter: T

    protected abstract fun initiateViews()

    protected abstract fun getLayoutId(): Int

    abstract fun getMenuItemId(): Int

    protected abstract fun readyToCall()

    abstract fun updateToolbar()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        presenter.attachView(this as V)
        initiateViews()
        readyToCall()
        updateToolbar()
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume() called with: ${javaClass.simpleName}")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop() called with: ${javaClass.simpleName}")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Timber.d("onHiddenChanged() $hidden called with: ${javaClass.simpleName}")
    }

    override fun onDestroyView() {
        presenter.clearSubscriptions()
        presenter.detachView()
        super.onDestroyView()
    }

    override fun showErrorScreen(shouldShow: Boolean, lottieJson: String?) {
        recycler_view.isVisible = !shouldShow
        error_view_lottie.isVisible = shouldShow
        error_view_lottie.cancelAnimation()
        if (lottieJson == null) return
        error_view_lottie.setAnimation(lottieJson)

        if (shouldShow) error_view_lottie.playAnimation()
    }

    override fun showError(msg: String) {
        if (BuildConfig.DEBUG) activity?.toast(msg)
    }

    override fun flipProgress(isRefreshing: Boolean) {
        swipe_refresh.isRefreshing = isRefreshing
    }

    open fun onBottomBtnTapped() {}
}
