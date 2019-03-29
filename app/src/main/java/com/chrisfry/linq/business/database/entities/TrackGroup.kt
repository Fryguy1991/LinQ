package com.chrisfry.linq.business.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Model reference for the parent of a group of tracks that are linked
 *
 * User HAS ZERO OR MORE TrackGroup
 */
@Entity(tableName = "track_groups",
    foreignKeys = arrayOf(
        ForeignKey(entity = User::class, parentColumns = arrayOf("id"), childColumns = arrayOf("owner_id"), onDelete = ForeignKey.CASCADE)
    ))
data class TrackGroup (
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "link_name") var linkName: String?,
    @ColumnInfo(name = "owner_id") var ownerId: String,
    @ColumnInfo(name = "track_count")var trackCount: Int
    )