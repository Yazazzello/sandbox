package by.yazazzello.forum.client.injection.module

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import by.yazazzello.forum.client.helpers.RxSchedulers
import by.yazazzello.forum.client.injection.rxbus.NavigationBus
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by yazazzello on 8/26/17.
 */
@Module()
class AppModule {
    
    companion object {
        const val NAVIGATION = "navigation"
        private const val IO = "IO"
        private const val MAIN_THREAD = "MAIN_THREAD"
        private const val SINGLE_THREAD = "SINGLE_THREAD"
        private const val PICASSO_DISK_CACHE_SIZE = 1024 * 1024 * 20//10 MB
    }

    @Provides
    @Singleton
    fun providePicasso(context: Context): Picasso {
        val memoryCache = LruCache(PICASSO_DISK_CACHE_SIZE)

        return Picasso.Builder(context)
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                //                .indicatorsEnabled(BuildConfig.DEBUG)
                //                .loggingEnabled(BuildConfig.DEBUG)
                .memoryCache(memoryCache).build()
    }

    @Provides
    @Named(NAVIGATION)
    fun providePrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(AppModule.NAVIGATION, Context.MODE_PRIVATE)
    }

    @Provides
    @Named(IO)
    fun provideIoScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Named(MAIN_THREAD)
    fun provideMainScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Named(SINGLE_THREAD)
    fun provideSingleThreadScheduler(): Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    @Provides
    @Singleton
    fun provideScheduler(@Named(AppModule.IO)ioScheduler: Scheduler, @Named(AppModule.MAIN_THREAD)mainScheduler: Scheduler,
                         @Named(AppModule.SINGLE_THREAD)singleThreadScheduler: Scheduler): RxSchedulers
            = RxSchedulers(ioScheduler, mainScheduler, singleThreadScheduler)

    @Provides
    @Singleton
    internal fun provideNavBus(): NavigationBus {
        return NavigationBus()
    }

    @Provides
    @Singleton
    internal fun provideFireBaseAnalytics(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}