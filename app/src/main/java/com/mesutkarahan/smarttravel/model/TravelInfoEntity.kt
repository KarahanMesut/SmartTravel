package com.mesutkarahan.smarttravel.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.firestore.IgnoreExtraProperties
import com.mesutkarahan.smarttravel.roomdb.Converters
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity(tableName = "travel_info")
@IgnoreExtraProperties
@Parcelize
@TypeConverters(Converters::class)
data class TravelInfoEntity(
    @PrimaryKey
    val id: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val date: Date = Date(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val travelDate: Long = 0L
) : Parcelable {
    constructor() : this("", "", "", "", Date(), 0.0, 0.0)
}