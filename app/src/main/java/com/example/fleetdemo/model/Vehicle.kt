package com.example.fleetdemo.model

data class Vehicle(
    val objectId: Int,
    val timestamp: String,
    val speed: Int,
    val objectName: String,
    val driverName: String,
    val address: String,
    val plate: String
    )