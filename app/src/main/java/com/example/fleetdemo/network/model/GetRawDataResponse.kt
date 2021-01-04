package com.example.fleetdemo.network.model

import com.example.fleetdemo.model.VehicleHistoryItem

data class GetRawDataResponse(val status : Int, val response: List<VehicleHistoryItem>)