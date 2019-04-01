package com.chrisfry.linq.userinterface

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import com.chrisfry.linq.AppConstants
import com.chrisfry.linq.R
import com.chrisfry.linq.business.dagger.components.*
import com.chrisfry.linq.business.dagger.modules.AppModule
import com.chrisfry.linq.business.dagger.modules.DatabaseModule
import com.chrisfry.linq.business.dagger.modules.FrySpotifyModule
import com.chrisfry.linq.business.models.AccessModel
import javax.inject.Inject

class App : Application() {
    companion object {
        private val TAG = App::class.java.name

        // Notification channel ID
        const val CHANNEL_ID = "LinqServiceChannel"
    }

    // Single reference to app
    lateinit var appComponent: AppComponent
    // Reference to shared preferences
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.linq_service_name),
                NotificationManager.IMPORTANCE_LOW)

            val manager = getSystemService(NotificationManager::class.java) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }
}