package by.yazazzello.forum.client.features

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import by.yazazzello.forum.client.helpers.RxSchedulers
import by.yazazzello.forum.client.injection.module.AppModule
import by.yazazzello.forum.client.injection.rxbus.NavigationBus
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import javax.inject.Named

abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    internal lateinit var rxSchedulers: RxSchedulers

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    internal lateinit var navigationBus: NavigationBus

    @Inject
    internal lateinit var fireAnalytics: FirebaseAnalytics

    @field:[Inject Named(AppModule.NAVIGATION)]
    internal lateinit var navigationPrefs : SharedPreferences

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        supportFragmentManager.addOnBackStackChangedListener {
            if(supportFragmentManager.fragments.isNotEmpty()) {
                (supportFragmentManager.fragments.last() as? BaseFragment<*, *>)?.updateToolbar()
            }
        }
    }

    override fun onResume() {
        fireAnalytics.setCurrentScreen(this, getCurrentScreenName(), null )
        super.onResume()
    }
    
    protected abstract fun getCurrentScreenName(): String

}
