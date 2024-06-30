package com.mesutkarahan.smarttravel.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mesutkarahan.smarttravel.model.TravelInfoEntity

@Dao
interface TravelInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(travelInfo: List<TravelInfoEntity>)

    @Query("SELECT * FROM travel_info")
    suspend fun getAllTravelInfo(): List<TravelInfoEntity>

    @Query("SELECT * FROM travel_info")
    fun getAllTravelsSync(): List<TravelInfoEntity>
}