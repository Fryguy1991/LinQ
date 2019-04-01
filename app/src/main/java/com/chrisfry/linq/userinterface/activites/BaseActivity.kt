package com.chrisfry.linq.userinterface.activites

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.chrisfry.linq.AppConstants
import com.chrisfry.linq.R
import com.chrisfry.linq.SplashFragment
import com.chrisfry.linq.business.database.AppDatabase
import com.chrisfry.linq.business.models.AccessModel
import com.chrisfry.linq.services.AccessService
import com.chrisfry.linq.services.LinqService
import com.chrisfry.linq.userinterface.App
import com.chrisfry.linq.userinterface.fragments.LoginFragment
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BaseActivity : AppCompatActivity(), LoginFragment.LoginFragmentListener, NavController.OnDestinationChangedListener, SplashFragment.SplashFragmentListener {
    companion object {
        private val TAG = BaseActivity::class.java.name

        private const val SPOTIFY_AUTH_CODE_REQUEST = 1
    }

    // Scheduler for refreshing access token
    private lateinit var scheduler: JobScheduler
    // Reference to navigation controller
    private lateinit var navController: NavController
    // Reference to activity toolbar
    private lateinit var toolbar: Toolbar
    // Configuration for app toolbar
    private lateinit var appBarConfiguration: AppBarConfiguration
    // Reference to database (allows activity to close it when destroyed)
    @Inject
    lateinit var appDatabase: AppDatabase

    private var authorizationRetries = 0

    // Intent for starting LinQ service (in order to start/stop)
    private lateinit var linqServiceIntent: Intent

    // TODO: Only listening for access so we can start the LinQ service (TEST), eventually remove this
    private val accessBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && context != null) {
                if (intent.action == AppConstants.BR_INTENT_ACCESS_TOKEN_UPDATED) {
                    linqServiceIntent = Intent(this@BaseActivity, LinqService::class.java)
                    ContextCompat.startForegroundService(this@BaseActivity, linqServiceIntent)

                    LocalBroadcastManager.getInstance(this@BaseActivity).unregisterReceiver(this)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        LocalBroadcastManager.getInstance(this).registerReceiver(accessBroadcastReceiver, IntentFilter(AppConstants.BR_INTENT_ACCESS_TOKEN_UPDATED))

        (application as App).appComponent.inject(this)

        toolbar = findViewById(R.id.app_toolbar)
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.frag_nav_host)
        navController.addOnDestinationChangedListener(this)
        appBarConfiguration = AppBarConfiguration.Builder(setOf(R.id.splashFragment, R.id.loginFragment, R.id.linkDisplayFragment)).build()

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            val settingsMenuItem = menu.findItem(R.id.settingsFragment)
            val navDestination = navController.currentDestination
            if (navDestination != null && settingsMenuItem != null) {
                when (navDestination.id) {
                    R.id.linkDisplayFragment -> {
                        settingsMenuItem.isVisible = true
                    }
                    else -> {
                        settingsMenuItem.isVisible = false
                    }
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SPOTIFY_AUTH_CODE_REQUEST -> {
                val response = AuthenticationClient.getResponse(resultCode, data)
                when (response.type) {
                    AuthenticationResponse.Type.CODE -> {
                        Log.d(TAG, "Received authorization code response")

                        if (response.code.isNullOrEmpty()) {
                            Log.e(TAG, "Authorization code is invalid.")
                            retryAuthorization()
                        } else {
                            authorizationRetries = 0
                            AccessModel.setAuthorizationCode(response.code)
                            startPeriodicAccessRefresh()
                        }
                    }
                    AuthenticationResponse.Type.ERROR -> {
                        Log.e(TAG, "Authentication error: ${response.error}")
                        retryAuthorization()
                    }
                    else -> {
                        Log.e(TAG, "Something went wrong. Not handling AuthenticationResponse type: ${response.type}")
                        retryAuthorization()
                    }
                }
            }
            else -> {
                // Not handling this request
            }
        }
    }

    private fun retryAuthorization() {
        authorizationRetries++
        if (authorizationRetries < AppConstants.AUTH_REQUEST_RETRY_LIMIT) {
            Log.e(TAG, "Re-attempting to get authorization code")
            requestSpotifyAuthorization()
        } else {
            Log.e(TAG, "Authorization failed too many times")
            // TODO: Handle this case
        }
    }

    private fun requestSpotifyAuthorization() {
        val accessScopes = arrayOf("app-remote-control")
        val builder = AuthenticationRequest.Builder(
            AppConstants.CLIENT_ID,
            AuthenticationResponse.Type.CODE,
            AppConstants.REDIRECT_URI
        )
        builder.setScopes(accessScopes)
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this, SPOTIFY_AUTH_CODE_REQUEST, request)
    }

    override fun requestAuthorization() {
        requestSpotifyAuthorization()
    }

    override fun requestAccessRefresh() {
        startPeriodicAccessRefresh()
    }

    private fun startPeriodicAccessRefresh() {
        scheduler.schedule(
            JobInfo.Builder(
                AppConstants.ACCESS_SERVICE_ID,
                ComponentName(this, AccessService::class.java)
            )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(TimeUnit.MINUTES.toMillis(20))
                .build()
        )
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        invalidateOptionsMenu()

        when(destination.id) {
            R.id.splashFragment,
            R.id.loginFragment -> {
                toolbar.visibility = View.GONE
            }
            R.id.linkDisplayFragment,
                R.id.settingsFragment,
                R.id.newLinkFragment,
                R.id.editLinkFragment -> {
                toolbar.visibility = View.VISIBLE
            }
            else -> {
                toolbar.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onDestroy() {
        // Shutdown service
        stopService(linqServiceIntent)

        // Close database before exiting application
        appDatabase.close()

        super.onDestroy()
    }
}