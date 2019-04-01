package com.chrisfry.linq.business.enums

import com.chrisfry.linq.AppConstants

enum class SpotifyPlayerContextType(val stringValue: String) {
    PLAYLIST("playlist"),
    PLAYLIST_RADIO("playlist_radio"),
    QUEUE("play_queue"),
    ARTIST("artist"),
    ALBUM("album"),
    SHOW("show"),
    UNKNOWN(AppConstants.EMPTY_STRING);

    companion object {
        fun getContextType(stringValue: String): SpotifyPlayerContextType {
            for (currentValue: SpotifyPlayerContextType in values()) {
                if (stringValue == currentValue.stringValue) {
                    return currentValue
                }
            }

            return UNKNOWN
        }
    }
}