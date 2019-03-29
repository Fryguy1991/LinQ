package com.chrisfry.linq.business.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.chrisfry.linq.business.database.entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id LIKE :userId")
    fun findUserById(userId: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun deleteUser(user: User)
}