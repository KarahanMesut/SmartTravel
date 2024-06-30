package com.mesutkarahan.smarttravel.model

import java.util.Date
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Plan(
    val id: String,
    val location: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val date: Date,
    val travelDate: Long = 0L
): Parcelable