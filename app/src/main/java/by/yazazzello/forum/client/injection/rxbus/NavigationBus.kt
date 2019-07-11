package by.yazazzello.forum.client.injection.rxbus

import android.content.Intent
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.features.BaseFragment
import by.yazazzello.forum.client.features.featured.ForumFeaturedTopicsFragment
import by.yazazzello.forum.client.features.history.ForumHistoryThreadsFragment
import by.yazazzello.forum.client.features.main.ForumMainFragment
import by.yazazzello.forum.client.features.topic.ForumLatestTopicsFragment
import by.yazazzello.forum.client.navigation.ActivityNavigationEvent
import by.yazazzello.forum.client.navigation.FragmentNavigationEvent
import by.yazazzello.forum.client.navigation.KEY_EXTRA_BUNDLE
import by.yazazzello.forum.client.navigation.NavigationEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by yazazzello on 8/30/16.
 */

@Singleton
class NavigationBus {

    val rootMap: HashMap<Int, String> = hashMapOf(
        R.id.navigation_forum to ForumMainFragment::class.java.simpleName,
        R.id.navigation_featured to ForumFeaturedTopicsFragment::class.java.simpleName,
        R.id.navigation_latest to ForumLatestTopicsFragment::class.java.simpleName,
        R.id.navigation_history to ForumHistoryThreadsFragment::class.java.simpleName
    )

    private val subject = PublishSubject.create<NavigationEvent>()

    var selectedBottomBarId: Int = 0

    init {
        subject.throttleFirst(400, TimeUnit.MILLISECONDS)
            .onErrorResumeNext(Observable.empty<NavigationEvent>())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(NavigationObserver())

    }

    fun navigate(navigation: NavigationEvent) {
        subject.onNext(navigation)
    }

    private inner class NavigationObserver : DisposableObserver<NavigationEvent>() {

        override fun onError(e: Throwable) {
            Timber.e(e)
        }

        override fun onComplete() {
            Timber.d("on Complete")
        }

        override fun onNext(navigation: NavigationEvent) {
            navigation.contextRef.get()?.let { context ->
                when (navigation) {
                    is ActivityNavigationEvent -> {
                        val intent = Intent(context, navigation.targetActivityClass)
                        intent.putExtra(KEY_EXTRA_BUNDLE, navigation.arguments)
                        context.startActivity(intent)

                    }
                    is FragmentNavigationEvent -> {
                        (context as? AppCompatActivity)?.apply {
                            //check if it's already top
                            val tag = navigation.fragment.javaClass.simpleName

                            if (selectedBottomBarId == 0) {
                                selectedBottomBarId = navigation.fragment.getMenuItemId()
                            }

                            if (selectedBottomBarId == navigation.fragment.getMenuItemId()) {
                                supportFragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(
                                        R.anim.enter,
                                        R.anim.exit,
                                        R.anim.pop_enter,
                                        R.anim.pop_exit
                                    )
                                    .add(navigation.containerId, navigation.fragment, tag)
                                    .addToBackStack(tag)
                                    .commit()
                                //if it's not - call popbackstack and replace
                            } else {
                                if (supportFragmentManager.backStackEntryCount > 0) {
                                    supportFragmentManager.popBackStackImmediate(
                                        rootMap[selectedBottomBarId],
                                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                                    )
                                }
                                supportFragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(
                                        R.anim.enter,
                                        R.anim.exit,
                                        R.anim.pop_enter,
                                        R.anim.pop_exit
                                    )
                                    .add(navigation.containerId, navigation.fragment, tag)
                                    .addToBackStack(tag)
                                    .commit()
                            }
                        }
                    }
                }
            }
        }
    }

    fun resetFragmentStack(supportFragmentManager: FragmentManager?, id: Int) {
        val poppedUp = supportFragmentManager?.popBackStackImmediate(rootMap[id], 0)?:false
        if (!poppedUp) {
            (supportFragmentManager?.findFragmentByTag(rootMap[id]) as? BaseFragment<*,*>)?.onBottomBtnTapped()
        }
        Timber.d("resetFragmentStack() poppedUp: $poppedUp")
    }
}
