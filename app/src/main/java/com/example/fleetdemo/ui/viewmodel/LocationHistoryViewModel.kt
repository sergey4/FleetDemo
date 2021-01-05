package com.example.fleetdemo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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
        set(value){
            if (value > 0){
                field = value
                refreshVehicleHistory()
            }
        }
    var plate: String = ""
    var vehicleHistoryStatus = dataRepository.vehicleHistoryStatus
    private var vehicleHistory = dataRepository.vehicleHistory

    val day : Int
        get() = calendar.get(Calendar.DAY_OF_MONTH)
    val month : Int
        get() = calendar.get(Calendar.MONTH)
    val year : Int
        get() = calendar.get(Calendar.YEAR)

    val dateText : String
        get() = uiDateFormat.format(calendar.time)

    val tripDistanceText : String
        get() = getTripDistance()

    // values exactly as received in OnDateSet()  (0-based month)
    fun setDate(year: Int, month: Int, dayOfMonth: Int){
        calendar.set(year, month, dayOfMonth)
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

    private fun getTripDistance() : String {
        val historyData = vehicleHistory.value ?: emptyList()
        val appContext = getApplication<Application>().applicationContext
        if (historyData.isEmpty() || (vehicleHistoryStatus.value?.status != RequestStatus.OK)){
            return appContext.getString(R.string.location_history_trip_distance_no_data)
        } else {
            val odometerDifference : Float = historyData.last().distance - historyData.first().distance
            val deltaDistanceSum : Float = historyData.fold(0.0f) { sum, element -> sum + element.deltaDistance }
            val resultDistance = if (odometerDifference > 0.0f) odometerDifference else deltaDistanceSum
            return appContext.getString(R.string.location_history_trip_distance, resultDistance)
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

}