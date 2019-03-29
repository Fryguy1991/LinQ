package com.chrisfry.linq.business.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.chrisfry.linq.business.database.entities.TrackGroup

@Dao
interface TrackGroupDao {
    @Query("SELECT * FROM track_groups")
    fun getAllLinks(): List<TrackGroup>

    @Query("SELECT * FROM track_groups WHERE owner_id LIKE :userId")
    fun findLinksByUserId(userId: String): List<TrackGroup>

    @Insert
    fun insertAll(vararg trackGroups: TrackGroup)

    @Delete
    fun deleteLink(trackGroup: TrackGroup)
}