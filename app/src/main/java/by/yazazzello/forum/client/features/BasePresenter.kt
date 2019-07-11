package by.yazazzello.forum.client.features


import by.yazazzello.forum.client.helpers.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<T : BaseMvpView>(var rxSchedulers: RxSchedulers) {

    private val compositeSubscription: CompositeDisposable by lazy { CompositeDisposable() }

    var mvpView: T? = null
        private set

    fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    fun clearSubscriptions() {
        compositeSubscription.clear()
    }

    fun detachView() {
        this.mvpView = null
    }

    fun addDisposable(subscription: Disposable) {
        compositeSubscription.add(subscription)
    }

    fun addDisposable(disposable: () -> Disposable) {
        compositeSubscription.add(disposable())
    }

    fun checkViewAttached() {
        mvpView ?: throw RuntimeException("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter")
    }
}
