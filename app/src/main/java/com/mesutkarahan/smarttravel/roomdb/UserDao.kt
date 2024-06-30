package com.mesutkarahan.smarttravel.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mesutkarahan.smarttravel.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?
}