package com.example.fleetdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.fleetdemo.repository.DataRepository
import com.example.fleetdemo.model.Vehicle
import com.example.fleetdemo.ui.fragment.LocationHistoryFragment
import com.example.fleetdemo.ui.viewmodel.LocationHistoryViewModel
import com.example.fleetdemo.ui.fragment.ApiKeyFragment
import com.example.fleetdemo.ui.fragment.VehicleListFragment

class MainActivity : AppCompatActivity(),
        ApiKeyFragment.ApiKeyDialogListener,
        VehicleListFragment.VehicleClickListener, VehicleListFragment.VehicleMenuItemListener {

    private val logTag = MainActivity::class.java.simpleName
    private lateinit var repository : DataRepository
    private val locationHistoryViewModel : LocationHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate() called with: savedInstanceState = $savedInstanceState")
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        repository = DataRepository.getInstance(applicationContext)
        repository.loadPreferences(this)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, VehicleListFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(logTag, "onOptionsItemSelected() called with: item = $item")
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // update toolbar title and button visibility according to current fragment.
    // probably isn't the best way to do it, but...
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        val showHomeButton = !((currentFragment == null) || (currentFragment is VehicleListFragment))
        supportActionBar?.setDisplayHomeAsUpEnabled(showHomeButton)
        when (currentFragment) {
            is VehicleListFragment -> { supportActionBar?.setTitle(R.string.title_vehicles) }
            is LocationHistoryFragment -> supportActionBar?.setTitle(
                getString(R.string.title_location_history, locationHistoryViewModel.plate))
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun showApiKeyDialog(){
        val dialog = ApiKeyFragment(repository.apiKey)
        dialog.show(supportFragmentManager, "API_KEY_DIALOG")
    }

    override fun onItemApiKeyClick() {
        showApiKeyDialog()
    }

    override fun onItemRefreshClick() {
        repository.refreshVehicleList()
    }

    override fun onApiKeyDialogNegativeClick() {
        Log.d(logTag, "onApiKeyDialogNegativeClick()")
    }

    override fun onApiKeyDialogPositiveClick(apiKey : String) {
        Log.d(logTag, "onApiKeyDialogPositiveClick(), key = $apiKey")
        repository.apiKey = apiKey
        repository.savePreferences(this)
        repository.refreshVehicleList()
        invalidateOptionsMenu()
    }

    override fun onVehicleClick(vehicle: Vehicle) {
        Log.d(logTag, "onVehicleClick() called with: vehicle = $vehicle")
        locationHistoryViewModel.objectId = vehicle.objectId
        locationHistoryViewModel.plate = vehicle.plate

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LocationHistoryFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

}