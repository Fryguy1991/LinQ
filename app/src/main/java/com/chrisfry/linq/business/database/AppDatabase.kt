package com.chrisfry.linq.business.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chrisfry.linq.AppConstants
import com.chrisfry.linq.business.database.daos.TrackGroupDao
import com.chrisfry.linq.business.database.daos.TrackDao
import com.chrisfry.linq.business.database.daos.GroupToTrackLinkDao
import com.chrisfry.linq.business.database.daos.UserDao
import com.chrisfry.linq.business.database.entities.TrackGroup
import com.chrisfry.linq.business.database.entities.Track
import com.chrisfry.linq.business.database.entities.GroupToTrackLink
import com.chrisfry.linq.business.database.entities.User

@Database(entities = arrayOf(User::class, TrackGroup::class, Track::class, GroupToTrackLink::class), version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun linkDao(): TrackGroupDao
    abstract fun trackDao(): TrackDao
    abstract fun trackLinkDao(): GroupToTrackLinkDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context)
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                AppConstants.DATABASE_NAME
            ).build()
        }
    }
}