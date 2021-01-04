package com.example.fleetdemo.model

import android.util.Log

class RequestInfo(val status : RequestStatus, val errorMessage : String = "") {
    private val logTag = RequestInfo::class.java.simpleName
    val isProgressBarVisible : Boolean
     get(){
         Log.d(logTag, "isProgressBarVisible(), status = $status")
         return (status == RequestStatus.LOADING)
     }
}