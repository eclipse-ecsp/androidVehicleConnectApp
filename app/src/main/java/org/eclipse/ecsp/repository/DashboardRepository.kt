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
import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.eclipse.ecsp.helper.AppConstants.DISASSOCIATED
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.supervisorScope
import org.eclipse.ecsp.helper.response.CustomMessage
import org.eclipse.ecsp.helper.response.error.CustomError
import org.eclipse.ecsp.helper.response.error.Status
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
     * @return [CustomMessage]
     */
    suspend fun checkRoRequestStatus(
        userId: String,
        vehicleId: String,
        roRequestId: String,
    ): CustomMessage<RoEventHistoryResponse> {
        var resp = CustomMessage<RoEventHistoryResponse>(Status.Failure)
        try {
            resp = roServiceInterface.checkRemoteOperationRequestStatus(userId, vehicleId, roRequestId)
        } catch (exception: Exception) {
            Log.e("RO Request Status API failed: ", exception.printStackTrace().toString())
            CustomMessage<RoEventHistoryResponse>(
                Status.Failure,
                CustomError.Generic(exception.printStackTrace().toString())
            )
        }
        return resp
    }

    /**
     * Function is to perform update RO status API call using SDK API functions
     *
     * @param userId as [String]
     * @param vehicleId as [String]
     * @param remoteOperationState [RemoteOperationState] value
     * @param percentage as [Long]
     * @param duration as [Long]
     * @return [CustomMessage]
     */
    suspend fun updateRoState(
        userId: String,
        vehicleId: String,
        remoteOperationState: RemoteOperationState,
        percentage: Int? = null,
        duration: Int? = null,
    ): CustomMessage<RoStatusResponse> {
        var data: CustomMessage<RoStatusResponse>
        try {
            data = roServiceInterface.updateROStateRequest(
                userId,
                vehicleId,
                percentage,
                duration,
                remoteOperationState,
            )
        } catch (exception: Exception) {
            Log.e("RO State Update API failed: ", exception.printStackTrace().toString())
            data = CustomMessage(
                Status.Failure,
                CustomError.Generic(exception.printStackTrace().toString())
            )
        }
        return data
    }

    /**
     * Function is to get the remote operation history data using SDK api
     *
     * @param userId as [String]
     * @param vehicleId as [String]
     * @return [CustomMessage]
     */
    suspend fun getRemoteOperationHistory(
        userId: String,
        vehicleId: String,
    ): CustomMessage<List<RoEventHistoryResponse>> {
        var resp: CustomMessage<List<RoEventHistoryResponse>>
        try {
            resp =  roServiceInterface.getRemoteOperationHistory(userId, vehicleId)
        } catch (exception: Exception) {
            Log.e("RO History Status API failed: ", exception.printStackTrace().toString())
            resp = CustomMessage(
                Status.Failure,
                CustomError.Generic(exception.printStackTrace().toString())
            )
        }
        return resp
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
     * Function is to fetch the associated device list
     *
     * @return [List] of [AssociatedDevice]
     */
    suspend fun getAssociatedDeviceList(): List<AssociatedDevice>? {
        var data: List<AssociatedDevice>? = null
        try {
            val result = vehicleServiceInterface.associatedDeviceList()
            if (result.status.requestStatus && result.response != null) {
                data =
                    result.response?.data?.filter { deviceList ->
                        deviceList.mDeviceId != null && deviceList.mAssociationStatus != DISASSOCIATED
                    }
            }

        } catch (exception: Exception) {
            Log.e(
                "Device association list API failed: ", exception.cause.toString(),
            )
        }
        return data
    }

    /**
     * Function is to fetch the vehicle profile data of each associated device.
     *
     * @param deviceList contains all the associated device which are not DISASSOCIATED
     * @return [HashMap] of [VehicleProfileModel] which contains only the device which having the device id
     */
    suspend fun getVehicleProfileData(deviceList: List<AssociatedDevice>): HashMap<String, VehicleProfileModel?> {
        val list = HashMap<String, VehicleProfileModel?>()
        try {
            supervisorScope {
                deviceList.forEach {
                    launch {
                        try {
                            if (it.mDeviceId != null) {
                                list[it.mDeviceId!!] = VehicleProfileModel(it)
                                val vehicleProfileData = vehicleServiceInterface.getVehicleProfile(it.mDeviceId!!)
                                if (vehicleProfileData.response != null
                                    && vehicleProfileData.response?.data != null
                                    && vehicleProfileData.response!!.data!!.isNotEmpty()
                                ) {
                                    list[it.mDeviceId!!] =
                                        VehicleProfileModel(
                                            it,
                                            vehicleProfileData.response?.data?.get(0)
                                        )
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(
                                "Vehicle Profile API failed for ${it.mDeviceId}: ",
                                e.cause.toString(),
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(
                "Vehicle Profile API failed by coroutine cancellation: ",
                e.cause.toString(),
            )
        }
        return list
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
                Log.e(
                    "NOTIFICATION_SUBSCRIPTION API",
                    "Notification config data subscribe API failed: ${exception.cause.toString()}"
                )
            }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            val result = notificationServiceInterface.updateNotificationConfig(
                userId,
                vehicleId,
                null,
                notificationConfigDataList,
            )
            if (result.status.requestStatus) {
                Log.d(
                    "NOTIFICATION_SUBSCRIPTION API",
                    "Notification subscription api response for $vehicleId -> ${result.response}"
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
