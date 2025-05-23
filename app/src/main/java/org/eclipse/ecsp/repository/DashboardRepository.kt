package org.eclipse.ecsp.repository

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
import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.eclipse.ecsp.helper.AppConstants.DISASSOCIATED
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.eclipse.ecsp.helper.response.CustomMessage
import org.eclipse.ecsp.notificationservice.model.AlertAnalysisData
import org.eclipse.ecsp.notificationservice.model.NotificationConfigData
import org.eclipse.ecsp.notificationservice.service.NotificationServiceInterface
import org.eclipse.ecsp.roservice.model.RemoteOperationState
import org.eclipse.ecsp.roservice.model.RoEventHistoryResponse
import org.eclipse.ecsp.roservice.model.RoStatusResponse
import org.eclipse.ecsp.roservice.service.RoServiceInterface
import org.eclipse.ecsp.userservice.service.UserServiceInterface
import org.eclipse.ecsp.vehicleservice.model.AssociatedDevice
import org.eclipse.ecsp.vehicleservice.service.VehicleServiceInterface

/**
 * Dashboard Repository class is to perform all network calls come from Dashboard activity
 *
 */
class DashboardRepository {
    private val roServiceInterface: RoServiceInterface by lazy {
        RoServiceInterface.roServiceInterface()
    }
    private val vehicleServiceInterface: VehicleServiceInterface by lazy {
        VehicleServiceInterface.vehicleServiceInterface()
    }
    private val notificationServiceInterface: NotificationServiceInterface by lazy {
        NotificationServiceInterface.notificationServiceInterface()
    }

    /**
     * Function is used to check the Ro request status using SDK API
     *
     * @param userId as [String]
     * @param vehicleId as [String]
     * @param roRequestId Ro request id as [String]
     * @return [MutableLiveData] of [RoEventHistoryResponse]'s [CustomMessage]
     */
    fun checkRoRequestStatus(
        userId: String,
        vehicleId: String,
        roRequestId: String,
    ): MutableLiveData<CustomMessage<RoEventHistoryResponse>> {
        val data = MutableLiveData<CustomMessage<RoEventHistoryResponse>>()
        val exception =
            CoroutineExceptionHandler { _, exception ->
                Log.e("RO Request Status API failed: ", exception.cause.toString())
            }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            roServiceInterface.checkRemoteOperationRequestStatus(userId, vehicleId, roRequestId) {
                data.postValue(it)
            }
        }
        return data
    }

    /**
     * Function is to perform update RO status API call using SDK API functions
     *
     * @param userId as [String]
     * @param vehicleId as [String]
     * @param remoteOperationState [RemoteOperationState] value
     * @param percentage as [Long]
     * @param duration as [Long]
     * @return
     */
    fun updateRoState(
        userId: String,
        vehicleId: String,
        remoteOperationState: RemoteOperationState,
        percentage: Int? = null,
        duration: Int? = null,
    ): MutableLiveData<CustomMessage<RoStatusResponse>> {
        val data = MutableLiveData<CustomMessage<RoStatusResponse>>()
        val exception =
            CoroutineExceptionHandler { _, exception ->
                Log.e("RO State Update API failed: ", exception.cause.toString())
            }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            roServiceInterface.updateROStateRequest(
                userId,
                vehicleId,
                percentage,
                duration,
                remoteOperationState,
            ) {
                data.postValue(it)
            }
        }
        return data
    }

    /**
     * Function is to get the remote operation history data using SDK api
     *
     * @param userId as [String]
     * @param vehicleId as [String]
     * @return [MutableLiveData] of [RoEventHistoryResponse]'s [CustomMessage]
     */
    fun getRemoteOperationHistory(
        userId: String,
        vehicleId: String,
    ): MutableLiveData<CustomMessage<List<RoEventHistoryResponse>>> {
        val data = MutableLiveData<CustomMessage<List<RoEventHistoryResponse>>>()
        val exception =
            CoroutineExceptionHandler { _, exception ->
                Log.e("RO History Status API failed: ", exception.cause.toString())
            }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            roServiceInterface.getRemoteOperationHistory(userId, vehicleId) {
                data.postValue(it)
            }
        }
        return data
    }

    /**
     * Function is to get the alert history data using SDK api
     *
     * @param deviceId as [String]
     * @param alertTypes [String] [List] of Alert types
     * @param since device associated date as [Long]
     * @param till till date or required date till the data is required as [Long]
     * @param size Data size as [Integer]
     * @param page Record page number as [Integer]
     * @param readStatus notification read status as [Boolean]
     * @return [MutableLiveData] of [AlertAnalysisData]'s [CustomMessage]
     */
    fun getAlertHistory(
        deviceId: String,
        alertTypes: List<String>,
        since: Long,
        till: Long,
        size: Int?,
        page: Int?,
        readStatus: String?,
    ): MutableLiveData<CustomMessage<AlertAnalysisData>> {
        val data = MutableLiveData<CustomMessage<AlertAnalysisData>>()
        val exception =
            CoroutineExceptionHandler { _, exception ->
                Log.e("Alert History API failed: ", exception.cause.toString())
            }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            data.postValue(
                notificationServiceInterface.notificationAlertHistory(
                    deviceId,
                    alertTypes,
                    since,
                    till,
                    size,
                    page,
                    readStatus,
                )
            )
        }
        return data
    }

    /**
     * Functions is to get the associated device list using SDK Api
     *
     * @return [MutableLiveData] of [HashMap]
     */
    fun associateDeviceList(): MutableLiveData<Pair<Boolean, HashMap<String, VehicleProfileModel?>>> {
        val data = MutableLiveData<Pair<Boolean, HashMap<String, VehicleProfileModel?>>>()
        val exception =
            CoroutineExceptionHandler { _, exception ->
                Log.e(
                    "Device association list and vehicle profile API failed: ",
                    exception.cause.toString(),
                )
            }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            val list = HashMap<String, VehicleProfileModel?>()
            val deviceCall =
                async {
                    var deviceAssociationListData: List<AssociatedDevice>? =
                        null
                    vehicleServiceInterface.associatedDeviceList {
                        if (it.response != null) {
                            deviceAssociationListData =
                                it.response?.data?.filter { deviceList ->
                                    deviceList.mDeviceId != null && deviceList.mAssociationStatus != DISASSOCIATED
                                }
                        }
                    }
                    return@async deviceAssociationListData
                }

            val vehicleProfileDataCall: Deferred<Pair<Boolean, HashMap<String, VehicleProfileModel?>>> =
                async {
                    var success = false
                    deviceCall.await()?.forEach {
                        if (it.mDeviceId != null) {
                            vehicleServiceInterface.getVehicleProfile(it.mDeviceId!!) { vehicleProfileData ->
                                list[it.mDeviceId!!] =
                                    VehicleProfileModel(
                                        it,
                                        vehicleProfileData.response?.data?.get(0)
                                    )
                            }
                            success = true
                        }
                    }?.let {
                        success = false
                    }
                    return@async Pair(success, list)
                }

            data.postValue(vehicleProfileDataCall.await())
        }
        return data
    }

    /**
     * Function is to do notification configuration API
     *
     * @param userId user email id
     * @param vehicleId as [String]
     * @param notificationConfigDataList [ArrayList] of [NotificationConfigData]
     */
    fun subscribeNotificationConfig(
        userId: String,
        vehicleId: String,
        notificationConfigDataList: ArrayList<NotificationConfigData>,
    ) {
        val exception =
            CoroutineExceptionHandler { _, exception ->
                Log.e("Notification config data subscribe API failed: ", exception.cause.toString())
            }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            notificationServiceInterface.updateNotificationConfig(
                userId,
                vehicleId,
                null,
                notificationConfigDataList,
            ) {
                Log.d(
                    "NOTIFICATION_SUBSCRIPTION API",
                    "Notification subscription api success for $vehicleId"
                )
            }
        }
    }

    fun doSignOut(userServiceInterface: UserServiceInterface, response: (Boolean) -> Unit) {
        userServiceInterface.signOutWithAppAuth {
            response(it.status.requestStatus)
        }
    }

    suspend fun requestForChangePassword(userServiceInterface: UserServiceInterface): CustomMessage<Any> =
        userServiceInterface.changePasswordRequest()
}
