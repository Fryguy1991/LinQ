package com.chrisfry.linq.business.dagger.modules

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chrisfry.linq.AppConstants
import com.chrisfry.linq.business.models.AccessModel
import dagger.Module
import dagger.Provides
import kaaes.spotify.webapi.android.SpotifyApi
import javax.inject.Singleton

@Module
class FrySpotifyModule(private val appContext: Context) : SpotifyApi() {

    private val accessBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                when (intent.action) {
                    AppConstants.BR_INTENT_ACCESS_TOKEN_UPDATED -> {
                        setAccessToken(AccessModel.getAccessToken())
                    }
                    else -> {
                        // Not handling here, do nothing
                    }
                }
            }
        }
    }

    init {
        LocalBroadcastManager.getInstance(appContext).registerReceiver(accessBroadcastReceiver, IntentFilter(AppConstants.BR_INTENT_ACCESS_TOKEN_UPDATED))
    }

    @Singleton
    @Provides
    fun providesSpotifyApi() : SpotifyApi {
        return this
    }

    fun breakdown() {
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(accessBroadcastReceiver)
    }
}