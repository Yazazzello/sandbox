package by.yazazzello.forum.client.injection.module

import by.yazazzello.forum.client.features.ForumFragmentsProvider
import by.yazazzello.forum.client.features.MainActivity
import by.yazazzello.forum.client.features.main.HomeModule
import by.yazazzello.forum.client.features.thread.ForumThreadActivity
import by.yazazzello.forum.client.features.thread.ForumThreadFragmentProvider
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by yazazzello on 8/27/17.
 */
@Module
abstract class ActivityBuilder {
    
    @ContributesAndroidInjector(modules = [(HomeModule::class), (ForumFragmentsProvider::class)])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [(ForumThreadFragmentProvider::class)])
    internal abstract fun bindThreadActivity(): ForumThreadActivity

}