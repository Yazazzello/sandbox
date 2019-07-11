package by.yazazzello.forum.client.features.thread

import android.os.Bundle
import android.view.MenuItem
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.features.BaseActivity
import by.yazazzello.forum.client.helpers.ext.instanceOf
import by.yazazzello.forum.client.navigation.KEY_EXTRA_BUNDLE
import timber.log.Timber

/**
 * Created by yazazzello on 8/31/16.
 */

class ForumThreadActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate() called with: savedInstanceState = [$savedInstanceState]")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.toolbar_coordinator_container_activity)
        val bundleExtra = intent.getBundleExtra(KEY_EXTRA_BUNDLE)
        
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, instanceOf<ForumThreadFragment>(bundleExtra))
                    .commit()
        }
    }

    override fun getCurrentScreenName(): String {
        return "Forum Thread Screen"
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
