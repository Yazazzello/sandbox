package by.yazazzello.forum.client.navigation

import android.content.Context
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.features.BaseActivity
import by.yazazzello.forum.client.features.BaseFragment
import org.jetbrains.anko.bundleOf
import java.lang.ref.WeakReference

/**
 * Created by yazazzello on 8/30/16.
 */

const val KEY_EXTRA_BUNDLE = "bundle"
const val KEY_EXTRA_FORUM_CATEGORY = "extra_forum_category"
const val KEY_EXTRA_FORUM_THREAD = "extra_forum_thread"

sealed class NavigationEvent(context: Context?) {

    var contextRef: WeakReference<Context?> = WeakReference(context)

}

class ActivityNavigationEvent(context: Context?,
                              var targetActivityClass: Class<out BaseActivity>,
                              vararg params: Pair<String, Any>) : NavigationEvent(context) {
    val arguments = bundleOf(*params)
}


class FragmentNavigationEvent(context: Context?,
                              var fragment: BaseFragment<*, *>,
                              val containerId: Int = R.id.container) : NavigationEvent(context)
