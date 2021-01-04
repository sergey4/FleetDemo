package com.example.fleetdemo.ui.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DateSelectorFragment(private val year : Int,
                           private val month : Int,
                           private val day : Int,
                           private val listener : DatePickerDialog.OnDateSetListener) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            DatePickerDialog(it, listener, year, month, day)
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}