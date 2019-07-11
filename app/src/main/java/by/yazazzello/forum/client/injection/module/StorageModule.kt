package by.yazazzello.forum.client.injection.module

import android.content.Context
import by.yazazzello.forum.client.BuildConfig
import by.yazazzello.forum.client.data.DataManager
import by.yazazzello.forum.client.data.db.Models
import by.yazazzello.forum.client.helpers.RxSchedulers
import by.yazazzello.forum.client.network.ApiService
import dagger.Lazy
import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import javax.inject.Singleton

/**

 */
@Module()
class StorageModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): KotlinReactiveEntityStore<Persistable> {
        // override onUpgrade to handle migrating to a new version
        val source = DatabaseSource(context, Models.DEFAULT, 1)
        if (BuildConfig.DEBUG) {
            // use this in development mode to drop and recreate the tables on every upgrade
            source.setTableCreationMode(TableCreationMode.DROP_CREATE)
        }
        return KotlinReactiveEntityStore(KotlinEntityDataStore(source.configuration))
    }

    @Singleton
    @Provides
    fun provideDataManager(apiService: Lazy<ApiService>, dbStorage: KotlinReactiveEntityStore<Persistable>, rxSchedulers: RxSchedulers): DataManager {
        return DataManager(apiService, dbStorage, rxSchedulers)
    }
}
