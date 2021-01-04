package com.example.fleetdemo.network.model

import com.example.fleetdemo.model.Vehicle

data class GetLastDataResponse(val status : Int, val response: List<Vehicle>)