package by.yazazzello.forum.client.helpers.ext

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.webkit.URLUtil



/**
 * Created by yazazzello on 5/23/17.
 */


fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

fun Context.resolveAndLaunchUrl(link: String, onFailAction:() -> Unit){
    val url = URLUtil.guessUrl(link)
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    if(checkIntentBeingHandled(intent)){
        startActivity(intent)
    } else {
        onFailAction()
    }
}

fun Context.checkIntentBeingHandled(intent: Intent)
        = packageManager.queryIntentActivities(intent, 0).isNotEmpty()