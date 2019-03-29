package com.chrisfry.linq.business.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.chrisfry.linq.business.database.entities.GroupToTrackLink

@Dao
interface GroupToTrackLinkDao {
    @Query("SELECT * FROM group_to_track_links")
    fun getAllLinkToTracks(): List<GroupToTrackLink>

    @Query("SELECT * FROM group_to_track_links WHERE link_id LIKE :linkId")
    fun findTrackLinksByLinkId(linkId: Int): List<GroupToTrackLink>

    @Insert
    fun insertAll(vararg users: GroupToTrackLink)

    @Delete
    fun deleteUser(user: GroupToTrackLink)
}