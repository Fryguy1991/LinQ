package com.chrisfry.linq.business.presenters

import android.content.SharedPreferences
import android.util.Log
import com.chrisfry.linq.AppConstants
import com.chrisfry.linq.business.dagger.components.AppComponent
import com.chrisfry.linq.business.models.AccessModel
import com.chrisfry.linq.business.presenters.interfaces.ISettingsPresenter
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyCallback
import kaaes.spotify.webapi.android.SpotifyError
import kaaes.spotify.webapi.android.models.UserPrivate
import retrofit.client.Response
import javax.inject.Inject

class SettingsPresenter(appcomponent: AppComponent) : ISettingsPresenter {
    companion object {
        private val TAG = this::class.java.name
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var spotifyApi: SpotifyApi

    init {
        appcomponent.inject(this)
    }

    private var view: ISettingsView? = null

    override fun enforceLinksChanged(shouldBeActive: Boolean) {
        Log.d(TAG, "Changing enforce links to: $shouldBeActive")
        sharedPreferences.edit().putBoolean(AppConstants.SP_ENFORCE_LINKS_KEY, shouldBeActive).apply()
    }

    override fun requestRemoveAllLinks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logoutPressed() {
        Log.d(TAG, "User pressed log out. Clear access values and return to log in screen")
        sharedPreferences.edit().putString(AppConstants.SP_REFRESH_TOKEN_KEY, AppConstants.EMPTY_STRING).apply()
        AccessModel.reset()

        view?.navigateToLogin()
    }

    override fun attach(view: ISettingsView) {
        Log.d(TAG, "Attaching SettingsPresenter to view")
        this.view = view

        // TODO: Link this default value to the value present in bools file
        view.displayEnforceLinks(sharedPreferences.getBoolean(AppConstants.SP_ENFORCE_LINKS_KEY, true))

        val currentUser = AccessModel.getCurrentUser()
        if (currentUser == null) {
            spotifyApi.service.getMe(currentUserCallback)
        } else {
            displayUserName(currentUser)
        }
    }

    private fun displayUserName(user: UserPrivate) {
        var name = user.display_name
        if (name == null) {
            name = user.id

            if (name == null) {
                name = AppConstants.EMPTY_STRING
            }
        }
        view?.displayUser(name)
    }

    override fun detach() {
        Log.d(TAG, "Detaching SettingsPresenter from view")
        view = null
    }

    private val currentUserCallback = object : SpotifyCallback<UserPrivate>() {
        override fun success(user: UserPrivate?, response: Response?) {
            if (user != null) {
                AccessModel.setCurrentUser(user)
                displayUserName(user)
            }
        }

        override fun failure(spotifyError: SpotifyError?) {
            Log.e(TAG, spotifyError?.errorDetails?.message.toString())
            Log.e(TAG, "Failed to retrieve user ")
            // TODO: Retry to retrieve user?
        }

    }

    interface ISettingsView {
        fun displayEnforceLinks(shouldBeActive: Boolean)

        fun displayUser(userName: String)

        fun navigateToLogin()
    }
}