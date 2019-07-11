package by.yazazzello.forum.client.injection.module

import android.content.Context
import by.yazazzello.forum.client.BuildConfig
import by.yazazzello.forum.client.helpers.ext.isNetworkConnected
import by.yazazzello.forum.client.injection.LiveConnectInterceptor
import by.yazazzello.forum.client.injection.OfflineConnectInterceptor
import by.yazazzello.forum.client.network.ApiService
import by.yazazzello.forum.client.network.ApiService.HEADERS.HEADER_ALLOW_OFFLINE_CACHE
import by.yazazzello.forum.client.network.ApiService.HEADERS.HEADER_FORCE_NETWORK
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import javax.inject.Singleton

/**
 * Created by yazazzello on 8/24/16.
 */
const val timeInCache: Int = 60 * 5 // minutes to read from cache
const val cacheSize: Long = 20 * 1024 * 1024 // MiB to store in cache

@Module
class NetworkModule {

    @Provides
    @Singleton
    internal fun provideApiService(retrofit: Lazy<Retrofit>): ApiService {
        return retrofit.get().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(gson: Gson, okHttpClient: Lazy<OkHttpClient>): Retrofit {
        return Retrofit.Builder()
                .baseUrl(ApiService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient.get())
                .build()
    }

    @Provides
    @Singleton
    internal fun provideOkHttp(@LiveConnectInterceptor liveInterceptor: Interceptor,
                               @OfflineConnectInterceptor offlineConnectInterceptor: Interceptor,
                               cache: Lazy<Cache>): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        val builder = OkHttpClient.Builder()
                .apply {
                    if (BuildConfig.DEBUG) {
                        addNetworkInterceptor(StethoInterceptor())
                        addInterceptor(logging)
                    }
                }
                .addNetworkInterceptor(liveInterceptor)
                .addInterceptor(offlineConnectInterceptor)
                .cache(cache.get())
        return builder.build()
    }

    @Provides
    @Singleton
    internal fun provideCache(context: Context): Cache {
        //setup cache
        val httpCacheDirectory = File(context.cacheDir, "responses")
        return Cache(httpCacheDirectory, cacheSize)
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @LiveConnectInterceptor
    internal fun provideLiveInterceptor(): Interceptor {
        return Interceptor {
            val request = it.request()
            Timber.d("injecting cache control")
            it.proceed(request).newBuilder()
                    .header("Cache-Control", "public, max-age=" + timeInCache)
                    .removeHeader("Pragma")
                    .build()
        }
    }

    @Provides
    @OfflineConnectInterceptor
    internal fun provideOfflineInterceptor(context: Context): Interceptor {
        return Interceptor {
            var request = it.request()
            request = request.newBuilder()
                    .removeHeader(HEADER_FORCE_NETWORK)
                    .apply {
                        if (request.header(HEADER_FORCE_NETWORK)?.toBoolean() == true) {
                            Timber.d("HEADER_FORCE_NETWORK  == true")
                            this.cacheControl(CacheControl.FORCE_NETWORK)
//                            this.header("Cache-Control", "public, max-age=5")
                        }
                    }.build()

            if (!context.isNetworkConnected() && request.header(HEADER_ALLOW_OFFLINE_CACHE)?.toBoolean() != false) {
                Timber.d("using the offline cache")
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200").build()
            }
            it.proceed(request)
        }
    }
}

