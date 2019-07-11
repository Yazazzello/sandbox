package by.yazazzello.forum.client.features

import by.yazazzello.forum.client.features.featured.ForumFeaturedTopicsFragment
import by.yazazzello.forum.client.features.history.ForumHistoryThreadsFragment
import by.yazazzello.forum.client.features.main.ForumMainFragment
import by.yazazzello.forum.client.features.main.ForumMainFragmentModule
import by.yazazzello.forum.client.features.topic.ForumLatestTopicsFragment
import by.yazazzello.forum.client.features.topic.ForumTopicsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by yazazzello on 8/27/17.
 */
@Module
abstract class ForumFragmentsProvider {
    @ContributesAndroidInjector(modules = arrayOf(ForumMainFragmentModule::class))
    internal abstract fun provideMainFragmentFactory(): ForumMainFragment

    @ContributesAndroidInjector()
    internal abstract fun provideTopicsFragmentFactory(): ForumTopicsFragment

    @ContributesAndroidInjector()
    internal abstract fun provideLatestTopicsFragmentFactory(): ForumLatestTopicsFragment

    @ContributesAndroidInjector()
    internal abstract fun provideHotTopicsFragmentFactory(): ForumFeaturedTopicsFragment

    @ContributesAndroidInjector()
    internal abstract fun provideHistoryFragmentFactory(): ForumHistoryThreadsFragment

}