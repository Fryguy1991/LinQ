package com.chrisfry.linq.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chrisfry.linq.AppConstants
import com.chrisfry.linq.business.enums.AuthRequestType
import com.chrisfry.linq.business.models.AccessModel
import com.chrisfry.linq.userinterface.App
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

class AccessService : JobService() {
    companion object {
        val TAG = AccessService::class.java.name
    }

    // Count for number of retries for authorization
    private var authorizationRetries = 0
    // Type representation for the sent authorization request
    private var requestType = AuthRequestType.NONE
    // Reference to shared preferences
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onStartJob(params: JobParameters?): Boolean {

        (application as App).appComponent.inject(this)
        requestAccess()
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    private fun requestAccess() {
        // TODO: Should be able to support requesting multiple AWS instances
        val client = OkHttpClient()
        val codeToSend: String

        // Determine if we're refreshing access or getting completely new tokens
        if (AccessModel.getRefreshToken().isNotEmpty()) {
            codeToSend = AccessModel.getRefreshToken()
            requestType = AuthRequestType.TOKEN_REFRESH
        } else if (AccessModel.getAuthorizationCode().isNotEmpty()) {
            codeToSend = AccessModel.getAuthorizationCode()
            requestType = AuthRequestType.NEW_TOKENS
        } else {
            Log.e(TAG, "Error: Authorization code has not been retrieved")
            return
        }

        val request = Request.Builder().url(String.format(AppConstants.AWS_URL_STRING, codeToSend)).build()
        client.newCall(request).enqueue(authCallback)
    }

    private val authCallback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e(TAG, "Access request failed: ${e.message}")
            retryAccessRequest()
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body()?.string()

            if (response.isSuccessful && !responseString.isNullOrEmpty()) {
                val bodyJson = JSONObject(responseString).getJSONObject(AppConstants.JSON_BODY_KEY)

                when (requestType) {
                    AuthRequestType.AUTHORIZATION,
                    AuthRequestType.NONE -> {
                        Log.e(TAG, "Error, not currently handling AUTHORIZATION or NONE cases")
                    }
                    AuthRequestType.NEW_TOKENS -> {
                        val refreshToken = bodyJson.getString(AppConstants.JSON_REFRESH_TOKEN_KEY)

                        Log.d(TAG, "Received refresh token:\nRefresh Token: $refreshToken")

                        // Store refresh token
                        AccessModel.setRefreshToken(refreshToken)

                        // Clear authorization code as it may not be valid in the future
                        AccessModel.setAuthorizationCode(AppConstants.EMPTY_STRING)

                        // Store refresh token in shared preferences
                        sharedPreferences.edit().putString(AppConstants.SP_REFRESH_TOKEN_KEY, refreshToken).apply()

                        storeNewAccessToken(bodyJson)
                    }
                    AuthRequestType.TOKEN_REFRESH -> {
                        storeNewAccessToken(bodyJson)
                    }
                }
                requestType = AuthRequestType.NONE
                authorizationRetries = 0
            } else {
                Log.e(TAG, "Response string was null or empty")
                retryAccessRequest()
            }
        }
    }

    private fun storeNewAccessToken(bodyJson: JSONObject) {
        val accessToken = bodyJson.getString(AppConstants.JSON_ACCESS_TOKEN_KEY)
        val expiresIn = bodyJson.getInt(AppConstants.JSON_EXPIRES_IN_KEY)

        Log.d(TAG, "Received new access token:\nAccess Token: $accessToken\nExpires In: $expiresIn seconds")

        // Calculate when access token expires (response "ExpiresIn" is in seconds, subtract a minute to worry less about timing)
        val expireTime = SystemClock.elapsedRealtime() + (expiresIn - 60) * 1000
        // Set access token and expire time into model
        AccessModel.setAccess(accessToken, expireTime)

        // Broadcast that the access code has been updated
        newAccessTokenBroadcast()
    }

    private fun retryAccessRequest() {
        authorizationRetries++
        if (authorizationRetries < AppConstants.AUTH_REQUEST_RETRY_LIMIT) {
            Log.e(TAG, "Retrying access request")
            requestType = AuthRequestType.NONE
            requestAccess()
        } else {
            Log.e(TAG, "Access request failed too many times")
            requestType = AuthRequestType.NONE
            // TODO: Handle this case
        }
    }

    private fun newAccessTokenBroadcast() {
        Log.d(TAG, "Broadcasting that access token has been updated")
        val accessRefreshIntent = Intent(AppConstants.BR_INTENT_ACCESS_TOKEN_UPDATED)
        LocalBroadcastManager.getInstance(this@AccessService).sendBroadcast(accessRefreshIntent)
    }
}