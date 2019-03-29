package com.chrisfry.linq.business.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User that owns track groups
 *
 * User HAS ZERO OR MORE TrackGroup
 */
@Entity(tableName = "users")
data class User (
    @PrimaryKey var id: String
)