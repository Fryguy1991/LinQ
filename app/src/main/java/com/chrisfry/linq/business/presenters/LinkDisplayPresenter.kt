package com.chrisfry.linq.business.presenters

import android.util.Log
import com.chrisfry.linq.business.dagger.components.AppComponent
import com.chrisfry.linq.business.database.daos.GroupToTrackLinkDao
import com.chrisfry.linq.business.database.daos.TrackDao
import com.chrisfry.linq.business.database.daos.TrackGroupDao
import com.chrisfry.linq.business.database.daos.UserDao
import com.chrisfry.linq.business.models.AccessModel
import com.chrisfry.linq.business.presenters.interfaces.ILinkDisplayPresenter
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyCallback
import kaaes.spotify.webapi.android.SpotifyError
import kaaes.spotify.webapi.android.models.Track
import kaaes.spotify.webapi.android.models.UserPrivate
import retrofit.client.Response
import javax.inject.Inject

class LinkDisplayPresenter(appComponent: AppComponent) : ILinkDisplayPresenter {
    companion object {
        private val TAG = this::class.java.name
    }

    init {
        appComponent.inject(this)
    }

    // API responsible for pulling data from Spotify
    @Inject
    lateinit var spotifyApi: SpotifyApi

    // Dao's required for pulling data from database
    @Inject
    lateinit var userDao: UserDao
    @Inject
    lateinit var trackGroupDao: TrackGroupDao
    @Inject
    lateinit var groupToTrackLinkDao: GroupToTrackLinkDao
    @Inject
    lateinit var trackDao: TrackDao


    private var view: ILinkDisplayView? = null

    override fun createNewLink() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun editExistingLink(trackGroupId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteLink(trackGroupId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun settingsPressed() {
        Log.d(TAG, "View settings pressed")
        view?.displaySettings()
    }

    override fun attach(view: ILinkDisplayView) {
        Log.d(TAG, "Attaching LinkDisplayPresenter to view")

        this.view = view

        spotifyApi.service.getMe(currentUserCallback)
    }

    override fun detach() {
        Log.d(TAG, "Detaching LinkDisplayPresenter from view")

        view = null
    }

    private val currentUserCallback = object : SpotifyCallback<UserPrivate>() {
        override fun success(user: UserPrivate?, response: Response?) {
            if (user != null) {
                AccessModel.setCurrentUser(user)
            }
        }

        override fun failure(spotifyError: SpotifyError?) {
            Log.e(TAG, spotifyError?.errorDetails?.message.toString())
            Log.e(TAG, "Failed to retrieve user ")
            // TODO: Retry to retrieve user?
        }
    }

    interface ILinkDisplayView {
        fun displayTrackGroups(trackGroupList: List<List<Track>>)

        fun displayUndoTime()

        fun displaySettings()

        fun navigateToNewTrackGroup()

        fun navigateToEditTrackGroup()
    }
}