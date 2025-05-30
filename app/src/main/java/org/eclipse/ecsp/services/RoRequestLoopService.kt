package org.eclipse.ecsp.services

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
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import org.eclipse.ecsp.helper.response.CustomMessage
import org.eclipse.ecsp.roservice.model.RoEventHistoryResponse
import org.eclipse.ecsp.helper.AppConstants
import org.eclipse.ecsp.helper.AppConstants.ALARM
import org.eclipse.ecsp.helper.AppConstants.DOOR
import org.eclipse.ecsp.helper.AppConstants.ENGINE
import org.eclipse.ecsp.helper.AppConstants.LIGHT
import org.eclipse.ecsp.helper.AppConstants.TRUNK
import org.eclipse.ecsp.helper.AppConstants.WINDOWS
import org.eclipse.ecsp.helper.isRequestPendingLong
import org.eclipse.ecsp.helper.toastError
import org.eclipse.ecsp.models.dataclass.RemoteOperationItem
import org.eclipse.ecsp.models.viewmodels.RemoteOperationVM
import org.eclipse.ecsp.repository.DashboardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * RoRequestLoopService class is to perform the RO related UI logic
 *
 * @property remoteOperationVM view model object of [RemoteOperationVM]
 * @property remoteOperationService object of [RemoteOperationService]
 */
class RoRequestLoopService(
    private val remoteOperationVM: RemoteOperationVM,
    private val remoteOperationService: RemoteOperationService,
) {
    private var checkRoRequestTimer: Job? = null

    fun roRequestStatusCallTimer(
        activity: Activity,
        userId: String,
        vehicleId: String,
        roRequestId: String,
        lifecycleOwner: LifecycleOwner,
        dashboardRepository: DashboardRepository,
        lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?,
        notifyRoUpdate: MutableState<Int>,
        isProgressBarLoading: MutableState<Boolean>?,
    ) {
        checkRoRequestTimer =
            CoroutineScope(Dispatchers.Main).launch {
                var count = 0
                while (isActive) {
                    if (count <= 50) {
                        count += 1
                        val roEventHistoryResponse = dashboardRepository.checkRoRequestStatus(
                            userId,
                            vehicleId,
                            roRequestId
                        )
                        updateRemoteUI(
                            activity, roEventHistoryResponse, isProgressBarLoading,
                            lazyStaggeredGridList, notifyRoUpdate,
                        )
                        delay(10000)
                    } else {
                        cancelJob()
                    }
                }
            }
    }

    private fun updateRemoteUI(
        activity: Activity,
        roEventHistoryResponse: CustomMessage<RoEventHistoryResponse>,
        isProgressBarLoading: MutableState<Boolean>?,
        lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?,
        notifyRoUpdate: MutableState<Int>,
    ) {
        isProgressBarLoading?.value = false
        if (roEventHistoryResponse.status.requestStatus && roEventHistoryResponse.response != null) {
            val response = roEventHistoryResponse.response
            when (response?.roStatus) {
                AppConstants.PROCESSED_SUCCESS -> {
                    val finalList =
                        remoteOperationService.updateGridItem(
                            response,
                            lazyStaggeredGridList?.value,
                            activity,
                        )
                    if (finalList != null) {
                        lazyStaggeredGridList?.value = finalList
                        notifyRoUpdate.value = notifyRoUpdate.value + 1
                    }
                    cancelJob()
                }

                AppConstants.PENDING -> {
                    if (isRequestPendingLong(response.roEvents.timestamp!!)) {
                        Log.d("PENDING_STATE", "3 min over")
                        response.roStatus = AppConstants.FORCED_FAILURE
                        val finalList =
                            remoteOperationService.updateGridItem(
                                response,
                                lazyStaggeredGridList?.value,
                                activity,
                            )
                        if (finalList != null) {
                            lazyStaggeredGridList?.value = finalList
                            notifyRoUpdate.value = notifyRoUpdate.value + 1
                        }
                        cancelJob()
                        toastError(
                            activity,
                            "${getEventType(response.roEvents.eventID)} Remote operation failed"
                        )
                    } else {
                        Log.d("PENDING_STATE", "within 3 min")
                        var tempList = lazyStaggeredGridList?.value
                        tempList =
                            remoteOperationService.updateGridItem(response, tempList, activity)
                        if (tempList != null) {
                            lazyStaggeredGridList?.value = tempList
                            notifyRoUpdate.value = notifyRoUpdate.value + 1
                        }
                    }
                }

                AppConstants.TTL_EXPIRED, AppConstants.PROCESSED_FAILED -> {
                    val finalList =
                        remoteOperationService.updateGridItem(
                            response,
                            lazyStaggeredGridList?.value,
                            activity,
                        )
                    if (finalList != null) {
                        lazyStaggeredGridList?.value = finalList
                        notifyRoUpdate.value = notifyRoUpdate.value + 1
                    }
                    cancelJob()
                    toastError(
                        activity,
                        "${getEventType(response.roEvents.eventID)} Remote operation failed"
                    )
                }
            }
        } else {
            toastError(activity, roEventHistoryResponse.error?.message.toString())
        }
    }

    private fun getEventType(eventId: String?): String {
        return when (eventId) {
            AppConstants.WINDOW_EVENT_ID -> WINDOWS
            AppConstants.LIGHTS_EVENT_ID -> LIGHT
            AppConstants.ALARM_EVENT_ID -> ALARM
            AppConstants.DOOR_EVENT_ID -> DOOR
            AppConstants.ENGINE_EVENT_ID -> ENGINE
            AppConstants.TRUNK_EVENT_ID -> TRUNK
            else -> ""
        }
    }

    private fun cancelJob() {
        if (checkRoRequestTimer != null && checkRoRequestTimer!!.isActive) {
            checkRoRequestTimer!!.cancel()
            checkRoRequestTimer = null
        }
    }
}
