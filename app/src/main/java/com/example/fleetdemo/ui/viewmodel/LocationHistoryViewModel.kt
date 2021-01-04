package com.example.fleetdemo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fleetdemo.R
import com.example.fleetdemo.model.RequestStatus
import com.example.fleetdemo.model.VehicleHistoryRequestParams
import com.example.fleetdemo.repository.DataRepository
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class LocationHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dataRepository = DataRepository.getInstance(application.applicationContext)
    private var calendar: Calendar = Calendar.getInstance()
    private val uiDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

    var objectId : Int = 0
    var plate: String = ""
    var vehicleHistoryStatus = dataRepository.vehicleHistoryStatus
    var vehicleHistory = dataRepository.vehicleHistory

    val day : Int
        get() = calendar.get(Calendar.DAY_OF_MONTH)
    val month : Int
        get() = calendar.get(Calendar.MONTH)
    val year : Int
        get() = calendar.get(Calendar.YEAR)

    val uiDate : LiveData<String>
        get() = mutableUiDate
    private var mutableUiDate : MutableLiveData<String> = MutableLiveData()

    val uiDistance : LiveData<String>
        get() = mutableUiDistance
    private var mutableUiDistance : MutableLiveData<String> = MutableLiveData("")

    // values exactly as received in OnDateSet()  (0-based month)
    fun setDate(year: Int, month: Int, dayOfMonth: Int){
        calendar.set(year, month, dayOfMonth)
        updateUiDate()
    }

    fun refreshVehicleHistory(){
        dataRepository.refreshVehicleHistory(getRequestParams())
    }

    private fun getRequestParams() : VehicleHistoryRequestParams {
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val beginDate = apiDateFormat.format(calendar.time)
        val nextDayCalendar = calendar.clone() as Calendar
        nextDayCalendar.add(Calendar.DAY_OF_MONTH, 1)
        val endDate = apiDateFormat.format(nextDayCalendar.time)
        return VehicleHistoryRequestParams(objectId, beginDate = beginDate, endDate = endDate)
    }

    fun refreshTripDistance(){
        val historyData = vehicleHistory.value ?: emptyList()
        val appContext = getApplication<Application>().applicationContext
        if (historyData.isEmpty()){
            val noDataText = appContext.getString(R.string.location_history_trip_distance_no_data)
            mutableUiDistance.postValue(noDataText)
        } else {
            val odometerDifference : Float = historyData.last().distance - historyData.first().distance
            mutableUiDistance.postValue(appContext.getString(
                R.string.location_history_trip_distance,
                odometerDifference))
        }
    }

    fun getHistoryCoordinates() : List<LatLng> {
        if (vehicleHistoryStatus.value?.status != RequestStatus.OK)
            return emptyList()
        val historyData = vehicleHistory.value ?: emptyList()
        val coordinates : ArrayList<LatLng> = ArrayList()
        historyData.forEach{
            coordinates.add(LatLng(it.latitude, it.longitude))
        }
        return coordinates
    }

    private fun updateUiDate(){
        mutableUiDate.postValue(uiDateFormat.format(calendar.time))
    }

    init {
        updateUiDate()
    }
}