package com.chrisfry.linq.business.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Reference object to go from track groups to tracks
 *
 * GroupToTrackLink HAS ONE TrackGroup
 * GroupToTrackLink HAS ONE Track
 */
@Entity(
    tableName = "group_to_track_links",
    foreignKeys = arrayOf(
        ForeignKey(entity = TrackGroup::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("link_id"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Track::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("track_id"))
    )
)
data class GroupToTrackLink(
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "link_id") var groupId: Int,
    @ColumnInfo(name = "track_id") var trackId: String,
    @ColumnInfo(name = "track_index") var trackIndex: Int
)