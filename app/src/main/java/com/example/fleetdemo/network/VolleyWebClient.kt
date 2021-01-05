package com.example.fleetdemo.network

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.example.fleetdemo.model.*
import com.example.fleetdemo.network.model.GetLastDataResponse
import com.example.fleetdemo.network.model.GetRawDataResponse

class VolleyWebClient(applicationContext : Context, private val baseApiUrl : String) {
    private val queue = Volley.newRequestQueue(applicationContext)
    private val apiPathVehicleList = "getLastData"
    private val apiPathVehicleHistory = "getRawData"
    private val logTag = VolleyWebClient::class.java.simpleName
    private val vehicleHistoryTag = "location"
    private val vehicleListTag = "vehicle_list"

    private fun getVehicleListUrl(apiKey: String) : String {
        val uri = Uri.parse(baseApiUrl).buildUpon()
            .appendPath(apiPathVehicleList)
            .appendQueryParameter("json", "true")
            .appendQueryParameter("key", apiKey)
            .build()
        return uri.toString()
    }

    private fun getVehicleHistoryUrl(apiKey: String, requestParams: VehicleHistoryRequestParams) : String {
        val uri = Uri.parse(baseApiUrl).buildUpon()
            .appendPath(apiPathVehicleHistory)
            .appendQueryParameter("json", "true")
            .appendQueryParameter("key", apiKey)
            .appendQueryParameter("begTimestamp", requestParams.beginDate)
            .appendQueryParameter("endTimestamp", requestParams.endDate)
            .appendQueryParameter("objectId", requestParams.objectId.toString())
            .build()
        return uri.toString()

    }

    fun requestVehicleList(apiKey : String, requestInfo : MutableLiveData<RequestInfo>,
            requestData : MutableLiveData<List<Vehicle>>) {
        if (requestInfo.value?.status == RequestStatus.LOADING) return
        requestInfo.postValue(RequestInfo(RequestStatus.LOADING))
        sendRequest(getVehicleListUrl(apiKey),
            Response.Listener<GetLastDataResponse> {
                Log.d(logTag, "requestVehicleList(), request ok, item count = ${it.response.size}")
                requestData.postValue(it.response)
                requestInfo.postValue(RequestInfo(RequestStatus.OK))
            },
            Response.ErrorListener {
                Log.d(logTag, "requestVehicleList(), request failed, ${it.message}")
                requestData.postValue(emptyList())
                requestInfo.postValue(getErrorInfo(it))
            },
            vehicleListTag
        )
    }

    fun requestVehicleHistory(apiKey : String,
                              requestParams: VehicleHistoryRequestParams,
                              requestInfo : MutableLiveData<RequestInfo>,
                              requestData : MutableLiveData<List<VehicleHistoryItem>>) {
        if (requestInfo.value?.status == RequestStatus.LOADING) {
            cancelRequest(vehicleHistoryTag)
        }
        requestInfo.postValue(RequestInfo(RequestStatus.LOADING))
        sendRequest(getVehicleHistoryUrl(apiKey, requestParams),
            Response.Listener<GetRawDataResponse> {
                Log.d(logTag, "requestVehicleHistory(), request ok, item count = ${it.response.size}")
                requestData.postValue(it.response)
                requestInfo.postValue(RequestInfo(RequestStatus.OK))
            },
            Response.ErrorListener {
                Log.d(logTag, "requestVehicleHistory(), request failed, ${it.message}")
                requestData.postValue(emptyList())
                requestInfo.postValue(getErrorInfo(it))
            },
            vehicleHistoryTag
        )
    }

    private fun getErrorInfo(volleyError: VolleyError) : RequestInfo {
        val errorMessage = when(volleyError){
            is AuthFailureError -> "permission denied"
            is TimeoutError -> "request timeout"
            else -> {
                if (volleyError.networkResponse != null) {
                    "http code ${volleyError.networkResponse.statusCode}"
                } else {
                    "unknown"
                }
            }
        }
        return RequestInfo(status = RequestStatus.FAILED, errorMessage = "Network error: $errorMessage")
    }

    private fun cancelRequest(tag: String){
        queue.cancelAll(tag)
    }

    private inline fun <reified T> sendRequest(url : String,
                            responseListener : Response.Listener<T>,
                            errorListener : Response.ErrorListener,
                            requestTag : String) {

        Log.d(logTag, "sendRequest(), url = $url")

        val jsonRequest =
            GsonRequest(
                url,
                T::class.java,
                null,
                responseListener,
                errorListener
            )

        jsonRequest.tag = requestTag
        queue.add(jsonRequest)
    }

}