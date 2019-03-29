package com.chrisfry.linq

class AppConstants {
    companion object {
        const val CLIENT_ID = "669c02fdcdd042dea761adf8ed42fc87"
        const val REDIRECT_URI = "linqredirect://callback"

        const val SPOTIFY_PACKAGE_NAME = "com.spotify.music"
        const val SPOTIFY_ACTIVITY_NAME = "com.spotify.music.MainActivity"

        // Service ID's
        const val ACCESS_SERVICE_ID = 0

        // Database constants
        const val DATABASE_NAME = "TrackLinks.db"

        // AWS request values
        const val AWS_SERVER_URL = "http://54.86.80.241/"
        const val AWS_URL_STRING = AWS_SERVER_URL + "app/linq/code/%s"

        // Auth JSON Keys
        const val JSON_BODY_KEY = "body"
        const val JSON_ACCESS_TOKEN_KEY = "access_token"
        const val JSON_REFRESH_TOKEN_KEY = "refresh_token"
        const val JSON_EXPIRES_IN_KEY = "expires_in"

        // Retry limit when attempting to gain Spotify access
        const val AUTH_REQUEST_RETRY_LIMIT = 3

        // Broadcast Receiver Intent Strings
        const val BR_INTENT_ACCESS_TOKEN_UPDATED = "access_token_updated"

        // Shared Preferences Keys
        const val SP_TITLE = "Linq_Shared_Preferences"
        const val SP_REFRESH_TOKEN_KEY = "stored_refresh_token"
        const val SP_USER_ID_KEY = "stored_current_user_id"
        const val SP_ENFORCE_LINKS_KEY = "stored_enforce_links"

        const val EMPTY_STRING = ""
    }
}