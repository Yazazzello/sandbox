package by.yazazzello.forum.client.helpers.ext

import android.os.Bundle
import android.support.v4.app.Fragment
import org.jetbrains.anko.bundleOf

/**
 * Created by yazazzello on 12/11/17.
 */
inline fun <reified T : Fragment> instanceOf(vararg params: Pair<String, Any>) = T::class.java.newInstance().apply {
    arguments = bundleOf(*params)
}

inline fun <reified T : Fragment> instanceOf(bundle: Bundle) = T::class.java.newInstance().apply {
    arguments = bundle
}