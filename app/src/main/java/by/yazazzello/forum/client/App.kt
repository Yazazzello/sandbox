package by.yazazzello.forum.client

import android.app.Activity
import android.content.Context
import android.os.StrictMode
import android.support.multidex.MultiDexApplication
import android.util.Log
import by.yazazzello.forum.client.injection.component.DaggerApplicationComponent
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import javax.inject.Inject


class App : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    companion object {

        operator fun get(context: Context): App {
            return context.applicationContext as App
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
//        Instabug.Builder(this, "fadd4cb2a859d432dbf59255e0a6fed6")
//                .setInvocationEvent(InstabugInvocationEvent.SHAKE)
//                .build()
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectFileUriExposure()
                    .detectActivityLeaks()
                    .penaltyLog()
                    .build())
        } else {
            Timber.plant(CrashTree())
        }
        RxJavaPlugins
                .setErrorHandler { throwable -> Timber.e(throwable, "rx hook ") }
        initComponent()
    }

    private fun initComponent() {
        DaggerApplicationComponent
                .builder()
                .context(this)
                .build()
                .inject(this)
    }

    inner class CrashTree : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return
            }
            Crashlytics.log(message)
            Crashlytics.logException(t ?: Exception("empty throwable"))
        }
    }
}
