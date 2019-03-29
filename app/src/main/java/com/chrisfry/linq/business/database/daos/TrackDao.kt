package com.chrisfry.linq.business.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.chrisfry.linq.business.database.entities.Track

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks")
    fun getAllTracks(): List<Track>

    @Query("SELECT * FROM tracks WHERE id LIKE :trackId")
    fun findTrackById(trackId: String) : Track

    @Insert
    fun insertAll(vararg links: Track)

    @Delete
    fun deleteLink(link: Track)
}