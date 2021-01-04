package com.example.fleetdemo.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fleetdemo.model.Vehicle
import com.example.fleetdemo.databinding.ListItemVehicleBinding

class VehicleListAdapter(private val listener : VehicleListFragment.VehicleClickListener) :
            RecyclerView.Adapter<VehicleListAdapter.ViewHolder>() {

    private val logTag = VehicleListAdapter::class.java.simpleName
    var data = listOf<Vehicle>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        Log.d(logTag, "onBindViewHolder() called with: holder = $holder, position = $position, item = $item")
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            ListItemVehicleBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, listener)
    }

    class ViewHolder(private val binding: ListItemVehicleBinding,
                     private val listener: VehicleListFragment.VehicleClickListener) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item : Vehicle){
            binding.vehicle = item
            binding.clickListener = View.OnClickListener { listener.onVehicleClick(item) }
            binding.executePendingBindings()
        }

    }
}