package by.yazazzello.forum.client.injection.component

import android.content.Context
import by.yazazzello.forum.client.App
import by.yazazzello.forum.client.injection.module.ActivityBuilder
import by.yazazzello.forum.client.injection.module.AppModule
import by.yazazzello.forum.client.injection.module.NetworkModule
import by.yazazzello.forum.client.injection.module.StorageModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidInjectionModule::class), (ActivityBuilder::class), (AppModule::class),
    (StorageModule::class), (NetworkModule::class)])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): ApplicationComponent
    }

    fun inject(app: App)

}
