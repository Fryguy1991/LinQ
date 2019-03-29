package com.chrisfry.linq.userinterface

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.chrisfry.linq.AppConstants
import com.chrisfry.linq.business.dagger.components.*
import com.chrisfry.linq.business.dagger.modules.AppModule
import com.chrisfry.linq.business.dagger.modules.DatabaseModule
import com.chrisfry.linq.business.dagger.modules.FrySpotifyModule
import com.chrisfry.linq.business.dagger.modules.FrySpotifyModule_ProvidesSpotifyApiFactory
import com.chrisfry.linq.business.models.AccessModel
import javax.inject.Inject

class App : Application() {
    companion object {
        private val TAG = App::class.java.name
    }

    // Single reference to app
    lateinit var appComponent: AppComponent
    // Reference to shared preferences
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .databaseModule(DatabaseModule(this))
            .frySpotifyModule(FrySpotifyModule(this))
            .build()

        appComponent.inject(this)

        // If we previously stored a refresh token load it
        val refreshToken = sharedPreferences.getString(AppConstants.SP_REFRESH_TOKEN_KEY, AppConstants.EMPTY_STRING)
        if (refreshToken != null && refreshToken.isNotEmpty()) {
            Log.d(TAG, "Loading refresh token")
            AccessModel.setRefreshToken(refreshToken)
        }
    }
}