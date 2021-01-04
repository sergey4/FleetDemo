package com.example.fleetdemo.ui

import android.view.View
import com.example.fleetdemo.model.RequestInfo
import com.google.android.material.snackbar.Snackbar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object UiUtils {
    // Sample timestamp: 2020-12-31 17:08:41+0200
    private const val dateFormat = "yyyy-MM-dd HH:mm:ssZ"

    private fun getTimeDifference(timestamp: String) : Long {
        var parsedDate: Date?
        try {
            parsedDate = SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(timestamp)
        } catch (e : ParseException) {
            return -1
        }
        val now = Date()
        return now.time - parsedDate.time
    }

    @JvmStatic
    fun getAgoTextForTimestamp(timestamp: String) : String {
        val difference = getTimeDifference(timestamp)
        if (difference < 0) return "-"
        val differenceInSeconds : Long = (difference / 1000)

        val days = differenceInSeconds / (60*60*24)
        var secondsRemaining = differenceInSeconds % (60*60*24)
        val hours = secondsRemaining / (60*60)
        secondsRemaining = secondsRemaining % (60*60)
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        return when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }

    fun showError(view : View?, requestInfo : RequestInfo){
        view?.let { Snackbar.make(it, requestInfo.errorMessage, Snackbar.LENGTH_SHORT).show() }
    }

}