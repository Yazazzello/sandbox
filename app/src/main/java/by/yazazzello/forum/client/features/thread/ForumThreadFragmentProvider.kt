package by.yazazzello.forum.client.features.thread

import by.yazazzello.forum.client.features.thread.dialogs.FirstPostDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by yazazzello on 8/27/17.
 */
@Module
abstract class ForumThreadFragmentProvider {
    @ContributesAndroidInjector(modules = [(ThreadModule::class)])
    internal abstract fun provideThreadFragmentFactory(): ForumThreadFragment

    @ContributesAndroidInjector()
    internal abstract fun provideFirstDialogFragmentFactory(): FirstPostDialog
}