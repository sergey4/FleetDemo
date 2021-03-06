package com.example.fleetdemo.ui.fragment

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.activityViewModels
import com.example.fleetdemo.R
import com.example.fleetdemo.databinding.FragmentLocationHistoryBinding
import com.example.fleetdemo.model.RequestInfo
import com.example.fleetdemo.model.RequestStatus
import com.example.fleetdemo.ui.UiUtils
import com.example.fleetdemo.ui.viewmodel.LocationHistoryViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class LocationHistoryFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val logTag = LocationHistoryFragment::class.java.simpleName
    companion object {
        fun newInstance() = LocationHistoryFragment()
    }

    private lateinit var binding : FragmentLocationHistoryBinding
    private val viewModel: LocationHistoryViewModel by activityViewModels()
    private var mapFragment: SupportMapFragment? = null
    private val callback = OnMapReadyCallback { googleMap ->
        val coordinates = viewModel.getHistoryCoordinates()
        googleMap.clear()
        if (coordinates.isNotEmpty()){
            addRoutePolyline(googleMap, coordinates)
            addStartEndMarkers(googleMap, coordinates)
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(8.0f))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates.last()))
        }
        googleMap.setMinZoomPreference(5.0f)
    }

    private fun addRoutePolyline(googleMap: GoogleMap, coordinates: List<LatLng>){
        googleMap.addPolyline(PolylineOptions()
            .clickable(false)
            .addAll(coordinates)
            .color(Color.GREEN)
        )
    }

    private fun addStartEndMarkers(googleMap: GoogleMap, coordinates: List<LatLng>){
        val startMarker = googleMap.addMarker(MarkerOptions()
            .title("Start")
            .position(coordinates.first())
        )
        startMarker.showInfoWindow()
        val endMarker = googleMap.addMarker(MarkerOptions()
            .title("End")
            .position(coordinates.last())
        )
        endMarker.showInfoWindow()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationHistoryBinding.inflate(inflater)
        binding.model = viewModel
        binding.clickListener = View.OnClickListener { showDateSelectionDialog() }
        binding.lifecycleOwner = this
        updateTextBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(logTag, "selected objectId = ${viewModel.objectId}, plate = ${viewModel.plate}")
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        viewModel.vehicleHistoryStatus.observe(viewLifecycleOwner,
            { status -> updateUiForStatus(status) }
        )
        mapFragment?.getMapAsync(callback)
    }

    private fun showDateSelectionDialog(){
        Log.d(logTag, "showDateSelectionDialog() called")
        val dialog = DateSelectorFragment(viewModel.year, viewModel.month, viewModel.day, this)
        dialog.show(parentFragmentManager, "DATE_SELECTOR_DIALOG")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.d(logTag, "onDateSet() called with: year = $year, month = $month, dayOfMonth = $dayOfMonth")
        viewModel.setDate(year, month, dayOfMonth)
        viewModel.refreshVehicleHistory()
    }

    private fun updateUiForStatus(requestInfo : RequestInfo) {
        Log.d(logTag, "updateUiForStatus, status = ${requestInfo.status}")
        updateTextBindings()
        if (requestInfo.status == RequestStatus.FAILED) {
            UiUtils.showError(view, requestInfo)
        } else if (requestInfo.status == RequestStatus.OK) {
            updateMap()
        }
    }

    private fun updateTextBindings(){
        binding.date = viewModel.dateText
        binding.tripDistance = viewModel.tripDistanceText
    }

    private fun updateMap(){
        mapFragment?.getMapAsync(callback)
    }

}