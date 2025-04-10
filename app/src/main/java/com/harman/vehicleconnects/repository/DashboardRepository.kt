package com.harman.vehicleconnects.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harman.vehicleconnects.helper.AppConstants.DISASSOCIATED
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel
import com.harman.androidvehicleconnectsdk.helper.response.CustomMessage
import com.harman.androidvehicleconnectsdk.notificationservice.model.AlertAnalysisData
import com.harman.androidvehicleconnectsdk.notificationservice.model.NotificationConfigData
import com.harman.androidvehicleconnectsdk.notificationservice.service.NotificationServiceInterface
import com.harman.androidvehicleconnectsdk.roservice.model.RemoteOperationState
import com.harman.androidvehicleconnectsdk.roservice.model.RoEventHistoryResponse
import com.harman.androidvehicleconnectsdk.roservice.model.RoStatusResponse
import com.harman.androidvehicleconnectsdk.roservice.service.RoServiceInterface
import com.harman.androidvehicleconnectsdk.vehicleservice.model.AssociatedDevice
import com.harman.androidvehicleconnectsdk.vehicleservice.service.VehicleServiceInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

    fun checkRoRequestStatus(
        userId: String,
        vehicleId: String,
        roRequestId: String
    ): MutableLiveData<CustomMessage<RoEventHistoryResponse>> {
        val data = MutableLiveData<CustomMessage<RoEventHistoryResponse>>()
        val exception = CoroutineExceptionHandler { _, exception ->
            Log.e("RO Request Status API failed: ", exception.cause.toString())
        }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            roServiceInterface.checkRemoteOperationRequestStatus(userId, vehicleId, roRequestId) {
                data.postValue(it)
            }
        }
        return data
    }

    fun updateRoState(
        userId: String,
        vehicleId: String,
        remoteOperationState: RemoteOperationState,
        percentage: Int? = null,
        duration: Int? = null
    ): MutableLiveData<CustomMessage<RoStatusResponse>> {
        val data = MutableLiveData<CustomMessage<RoStatusResponse>>()
        val exception = CoroutineExceptionHandler { _, exception ->
            Log.e("RO State Update API failed: ", exception.cause.toString())
        }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            roServiceInterface.updateROStateRequest(
                userId,
                vehicleId,
                percentage,
                duration,
                remoteOperationState
            ) {
                data.postValue(it)
            }
        }
        return data
    }

    fun getRemoteOperationHistory(
        userId: String,
        vehicleId: String
    ): MutableLiveData<CustomMessage<List<RoEventHistoryResponse>>> {
        val data = MutableLiveData<CustomMessage<List<RoEventHistoryResponse>>>()
        val exception = CoroutineExceptionHandler { _, exception ->
            Log.e("RO History Status API failed: ", exception.cause.toString())
        }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            roServiceInterface.getRemoteOperationHistory(userId, vehicleId) {
                data.postValue(it)
            }
        }
        return data
    }

    fun getAlertHistory(
        deviceId: String,
        alertTypes: List<String>,
        since: Long,
        till: Long,
        size: Int?,
        page: Int?,
        readStatus: String?
    ): MutableLiveData<CustomMessage<AlertAnalysisData>> {
        val data = MutableLiveData<CustomMessage<AlertAnalysisData>>()
        val exception = CoroutineExceptionHandler { _, exception ->
            Log.e("Alert History API failed: ", exception.cause.toString())
        }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            notificationServiceInterface.notificationAlertHistory(
                deviceId,
                alertTypes,
                since,
                till,
                size,
                page,
                readStatus
            ) {
                data.postValue(it)
            }
        }
        return data
    }

    fun associateDeviceList(): MutableLiveData<HashMap<String, VehicleProfileModel?>> {
        val data = MutableLiveData<HashMap<String, VehicleProfileModel?>>()
        val exception = CoroutineExceptionHandler { _, exception ->
            Log.e(
                "Device association list and vehicle profile API failed: ",
                exception.cause.toString()
            )
        }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            val list = HashMap<String, VehicleProfileModel?>()
            val deviceCall = async {
                var deviceAssociationListData: List<AssociatedDevice>? =
                    null
                vehicleServiceInterface.associatedDeviceList {
                    deviceAssociationListData = it.response?.data?.filter { deviceList ->
                        deviceList.mDeviceId != null && deviceList.mAssociationStatus != DISASSOCIATED
                    }
                }
                return@async deviceAssociationListData
            }

            val vehicleProfileDataCall : Deferred<HashMap<String, VehicleProfileModel?>> = async {
                deviceCall.await()?.forEach {
                    if (it.mDeviceId != null)
                        vehicleServiceInterface.getVehicleProfile(it.mDeviceId!!) { vehicleProfileData ->
                            list[it.mDeviceId!!] =
                                VehicleProfileModel(it, vehicleProfileData.response?.data?.get(0))
                        }
                }
                return@async list
            }

            data.postValue(vehicleProfileDataCall?.await())
        }
        return data
    }

    fun subscribeNotificationConfig(
        userId: String,
        vehicleId: String,
        notificationConfigDataList: ArrayList<NotificationConfigData>
    ) {
        val exception = CoroutineExceptionHandler { _, exception ->
            Log.e("Notification config data subscribe API failed: ", exception.cause.toString())
        }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            notificationServiceInterface.updateNotificationConfig(
                userId,
                vehicleId,
                null,
                notificationConfigDataList
            ) {
                Log.d("NOTIFICATION_SUBSCRIPTION API","Notification subscription api success for $vehicleId")
            }
        }
    }
}