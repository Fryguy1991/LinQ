package com.chrisfry.linq.business.dagger.modules

import android.content.Context
import com.chrisfry.linq.business.database.AppDatabase
import com.chrisfry.linq.business.database.daos.GroupToTrackLinkDao
import com.chrisfry.linq.business.database.daos.TrackDao
import com.chrisfry.linq.business.database.daos.TrackGroupDao
import com.chrisfry.linq.business.database.daos.UserDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(private val context: Context) {

    private val appDatabase = AppDatabase.getInstance(context)

    @Singleton
    @Provides
    fun providesAppDatabase() : AppDatabase {
        return appDatabase
    }

    @Singleton
    @Provides
    fun providesUserDao() : UserDao {
        return appDatabase.userDao()
    }

    @Singleton
    @Provides
    fun providesTrackDao() : TrackDao {
        return appDatabase.trackDao()
    }

    @Singleton
    @Provides
    fun providesTrackGroupDap() : TrackGroupDao {
        return appDatabase.linkDao()
    }

    @Singleton
    @Provides
    fun providesGroupToTrackLinkDao() : GroupToTrackLinkDao {
        return appDatabase.trackLinkDao()
    }
}