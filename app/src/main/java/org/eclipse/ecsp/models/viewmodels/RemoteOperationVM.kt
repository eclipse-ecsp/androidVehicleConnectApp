package org.eclipse.ecsp.models.viewmodels
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
import android.app.Activity
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.eclipse.ecsp.helper.response.CustomMessage
import org.eclipse.ecsp.roservice.model.RemoteOperationState
import org.eclipse.ecsp.roservice.model.RoEventHistoryResponse
import org.eclipse.ecsp.roservice.model.RoStatusResponse
import org.eclipse.ecsp.helper.AppConstants
import org.eclipse.ecsp.helper.AppConstants.FORCED_FAILURE
import org.eclipse.ecsp.helper.AppConstants.PENDING
import org.eclipse.ecsp.helper.fromJson
import org.eclipse.ecsp.helper.isRequestPendingLong
import org.eclipse.ecsp.helper.toastError
import org.eclipse.ecsp.models.dataclass.ROErrorMessage
import org.eclipse.ecsp.models.dataclass.RemoteOperationItem
import org.eclipse.ecsp.repository.DashboardRepository
import org.eclipse.ecsp.services.RemoteOperationService
import org.eclipse.ecsp.services.RoRequestLoopService

/**
 * Represents the View Model class used by Remote operation screen
 *
 * @constructor
 *
 * @param activity of Application lifecycle
 */
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

    /**
     * Function to trigger the grid view item click
     *
     * @param eventTitleName title name of the event
     * @param state state of the event
     * @param showBottomSheet [MutableState] of [Triple] <[Boolean], [eventTitleName], [state]>
     * @param openDialog
     */
    fun gridItemClick(
        eventTitleName: String,
        state: String,
        showBottomSheet: MutableState<Triple<Boolean, String, String>>?,
        openDialog: MutableState<Triple<Boolean, String, String>>?,
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
        notifyRoUpdate: MutableState<Int>,
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

    /**
     * Function is to get the remote operation history data using SDK API
     *
     * @param lifecycleOwner Application lifecycle owner object
     * @param activity application activity
     * @param isProgressBarLoading [MutableState] of [Boolean]
     * @param lazyStaggeredGridList [MutableState] of [RemoteOperationItem] 's [ArrayList]
     * @param notifyRoUpdate [MutableState] of [Integer] to notify internal logic
     * @param userId is [String] value
     * @param vehicleId is [String] value
     */
    fun getRemoteOperationHistory(
        lifecycleOwner: LifecycleOwner,
        activity: Activity,
        isProgressBarLoading: MutableState<Boolean>?,
        lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?,
        notifyRoUpdate: MutableState<Int>,
        userId: String,
        vehicleId: String,
    ) {
        viewModelScope.launch {
            val roEventHistoryResponse = dashboardRepository.getRemoteOperationHistory(userId, vehicleId)
            isProgressBarLoading?.value = false
            if(roEventHistoryResponse.status.requestStatus){
                updateListOnRoHistory(
                    activity,
                    roEventHistoryResponse,
                    lazyStaggeredGridList,
                    notifyRoUpdate,
                )
            } else {
                toastError(activity, "RO History: ${roEventHistoryResponse.error?.message ?: "Error occurred"}")
            }
        }
    }

    /*fun cancelJob() {
        if (checkRoRequestTimer != null && checkRoRequestTimer!!.isActive) {
            checkRoRequestTimer!!.cancel()
            checkRoRequestTimer = null
        }
    }*/

    /**
     * Function is to check Remote operation request status using SDK API
     *
     * @param lifecycleOwner Application lifecycle owner object
     * @param activity application activity
     * @param userId is [String] value
     * @param vehicleId is [String] value
     * @param roRequestId is RO request ID
     * @param isProgressBarLoading [MutableState] of [Boolean]
     * @param lazyStaggeredGridList [MutableState] of [RemoteOperationItem] 's [ArrayList]
     * @param notifyRoUpdate [MutableState] of [Integer] to notify internal logic
     * @param isFromLoop [Boolean] value for logic
     */
    fun checkRoRequestStatus(
        lifecycleOwner: LifecycleOwner,
        activity: Activity,
        userId: String,
        vehicleId: String,
        roRequestId: String,
        isProgressBarLoading: MutableState<Boolean>?,
        lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>,
        notifyRoUpdate: MutableState<Int>,
        isFromLoop: Boolean,
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
            isProgressBarLoading,
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

    /**
     * Function is to update the state of RO using SDK API
     *
     * @param activity application activity
     * @param lifecycleOwner Application lifecycle owner object
     * @param userId is [String] value
     * @param vehicleId is [String] value
     * @param remoteOperationState RO state [RemoteOperationState]
     * @param isProgressBarLoading [MutableState] of [Boolean]
     * @param percentage percentage of RO configuration event (comes in window)
     * @param duration duration of ro event to happen (comes in window)
     * @param isFromClickAction [Boolean] value for logic
     */
    fun updateRoState(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        userId: String,
        vehicleId: String,
        remoteOperationState: RemoteOperationState,
        isProgressBarLoading: MutableState<Boolean>?,
        percentage: Int? = null,
        duration: Int? = null,
        isFromClickAction: Boolean = false,
    ) {
        viewModelScope.launch {
            val roStatusResponse = dashboardRepository.updateRoState(
                userId,
                vehicleId,
                remoteOperationState,
                percentage,
                duration,
            )
            isProgressBarLoading?.value = false
            if (!isFromClickAction) {
                updateROStatus(
                    activity,
                    vehicleId,
                    roStatusResponse,
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

    /**
     * function is to trigger on click of check RO Request status
     *
     * @return [LiveData] of [Triple]
     */
    fun clickedOnCheckRoRequest(): LiveData<Triple<String, String, Boolean>> = checkRoRequest

    private fun triggerRoRequestCheck(
        vehicleId: String,
        requestId: String,
        isFromLoop: Boolean = false,
    ) = checkRoRequest.postValue(Triple(vehicleId, requestId, isFromLoop))

    /**
     * Function is to update the device id value
     *
     * @param deviceId device Id as [String]
     */
    fun clickOnRoHistory(deviceId: String) = roHistory.postValue(deviceId)

    /**
     * Function is to trigger the RO history using device id value
     *
     * @return [LiveData] of device id
     */
    fun getRoHistoryData(): LiveData<String> {
        return roHistory
    }

    /**
     * Function to set the update RO status
     *
     * @param remoteOperationState RO state
     * @param percentage as [Integer]
     * @param duration as [Integer]
     */
    fun clickOnUpdateRoStatus(
        remoteOperationState: RemoteOperationState,
        percentage: Int? = null,
        duration: Int? = null,
    ) = _updateRoStatusData.postValue(Triple(remoteOperationState, percentage, duration))

    /**
     * Function is to update the RO status
     *
     * @return [LiveData] of [Triple]<[RemoteOperationState, [Integer], [Integer]]>
     */
    fun updateRoStatusData(): LiveData<Triple<RemoteOperationState, Int?, Int?>> = _updateRoStatusData

    /**
     * Function is to update the RO Status
     *
     * @param activity application activity
     * @param deviceId device id as [String]
     * @param roStatusResponse [CustomMessage] of [RoStatusResponse]
     */
    private fun updateROStatus(
        activity: Activity,
        deviceId: String?,
        roStatusResponse: CustomMessage<RoStatusResponse>,
    ) {
        if (roStatusResponse.status.requestStatus && roStatusResponse.response != null) {
            val response = roStatusResponse.response as RoStatusResponse
            if (response.requestId != null) {
                if (deviceId != null) {
                    triggerRoRequestCheck(
                        deviceId,
                        response.requestId!!,
                    )
                }
            }
        } else {
            toastError(activity, roStatusResponse.error?.message.toString())
        }
    }
}
