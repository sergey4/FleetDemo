package com.example.fleetdemo.model

data class VehicleHistoryRequestParams(
    val objectId: Int,
    val beginDate: String,
    val endDate: String
    )