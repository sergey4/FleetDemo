package com.example.fleetdemo.model

import com.google.gson.annotations.SerializedName

data class VehicleHistoryItem(
    val timestamp: String,
    @SerializedName("Latitude")
    val latitude: Double,
    @SerializedName("Longitude")
    val longitude: Double,
    @SerializedName("GPSState")
    val gpsState: String,
    @SerializedName("Distance")
    val deltaDistance: Float,
    @SerializedName("DeltaDistance")
    val distance: Float,
    @SerializedName("Speed")
    val speed: Int
    )