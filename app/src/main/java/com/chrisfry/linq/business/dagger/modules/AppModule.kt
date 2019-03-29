package com.chrisfry.linq.business.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import com.chrisfry.linq.AppConstants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule (private val appContext: Context) {

    @Provides
    @Singleton
    fun providesSharedPreferences() : SharedPreferences {
        return appContext.getSharedPreferences(AppConstants.SP_TITLE, Context.MODE_PRIVATE)
    }
}