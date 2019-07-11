package by.yazazzello.forum.client.features

import android.os.Bundle
import androidx.core.content.edit
import by.yazazzello.forum.client.R
import by.yazazzello.forum.client.features.featured.ForumFeaturedTopicsFragment
import by.yazazzello.forum.client.features.history.ForumHistoryThreadsFragment
import by.yazazzello.forum.client.features.main.ForumMainFragment
import by.yazazzello.forum.client.features.topic.ForumLatestTopicsFragment
import by.yazazzello.forum.client.helpers.ext.instanceOf
import by.yazazzello.forum.client.helpers.ext.showSnackBar
import by.yazazzello.forum.client.helpers.maps
import by.yazazzello.forum.client.navigation.FragmentNavigationEvent
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

private const val MENU = "menu_id"

/**
 * Created by yazazzello on 8/30/16.
 */

class MainActivity : BaseActivity() {
    
    private var backPressTwice: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.fragments.isNotEmpty()) {
                (supportFragmentManager.fragments.last() as? BaseFragment<*, *>)?.let {
                    bottom_bar.menu.findItem(it.getMenuItemId())?.isChecked = true
                    navigationBus.selectedBottomBarId = bottom_bar.selectedItemId
                }
            }
        }

        bottom_bar.setOnNavigationItemSelectedListener {
            if (it.itemId == bottom_bar.selectedItemId) {
                navigationBus.resetFragmentStack(supportFragmentManager, it.itemId)
                return@setOnNavigationItemSelectedListener true
            }

            when (it.itemId) {
                R.id.navigation_forum -> {
                    navigationPrefs.edit {
                        putInt(MENU, 1)
                    }
                    navigationBus.navigate(FragmentNavigationEvent(this,
                            instanceOf<ForumMainFragment>()))
                    true
                }
                R.id.navigation_featured -> {
                    navigationPrefs.edit {
                        putInt(MENU, 2)
                    }
                    navigationBus.navigate(FragmentNavigationEvent(this,
                            instanceOf<ForumFeaturedTopicsFragment>()))
                    true
                }
                R.id.navigation_latest -> {
                    navigationPrefs.edit {
                        putInt(MENU, 3)
                    }
                    navigationBus.navigate(FragmentNavigationEvent(this,
                            instanceOf<ForumLatestTopicsFragment>()))
                    true
                }
                R.id.navigation_history -> {
                    navigationPrefs.edit {
                        putInt(MENU, 4)
                    }
                    navigationBus.navigate(FragmentNavigationEvent(this,
                            instanceOf<ForumHistoryThreadsFragment>()))
                    true
                }
                else -> false
            }

        }

        if (savedInstanceState == null) {
            val lastId = maps.NAVIGATION_MAP[navigationPrefs.getInt(MENU, 1)]
            bottom_bar.selectedItemId = lastId ?: R.id.navigation_forum
        }
    }

    override fun getCurrentScreenName(): String {
        return "Main Forum Screen"
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            return
        }

        if (backPressTwice) {
            finish()
        } else {
            backPressTwice = true
            showSnackBar(getString(R.string.click_one_more_time_to_exit))
            Observable.timer(2, TimeUnit.SECONDS)
                    .subscribeOn(rxSchedulers.ioScheduler)
                    .observeOn(rxSchedulers.mainThreadScheduler)
                    .subscribe { backPressTwice = false }
        }
    }

}
