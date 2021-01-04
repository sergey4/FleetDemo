package com.example.fleetdemo.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleetdemo.R
import com.example.fleetdemo.model.RequestInfo
import com.example.fleetdemo.model.RequestStatus
import com.example.fleetdemo.model.Vehicle
import com.example.fleetdemo.databinding.FragmentVehicleListBinding
import com.example.fleetdemo.ui.UiUtils
import com.example.fleetdemo.ui.viewmodel.VehicleListViewModel
import java.lang.ClassCastException

class VehicleListFragment : Fragment() {

    interface VehicleClickListener {
        fun onVehicleClick(vehicle : Vehicle)
    }
    interface VehicleMenuItemListener {
        fun onItemApiKeyClick()
        fun onItemRefreshClick()
    }

    private lateinit var listener : VehicleClickListener
    private lateinit var menuListener : VehicleMenuItemListener

    private val logTag = VehicleListFragment::class.java.simpleName
    private lateinit var adapter: VehicleListAdapter
    companion object {
        fun newInstance() = VehicleListFragment()
    }

    private val viewModel: VehicleListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentVehicleListBinding.inflate(inflater)
        binding.model = viewModel
        binding.lifecycleOwner = this
        initRecyclerViewAndAdapter(binding.vehicleRecyclerview)
        return binding.root
    }

    private fun initRecyclerViewAndAdapter(recyclerView: RecyclerView){
        adapter = VehicleListAdapter(listener)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.vehicleListStatus.observe(viewLifecycleOwner,
            { status -> updateUiForStatus(status) }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_api_key -> {
                menuListener.onItemApiKeyClick()
                true
            }
            R.id.action_refresh -> {
                menuListener.onItemRefreshClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem?.setEnabled(viewModel.isRefreshButtonEnabled)
        super.onPrepareOptionsMenu(menu)
    }

    private fun updateUiForStatus(requestInfo : RequestInfo) {
        Log.d(logTag, "updateUiForStatus, status = ${requestInfo.status}")
        when(requestInfo.status) {
            RequestStatus.FAILED -> UiUtils.showError(view, requestInfo)
            RequestStatus.OK -> adapter.data = viewModel.vehicleList.value ?: emptyList()
            else -> return
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as VehicleClickListener
            menuListener = context as VehicleMenuItemListener
        } catch (e: ClassCastException){
            throw ClassCastException("$context must implement VehicleClickListener and VehicleMenuItemListener")
        }
    }


}