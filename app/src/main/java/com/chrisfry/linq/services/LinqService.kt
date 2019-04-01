package com.chrisfry.linq.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.chrisfry.linq.AppConstants
import com.chrisfry.linq.R
import com.chrisfry.linq.business.enums.SpotifyPlayerContextType
import com.chrisfry.linq.business.enums.TrackGroupState
import com.chrisfry.linq.business.models.TrackGroupDisplayModel
import com.chrisfry.linq.userinterface.App
import com.chrisfry.linq.userinterface.activites.BaseActivity
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.Empty
import com.spotify.protocol.types.PlayerContext
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track

class LinqService : Service() {
    companion object {
        private val TAG = this::class.java.name
    }

    // EXAMPLE/DEBUG/DEMO/TEST TRACK GROUPS
    // EXAMPLE LINK 1
    // The Main Squeeze - Where Do We Gone (interlude)
    private val exampleLink1First = "7ivLwpkt0OVoJmgZWwPNDa"
    // The Main Squeeze - Where Do We Gone
    private val exampleLink1Second = "6c6s5vRLvoiik440UouY48"
    private val exTrackGroup1 = TrackGroupDisplayModel(1, listOf(exampleLink1First, exampleLink1Second))

    // EXAMPLE LINK 2
    // A Day To Remember - City of Ocala
    private val exampleLink2First = "3r0BQ5d8TOS8SPsA32M2pb"
    // A Day To Remember - Right Back At It Again
    private val exampleLink2Second = "3hZVFVByKea36JhkVaKG59"
    private val exTrackGroup2 = TrackGroupDisplayModel(2, listOf(exampleLink2First, exampleLink2Second))

    // EXAMPLE LINK 3
    // Step Rockets - Limbo
    private val exampleLink3First = "5Uei4oYLVlW1EYmK7DLjaq"
    // Step Rockets - To My Grave
    private val exampleLink3Second = "0NNlvZr022L2FPyhqBg3od"
    private val exTrackGroup3 = TrackGroupDisplayModel(3, listOf(exampleLink3First, exampleLink3Second))

    // SERVICE ELEMENTS

    // SPOTIFY ELEMENTS
    // Reference to Spotify app remote object
    private lateinit var spotifyAppRemote: SpotifyAppRemote
    // Flag indicating if we've successfully connected to the Spotify app remote
    private var isAppRemoteConnected = false

    // TRACK LINKING ELEMENTS
    // List of track groups that need to be enforced
    private val trackGroupList: MutableList<TrackGroupDisplayModel> = mutableListOf()
    // Model of the track group that is being enforced
    private var cachedTrackGroup: TrackGroupDisplayModel? = null
    // Reference to current state of track group enforcement
    private var trackGroupState = TrackGroupState.NONE
    // Reference to track group index that is currently being enforced
    private var trackGroupEnforcementIndex = -1
    // Cached values for tracks
    private var cachedCurrentTrack: Track? = null
    // Flag indicating if we need to skip a track (track flagged for track group enforcement is not the first track)
    private var needToSkip = false

    // UI ELEMENTS
    // Reference to notification builder
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onBind(intent: Intent?): IBinder? {
        // Currently don't need binding for this service
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Starting LinQ service")

        // Create intent for touching foreground notification
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, BaseActivity::class.java),
            0
        )

        // TODO: Replace notification icon (placeholder)
        // Build foreground notification
        notificationBuilder = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setSubText(getString(R.string.linq_service_notification_description))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        startForeground(AppConstants.LINQ_SERVICE_ID, notificationBuilder.build())

        setupSpotifyAppRemote()

        return START_NOT_STICKY
    }

    private fun setupSpotifyAppRemote() {
        val connectionParams =
            ConnectionParams.Builder(AppConstants.CLIENT_ID).setRedirectUri(AppConstants.REDIRECT_URI)
                .showAuthView(false).build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onFailure(throwable: Throwable?) {
                if (throwable?.message != null) {
                    Log.e(TAG, "Failed to connect to Spotify app remote ${throwable.message}")
                }
            }

            override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
                if (spotifyAppRemote != null) {
                    Log.d(TAG, "Successfully connected to Spotify app remote")

                    this@LinqService.spotifyAppRemote = spotifyAppRemote
                    isAppRemoteConnected = true

                    // Listen to Spotify player state
                    this@LinqService.spotifyAppRemote.playerApi.subscribeToPlayerState()
                        .setEventCallback(playerStateCallback)

                    // Listen to Spotify player context
                    this@LinqService.spotifyAppRemote.playerApi.subscribeToPlayerContext()
                        .setEventCallback(playerContextCallback)

                    // Retrieve track group data for user
                    loadTrackGroupData()
                } else {
                    Log.e(TAG, "Received invalid Spotify app remote")
                    // TODO: Handle this case
                }
            }
        })
    }

    private fun loadTrackGroupData() {
        // TODO: Populate this with actual track group data instead of demo data
        trackGroupList.addAll(listOf(exTrackGroup1, exTrackGroup2, exTrackGroup3))
    }

    private val playerContextCallback = object : Subscription.EventCallback<PlayerContext> {
        override fun onEvent(playerContext: PlayerContext?) {
            if (playerContext != null) {
                val typeString = playerContext.type
                var contextType = SpotifyPlayerContextType.UNKNOWN
                if (typeString != null) {
                    contextType = SpotifyPlayerContextType.getContextType(playerContext.type)
                }

                val message = "PLAYER CONTEXT: " + when (contextType) {
                    SpotifyPlayerContextType.PLAYLIST -> "playlist"
                    SpotifyPlayerContextType.PLAYLIST_RADIO -> "playlist radio"
                    SpotifyPlayerContextType.QUEUE -> "queue"
                    SpotifyPlayerContextType.ARTIST -> "artist"
                    SpotifyPlayerContextType.ALBUM -> "album"
                    SpotifyPlayerContextType.SHOW -> "show"
                    SpotifyPlayerContextType.UNKNOWN -> "is unknown ($typeString)"
                }

                Log.d(TAG, message)
            } else {
                Log.e(TAG, "Invalid player context")
            }
        }
    }

    private val playerStateCallback = object : Subscription.EventCallback<PlayerState> {
        override fun onEvent(playerState: PlayerState?) {
            if (playerState != null) {
                Log.d(TAG, "PLAYER STATE EVENT")

                val track = playerState.track
                if (track != null) {
                    Log.d(TAG, "Current track: ${track.name}")

                    val currentTrack = cachedCurrentTrack

                    val currentTrackUri = track.uri
                    if (currentTrackUri != null) {

                        if (currentTrack != null && currentTrackUri == currentTrack.uri) {
                            Log.d(TAG, "Detected player state change but NOT track change")
                            // TODO: What if track is played multiple times? Need to handle this case
                        } else {
                            Log.d(TAG, "Track change detected")

                            // Update cached track
                            cachedCurrentTrack = track

                            // Extract ID from track URI
                            val currentTrackId = currentTrackUri.replace("spotify:track:", AppConstants.EMPTY_STRING)

                            when (trackGroupState) {
                                TrackGroupState.NONE -> {
                                    Log.d(TAG, "Check if track is associated with a track group")
                                    if (checkForLink(currentTrackId) >= 0) {
                                        setupTrackGroup(currentTrackId)
                                    }
                                }
                                TrackGroupState.ENFORCING -> {
                                    trackGroupEnforcementIndex++

                                    if (shouldEnforcmentStop(currentTrackId)) {
                                        Log.d(TAG, "Stopping track group enforcement (NONE)")

                                        trackGroupState = TrackGroupState.NONE
                                        cachedTrackGroup = null
                                        trackGroupEnforcementIndex = -1

                                        Log.d(TAG, "Check if track is associated with a track group")
                                        if (checkForLink(currentTrackId) >= 0) {
                                            setupTrackGroup(currentTrackId)
                                        } else {
                                            // TODO: May need to do some track cleanup here check here?
                                        }
                                    }
                                }
                                TrackGroupState.SETUP -> {
                                    val trackGroupToEnforce = cachedTrackGroup

                                    // Need to change state to enforcing when we detect we've "skipped" the track that
                                    // caused us to enter setup state
                                    if (trackGroupToEnforce != null && needToSkip && currentTrackId == trackGroupToEnforce.trackIdList[0]) {
                                        Log.d(TAG, "Track change (skip) to first track in track group (ENFORCING")
                                        needToSkip = false
                                        trackGroupState = TrackGroupState.ENFORCING
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "Current track URI is invalid")
                    }
                } else {
                    Log.e(TAG, "Current track is invalid")
                }
            } else {
                Log.e(TAG, "Invalid player state")
            }
        }
    }

    private fun setupTrackGroup(currentTrackId: String) {
        Log.d(TAG, "Setup track enforcement (SETUP)")
        trackGroupState = TrackGroupState.SETUP

        queueTracksInTrackGroup(currentTrackId)
        trackGroupEnforcementIndex = 0
    }

    private fun checkForLink(currentTrackId: String): Int {
        if (currentTrackId.isNotEmpty()) {
            for (trackGroup: TrackGroupDisplayModel in trackGroupList) {
                if (trackGroup.trackIdList.contains(currentTrackId)) {
                    Log.d(TAG, "Found a track group to enforce with ID: ${trackGroup.trackGroupId}")
                    cachedTrackGroup = trackGroup
                    return trackGroup.trackGroupId
                }
            }
            Log.d(TAG, "Did not find a track group to enforce")
        } else {
            Log.e(TAG, "Received empty track ID")
        }

        return -1
    }

    private fun queueTracksInTrackGroup(currentTrackId: String) {
        val trackGroupToEnforce = cachedTrackGroup

        when {
            currentTrackId.isEmpty() -> {
                Log.e(TAG, "Current track ID is empty")
            }
            trackGroupToEnforce == null -> {
                Log.e(TAG, "Cached track group is null")
            }
            trackGroupToEnforce.trackIdList.size < 2 -> {
                Log.e(TAG, "Invalid track group size")
            }
            else -> {
                var trackToQueueIndex = -1
                needToSkip = false

                // Result callback for making subsequent queue calls
                val queueResultCallback = object : CallResult.ResultCallback<Empty> {
                    override fun onResult(p0: Empty?) {
                        Log.d(TAG, "Successfully queued a track!")
                        trackToQueueIndex++
                        if (trackToQueueIndex < trackGroupToEnforce.trackIdList.size) {
                            spotifyAppRemote.playerApi.queue(
                                String.format(
                                    AppConstants.TRACK_URI_FORMAT,
                                    trackGroupToEnforce.trackIdList[trackToQueueIndex]
                                )
                            )
                                .setResultCallback(this)
                                .setErrorCallback {
                                    Log.e(TAG, "Error queueing track")
                                    // TODO: Handle this case
                                }
                        } else {
                            if (needToSkip) {
                                Log.d(TAG, "Finished queueing tracks. Need to skip track that initiated track group enforcement")

                                val currentTrack = cachedCurrentTrack

                                if (currentTrack != null) {

                                    val seekPosition =
                                        if (currentTrack.duration - AppConstants.SKIP_SEEK_POSITION_OFFSET_MS < 0) {
                                            0
                                        } else {
                                            currentTrack.duration - AppConstants.SKIP_SEEK_POSITION_OFFSET_MS
                                        }

                                    // Currently need to seek to end of track. playerApi.skipNext skips to next track in
                                    // the player context. Queued songs are NOT currently considered "in context"
                                    spotifyAppRemote.playerApi.seekTo(seekPosition).setResultCallback {
                                        Log.d(TAG, "Successfully seeking to end of track")
                                    }.setErrorCallback {
                                        Log.e(TAG, "Error seeking track")
                                        // TODO: Handle this case
                                    }
                                } else {
                                    Log.e(TAG, "Error: Current track is null")
                                }
                            } else {
                                Log.d(TAG, "Finished queueing tracks for track group, ready to enforce (ENFORCING)")
                                trackGroupState = TrackGroupState.ENFORCING
                            }
                        }
                    }
                }

                if (currentTrackId == trackGroupToEnforce.trackIdList[0]) {
                    Log.d(TAG, "Matched first track in track group. Queueing the rest of the tracks")
                    trackToQueueIndex = 1
                } else {
                    Log.d(TAG, "Queueing ALL tracks in the track group")
                    trackToQueueIndex = 0
                    needToSkip = true
                }

                spotifyAppRemote.playerApi.queue(
                    String.format(
                        AppConstants.TRACK_URI_FORMAT,
                        trackGroupToEnforce.trackIdList[trackToQueueIndex]
                    )
                )
                    .setResultCallback(queueResultCallback)
                    .setErrorCallback {
                        Log.e(TAG, "Error queueing track")
                        // TODO: Handle this case
                    }
            }
        }
    }

    private fun shouldEnforcmentStop(currentTrackId: String): Boolean {
        val trackGroupToEnforce = cachedTrackGroup

        when {
            currentTrackId.isEmpty() -> {
                Log.e(TAG, "Current track ID is empty")
            }
            trackGroupToEnforce == null -> {
                Log.e(TAG, "Cached track group is null")
            }
            else -> {
                // Should stop current enforcement if index has left bounds of track group OR the current track does not
                // match what should be playing (user can change track at any time)
                val shouldEnforcementStop = trackGroupEnforcementIndex >= trackGroupToEnforce.trackIdList.size
                        || trackGroupToEnforce.trackIdList[trackGroupEnforcementIndex] != currentTrackId

                if (shouldEnforcementStop) {
                    Log.d(TAG, "Link enforcement should stop")
                }

                return shouldEnforcementStop
            }
        }
        return false
    }

    override fun onDestroy() {
        Log.d(TAG, "Destroying LinQ Service")

        if (isAppRemoteConnected) {
            Log.d(TAG, "Disconnecting from Spotify app remote")
            SpotifyAppRemote.disconnect(spotifyAppRemote)
        }

        super.onDestroy()
    }
}