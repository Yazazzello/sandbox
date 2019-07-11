package by.yazazzello.forum.client.network


import by.yazazzello.forum.client.network.ApiService.HEADERS.HEADER_ALLOW_OFFLINE_CACHE
import by.yazazzello.forum.client.network.ApiService.HEADERS.HEADER_FORCE_NETWORK
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    companion object {

        const val ENDPOINT = "https://forum.onliner.by/"
    }

    @GET(value = "viewtopic.php")
    fun getTopic(@Header(value = HEADER_FORCE_NETWORK) forceIgnoreCache: Boolean,
                 @Header(value = HEADER_ALLOW_OFFLINE_CACHE) allowOfflineCache: Boolean,
                 @Query("t") topic: Int, @Query("start") startPost: Int): Single<ResponseBody>
    
    @GET(value = "viewtopic.php")
    fun getTopicByPost(@Header(value = HEADER_FORCE_NETWORK) forceIgnoreCache: Boolean,
                       @Header(value = HEADER_ALLOW_OFFLINE_CACHE) allowOfflineCache: Boolean,
                       @Query("p") post: Int): Single<ResponseBody>
    
    @GET(value = "viewforum.php")
    fun getForum(@Header(value = HEADER_FORCE_NETWORK) forceIgnoreCache: Boolean,
                 @Query("f") forumId: Int, @Query("start") startItem: Int): Single<ResponseBody>

    @GET(value = "search.php?type=lastposts&time=86400")
    fun getLastPosts(@Header(value = HEADER_FORCE_NETWORK) forceIgnoreCache: Boolean, @Query("start") startItem: Int): Single<ResponseBody>
    
    @GET("/")
    fun getMainForum(@Header(value = HEADER_FORCE_NETWORK) forceNetwork: Boolean): Single<ResponseBody>

    @GET
    fun getPageByUrl(@Header(value = HEADER_FORCE_NETWORK) forceNetwork: Boolean, @Url url: String): Single<ResponseBody>

    object HEADERS {
        
        const val HEADER_ALLOW_OFFLINE_CACHE = "X-Allow-Offline-Cache"

        const val HEADER_FORCE_NETWORK = "X-Force-Network"
    }

}
