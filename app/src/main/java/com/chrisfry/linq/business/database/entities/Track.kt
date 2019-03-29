package com.chrisfry.linq.business.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Track model containing information needed for LinQ
 */
@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey var id: String
)