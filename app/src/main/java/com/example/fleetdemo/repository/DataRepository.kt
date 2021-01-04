package com.example.fleetdemo.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fleetdemo.model.*
import com.example.fleetdemo.network.VolleyWebClient

class DataRepository private constructor (applicationContext : Context) {

    private val baseApiUrl = "https://app.ecofleet.com/seeme/Api/Vehicles"
    private val webClient = VolleyWebClient(applicationContext, baseApiUrl)

    var apiKey : String = ""
    val vehicleListStatus : LiveData<RequestInfo>
        get() = mutableVehicleListStatus
    private var mutableVehicleListStatus : MutableLiveData<RequestInfo> = MutableLiveData()

    val vehicleList : LiveData<List<Vehicle>>
        get() = mutableVehicleList
    private var mutableVehicleList : MutableLiveData<List<Vehicle>> = MutableLiveData()

    val vehicleHistoryStatus : LiveData<RequestInfo>
        get() = mutableVehicleHistoryStatus
    private var mutableVehicleHistoryStatus : MutableLiveData<RequestInfo> = MutableLiveData()

    val vehicleHistory : LiveData<List<VehicleHistoryItem>>
        get() = mutableVehicleHistory
    private var mutableVehicleHistory : MutableLiveData<List<VehicleHistoryItem>> = MutableLiveData()

    fun isApiKeySet() : Boolean {
        return apiKey.isNotBlank()
    }

    fun refreshVehicleList(){
        if (isApiKeySet())
            webClient.requestVehicleList(apiKey, mutableVehicleListStatus, mutableVehicleList)
    }

    fun refreshVehicleHistory(requestParams: VehicleHistoryRequestParams){
        if (isApiKeySet())
            webClient.requestVehicleHistory(apiKey, requestParams,
                mutableVehicleHistoryStatus,
                mutableVehicleHistory
            )
    }

    fun loadPreferences(context: Context){
        val sharedPreferences = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE)
        apiKey = sharedPreferences.getString(PREF_API_KEY, "") ?: ""
    }

    fun savePreferences(context: Context){
        val sharedPreferences = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE)
        sharedPreferences.edit().putString(PREF_API_KEY, apiKey).apply()
    }

    // ideas from here -
    // https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
    companion object {
        private const val PREF_FILE = "defaultPreferences"
        private const val PREF_API_KEY = "api_key"

        @Volatile
        private var instance: DataRepository? = null

        @Synchronized
        fun getInstance(applicationContext: Context): DataRepository = instance ?:
            DataRepository(applicationContext).also { instance = it }
    }

    init {
        mutableVehicleListStatus.value = RequestInfo(status = RequestStatus.NONE)
    }
}