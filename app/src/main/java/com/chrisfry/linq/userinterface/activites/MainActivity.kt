package com.chrisfry.linq.userinterface.activites

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import com.chrisfry.linq.userinterface.App
import com.chrisfry.linq.R
import com.chrisfry.linq.business.database.AppDatabase
import com.chrisfry.linq.business.database.daos.GroupToTrackLinkDao
import com.chrisfry.linq.business.database.daos.TrackDao
import com.chrisfry.linq.business.database.daos.TrackGroupDao
import com.chrisfry.linq.business.database.daos.UserDao
import com.chrisfry.linq.business.database.entities.TrackGroup
import com.chrisfry.linq.business.database.entities.Track
import com.chrisfry.linq.business.database.entities.GroupToTrackLink
import com.chrisfry.linq.business.database.entities.User
import javax.inject.Inject

class MainActivity : Activity() {
    companion object {
        val TAG = MainActivity::class.java.name
    }

    private var addUserIndex = 0

    private var linkIndex = 0

    private var trackLinkIndex = 0

    private var trackIndex = 0

    // Reference to database for links
    @Inject
    lateinit var database: AppDatabase

    // References to DAO's for database control
    @Inject
    lateinit var userDao: UserDao
    @Inject
    lateinit var trackDao: TrackDao
    @Inject
    lateinit var trackGroupDao: TrackGroupDao
    @Inject
    lateinit var groupToTrackLinkDao: GroupToTrackLinkDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).appComponent.inject(this)

        UpdateIndices().execute()

        setContentView(R.layout.main_activity)



        initView()
    }

    private fun initView() {
        findViewById<View>(R.id.btn_add_user).setOnClickListener {
            val newUser = User("user$addUserIndex")

            AddUserTask().execute(newUser)

            addUserIndex++
        }

        findViewById<View>(R.id.btn_delete_database).setOnClickListener {
            ClearDatabase().execute()
        }

        findViewById<View>(R.id.btn_add_link_to_last_user).setOnClickListener {
            AddLinkToLastUser().execute()
        }

        findViewById<View>(R.id.btn_add_with_link).setOnClickListener {
            val newUser = User("user$addUserIndex")

            AddUserWithLink().execute(newUser)

            addUserIndex++
        }
    }

    override fun onDestroy() {
        database.close()

        super.onDestroy()
    }

    private inner class AddUserWithLink : AsyncTask<User, Int, Int>() {
        override fun doInBackground(vararg params: User?): Int {
            for (user: User? in params) {
                if (user != null) {
                    userDao.insertAll(user)

                    val link = TrackGroup(linkIndex, "link$linkIndex", user.id, 2)
                    linkIndex++
                    trackGroupDao.insertAll(link)

                    val track1 = Track("track$trackIndex")
                    trackIndex++
                    val track2 = Track("track$trackIndex")
                    trackIndex++
                    trackDao.insertAll(track1, track2)

                    val trackLink1 = GroupToTrackLink(trackLinkIndex, link.id, track1.id, 0)
                    trackLinkIndex++
                    val trackLink2 = GroupToTrackLink(trackLinkIndex, link.id, track2.id, 1)
                    trackLinkIndex++
                    groupToTrackLinkDao.insertAll(trackLink1, trackLink2)
                }
            }

            val usersInDatabase = userDao.getAllUsers()
            Log.d(TAG, "There are now ${usersInDatabase.size} users in the database")

            var usersString = "Users in the database: "
            for (user: User in usersInDatabase) {
                usersString += "${user.id}, "
            }
            Log.d(TAG, usersString)

            return usersInDatabase.size
        }
    }

    private inner class AddLinkToLastUser : AsyncTask<Any, Int, Int>() {
        override fun doInBackground(vararg params: Any?): Int {
            val users = database.userDao().getAllUsers()
            if (users.size > 0) {
                Log.d(TAG, "Adding link to ${users[users.size - 1].id}")

                val link = TrackGroup(linkIndex, "link$linkIndex", users[users.size - 1].id, 2)
                linkIndex++
                trackGroupDao.insertAll(link)

                val track1 = Track("track$trackIndex")
                trackIndex++
                val track2 = Track("track$trackIndex")
                trackIndex++
                trackDao.insertAll(track1, track2)

                val trackLink1 = GroupToTrackLink(trackLinkIndex, link.id, track1.id, 0)
                trackLinkIndex++
                val trackLink2 = GroupToTrackLink(trackLinkIndex, link.id, track2.id, 1)
                trackLinkIndex++
                groupToTrackLinkDao.insertAll(trackLink1, trackLink2)
            } else {
                Log.d(TAG, "No user to add a link to")
            }

            return 0
        }
    }

    private inner class AddUserTask : AsyncTask<User, Int, Int>() {
        override fun doInBackground(vararg params: User?): Int {
            for (user: User? in params) {
                if (user != null) {
                    userDao.insertAll(user)
                }
            }

            val usersInDatabase = userDao.getAllUsers()
            Log.d(TAG, "There are now ${usersInDatabase.size} users in the database")

            var usersString = "Users in the database: "
            for (user: User in usersInDatabase) {
                usersString += "${user.id}, "
            }
            Log.d(TAG, usersString)

            return usersInDatabase.size
        }
    }

    private inner class UpdateIndices : AsyncTask<Any, Int, Int>() {
        override fun doInBackground(vararg params: Any?): Int {
            addUserIndex = userDao.getAllUsers().size
            linkIndex = trackGroupDao.getAllLinks().size
            trackLinkIndex = groupToTrackLinkDao.getAllLinkToTracks().size
            trackIndex = trackDao.getAllTracks().size
            return addUserIndex
        }
    }

    private inner class ClearDatabase : AsyncTask<Any, Int, Int>() {
        override fun doInBackground(vararg params: Any?): Int {
            database.clearAllTables()
            addUserIndex = 0
            trackIndex = 0
            linkIndex = 0
            trackLinkIndex = 0

            return 0
        }

    }
}