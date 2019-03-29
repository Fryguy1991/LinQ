package com.chrisfry.linq.business.models

import kaaes.spotify.webapi.android.models.UserPrivate

class AccessModel {
    companion object {
        // Authorization code for requesting an access token and refresh token
        private var authorizationCode = ""
        // Refresh token for requesting a new access token
        private var refreshToken = ""
        // Access token for access to Spotify services
        private var accessToken = ""
        // Time when this access token expires
        private var accessExpireTime: Long = -1
        // Spotify user object of who has access
        private var currentUser: UserPrivate? = null

        fun setAccess(token: String, expireTime: Long) {
            if (token.isEmpty()) {
                this.accessToken = ""
                this.accessExpireTime = -1
            } else {
                this.accessToken = token
                this.accessExpireTime = expireTime
            }
        }

        fun reset() {
            authorizationCode = ""
            refreshToken = ""
            accessToken = ""
            accessExpireTime = -1
            currentUser = null

        }

        fun getAccessToken(): String {
            return accessToken
        }

        fun getAccessExpireTime(): Long {
            return accessExpireTime
        }

        fun getAuthorizationCode(): String {
            return authorizationCode
        }

        fun setAuthorizationCode(authorizationCode: String) {
            this.authorizationCode = authorizationCode
        }

        fun setRefreshToken(refreshToken: String) {
            this.refreshToken = refreshToken
        }

        fun getRefreshToken(): String {
            return refreshToken
        }

        fun setCurrentUser(user: UserPrivate?) {
            this.currentUser = user
        }

        fun getCurrentUser(): UserPrivate? {
            return currentUser
        }
    }
}