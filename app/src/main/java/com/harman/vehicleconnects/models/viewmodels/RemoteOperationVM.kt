package com.harman.vehicleconnects.models.viewmodels

import android.app.Activity
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.harman.androidvehicleconnectsdk.helper.response.CustomMessage
import com.harman.androidvehicleconnectsdk.roservice.model.RemoteOperationState
import com.harman.androidvehicleconnectsdk.roservice.model.RoEventHistoryResponse
import com.harman.androidvehicleconnectsdk.roservice.model.RoStatusResponse
import com.harman.vehicleconnects.helper.AppConstants
import com.harman.vehicleconnects.helper.AppConstants.FORCED_FAILURE
import com.harman.vehicleconnects.helper.AppConstants.PENDING
import com.harman.vehicleconnects.helper.dataToJson
import com.harman.vehicleconnects.helper.fromJson
import com.harman.vehicleconnects.helper.isRequestPendingLong
import com.harman.vehicleconnects.helper.toastError
import com.harman.vehicleconnects.models.dataclass.ROErrorMessage
import com.harman.vehicleconnects.models.dataclass.RemoteOperationItem
import com.harman.vehicleconnects.repository.DashboardRepository
import com.harman.vehicleconnects.services.RemoteOperationService
import com.harman.vehicleconnects.services.RoRequestLoopService

/********************************************************************************
 * Copyright (c) 2023-24 Harman International
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
class RemoteOperationVM(activity: Activity) : AndroidViewModel(activity.application) {
    //    private var checkRoRequestTimer: Job? = null
    private var checkRoRequest = MutableLiveData<Triple<String, String, Boolean>>()
    private var roHistory = MutableLiveData<String>()
    private var _updateRoStatusData = MutableLiveData<Triple<RemoteOperationState, Int?, Int?>>()
    private val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository()
    }
    private val remoteOperationService: RemoteOperationService by lazy {
        RemoteOperationService()
    }

    /*private fun roRequestStatusCallTimer(
        vehicleId: String,
        roRequestId: String
    ): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            var count = 0
            while (isActive) {
                if (count <= 50) {
                    delay(10000)
                    count += 1
                    triggerRoRequestCheck(vehicleId, roRequestId, true)
                } else {
                    cancelJob()
                }
            }
        }
    }*/

    fun gridItemClick(
        eventTitleName: String, state: String,
        showBottomSheet: MutableState<Triple<Boolean, String, String>>?,
        openDialog: MutableState<Triple<Boolean, String, String>>?
    ) {
        when (eventTitleName) {
            AppConstants.WINDOWS, AppConstants.LIGHT -> {
                showBottomSheet?.value = Triple(true, eventTitleName, state)
            }

            else -> {
                openDialog?.value = Triple(true, state, eventTitleName)
            }
        }
    }

    private fun updateListOnRoHistory(
        activity: Activity,
        roEventHistoryResponse: CustomMessage<List<RoEventHistoryResponse>>?,
        lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?,
        notifyRoUpdate: MutableState<Int>
    ) {
        if (roEventHistoryResponse?.response != null) {
            val hashMap: HashMap<String, RoEventHistoryResponse> = HashMap()
            roEventHistoryResponse.response!!.forEach { data ->
                if (data.roStatus == PENDING && isRequestPendingLong(data.roEvents.timestamp!!)) {
                    data.roStatus = FORCED_FAILURE
                }
                if (hashMap.isNotEmpty()) {
                    if (hashMap.containsKey(data.roEvents.eventID)) {
                        val oldStatusDetail = hashMap[data.roEvents.eventID]
                        if (oldStatusDetail != null && oldStatusDetail.roEvents.timestamp!! < data.roEvents.timestamp!!) {
                            hashMap[data.roEvents.eventID!!] = data
                        }
                    } else {
                        hashMap[data.roEvents.eventID!!] = data
                    }
                } else {
                    hashMap[data.roEvents.eventID!!] = data
                }
            }

            hashMap.let {
                val list: List<RoEventHistoryResponse> =
                    ArrayList(it.values)
                if (lazyStaggeredGridList?.value != null) {
                    var tempList = lazyStaggeredGridList.value
                    list.forEach { events ->
                        tempList = remoteOperationService.updateGridItem(events, tempList, activity)
                            ?: arrayListOf()
                    }
                    lazyStaggeredGridList.value = (tempList)
                    notifyRoUpdate.value = notifyRoUpdate.value + 1
                }
            }
        } else {
            lazyStaggeredGridList?.value = (ArrayList(AppConstants.defaultRoValuesList))
            notifyRoUpdate.value = notifyRoUpdate.value + 1
            val errorMessage = roEventHistoryResponse?.error?.message.toString()
            toastError(activity, Gson().fromJson<List<ROErrorMessage>>(errorMessage)[0].message)
        }
    }

    /*private fun updateRemoteUI(
        activity: Activity,
        roEventHistoryResponse: CustomMessage<RoEventHistoryResponse>,
        vehicleId: String,
        roRequestId: String,
        lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?,
        notifyRoUpdate: MutableState<Int>
    ) {
        if (roEventHistoryResponse.status.requestStatus && roEventHistoryResponse.response != null) {
            val response = roEventHistoryResponse.response
            when (response?.roStatus) {
                AppConstants.PROCESSED_SUCCESS -> {
                    val finalList =
                        remoteOperationService.updateGridItem(response, lazyStaggeredGridList?.value, activity)
                    if (finalList != null) {
                        lazyStaggeredGridList?.value = finalList
                        notifyRoUpdate.value = notifyRoUpdate.value + 1
                    }
                    cancelJob()
                }

                PENDING -> {
                    if (isRequestPendingLong(response.roEvents.timestamp!!)) {
                        Log.d("PENDING_STATE", "3 min over")
                        response.roStatus = FORCED_FAILURE
                        val finalList =
                            remoteOperationService.updateGridItem(response, lazyStaggeredGridList?.value, activity)
                        if (finalList != null) {
                            lazyStaggeredGridList?.value = finalList
                            notifyRoUpdate.value = notifyRoUpdate.value + 1
                        }
                        cancelJob()
                        toastError(activity, "Remote operation failed")
                    } else {
                        Log.d("PENDING_STATE", "within 3 min")
                        var tempList = lazyStaggeredGridList?.value
                        tempList = remoteOperationService.updateGridItem(response, tempList, activity)
                        if (tempList != null) {
                            lazyStaggeredGridList?.value = tempList
                            notifyRoUpdate.value = notifyRoUpdate.value + 1
                        }
                        if (checkRoRequestTimer == null)
                            checkRoRequestTimer =
                                roRequestStatusCallTimer(vehicleId, roRequestId)
                    }
                }

                AppConstants.TTL_EXPIRED, AppConstants.PROCESSED_FAILED -> {
                    val finalList = remoteOperationService.updateGridItem(
                        response,
                        lazyStaggeredGridList?.value,
                        activity
                    )
                    if (finalList != null) {
                        lazyStaggeredGridList?.value = finalList
                        notifyRoUpdate.value = notifyRoUpdate.value + 1
                    }
                    cancelJob()
                    toastError(activity, "Remote operation failed")
                }
            }
        } else
            toastError(activity, roEventHistoryResponse.error?.message.toString())
    }*/


    fun getRemoteOperationHistory(
        lifecycleOwner: LifecycleOwner,
        activity: Activity,
        isProgressBarLoading: MutableState<Boolean>?,
        lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?,
        notifyRoUpdate: MutableState<Int>,
        userId: String,
        vehicleId: String
    ) {
        dashboardRepository.getRemoteOperationHistory(userId, vehicleId)
            .observe(lifecycleOwner) { roEventHistoryResponse ->
                isProgressBarLoading?.value = false
                updateListOnRoHistory(
                    activity, roEventHistoryResponse,
                    lazyStaggeredGridList, notifyRoUpdate
                )
            }
    }

    /*fun cancelJob() {
        if (checkRoRequestTimer != null && checkRoRequestTimer!!.isActive) {
            checkRoRequestTimer!!.cancel()
            checkRoRequestTimer = null
        }
    }*/

    fun checkRoRequestStatus(
        lifecycleOwner: LifecycleOwner,
        activity: Activity,
        userId: String,
        vehicleId: String,
        roRequestId: String,
        isProgressBarLoading: MutableState<Boolean>?,
        lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>,
        notifyRoUpdate: MutableState<Int>,
        isFromLoop: Boolean
    ) {
        isProgressBarLoading?.value = true
        RoRequestLoopService(this, remoteOperationService).roRequestStatusCallTimer(
            activity,
            userId,
            vehicleId,
            roRequestId,
            lifecycleOwner,
            dashboardRepository,
            lazyStaggeredGridList,
            notifyRoUpdate,
            isProgressBarLoading
        )
        /*dashboardRepository.checkRoRequestStatus(userId, vehicleId, roRequestId)
            .observe(lifecycleOwner) { roEventHistoryResponse ->
                isProgressBarLoading?.value = false
                updateRemoteUI(
                    activity, roEventHistoryResponse,
                    vehicleId, roRequestId,
                    lazyStaggeredGridList, notifyRoUpdate
                )
            }*/
    }

    fun updateRoState(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        userId: String,
        vehicleId: String,
        remoteOperationState: RemoteOperationState,
        isProgressBarLoading: MutableState<Boolean>?,
        percentage: Int? = null,
        duration: Int? = null,
        isFromClickAction: Boolean = false
    ) {
        dashboardRepository.updateRoState(
            userId,
            vehicleId,
            remoteOperationState,
            percentage,
            duration
        ).observe(lifecycleOwner) { roStatusResponse ->
            isProgressBarLoading?.value = false
            if (!isFromClickAction) {
                updateROStatus(
                    activity,
                    vehicleId, roStatusResponse
                )
            } else {
                if (roStatusResponse.status.requestStatus && roStatusResponse.response != null) {
                    val response = roStatusResponse.response as RoStatusResponse
                    if (response.requestId != null) {
                        triggerRoRequestCheck(vehicleId, response.requestId!!)
                    }
                } else {
                    toastError(activity, roStatusResponse.error?.message.toString())
                }
            }
        }
    }

    fun clickedOnCheckRoRequest(): LiveData<Triple<String, String, Boolean>> = checkRoRequest

    internal fun triggerRoRequestCheck(
        vehicleId: String,
        requestId: String,
        isFromLoop: Boolean = false
    ) = checkRoRequest.postValue(Triple(vehicleId, requestId, isFromLoop))

    fun clickOnRoHistory(deviceId: String) = roHistory.postValue(deviceId)

    fun getRoHistoryData(): LiveData<String> {
        return roHistory
    }

    fun clickOnUpdateRoStatus(
        remoteOperationState: RemoteOperationState,
        percentage: Int? = null,
        duration: Int? = null
    ) = _updateRoStatusData.postValue(Triple(remoteOperationState, percentage, duration))

    fun updateRoStatusData(): LiveData<Triple<RemoteOperationState, Int?, Int?>> =
        _updateRoStatusData

    private fun updateROStatus(
        activity: Activity,
        deviceId: String?,
        roStatusResponse: CustomMessage<RoStatusResponse>
    ) {
        if (roStatusResponse.status.requestStatus && roStatusResponse.response != null) {
            val response = roStatusResponse.response as RoStatusResponse
            if (response.requestId != null) {
                if (deviceId != null) {
                    triggerRoRequestCheck(
                        deviceId,
                        response.requestId!!
                    )
                }
            }
        } else {
            toastError(activity, roStatusResponse.error?.message.toString())
        }
    }
}