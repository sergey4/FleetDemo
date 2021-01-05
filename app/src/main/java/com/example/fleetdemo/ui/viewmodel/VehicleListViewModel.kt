package com.example.fleetdemo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.fleetdemo.repository.DataRepository

class VehicleListViewModel(application: Application) : AndroidViewModel(application){
    private val dataRepository = DataRepository.getInstance(application.applicationContext)
    var vehicleListStatus = dataRepository.vehicleListStatus
    var vehicleList = dataRepository.vehicleList
    val isRefreshButtonEnabled : Boolean
        get() = dataRepository.isApiKeySet()

    init {
        dataRepository.refreshVehicleList()
    }
}