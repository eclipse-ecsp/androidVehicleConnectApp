package com.harman.vehicleconnects.models.viewmodels

import android.app.Activity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessaging
import com.harman.androidvehicleconnectsdk.notificationservice.model.ChannelData
import com.harman.androidvehicleconnectsdk.notificationservice.model.NotificationConfigData
import com.harman.androidvehicleconnectsdk.userservice.service.UserServiceInterface
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel
import com.harman.vehicleconnects.repository.DashboardRepository

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
class DashboardVM(activity: Activity) : AndroidViewModel(activity.application) {
    private var topBarTitle = MutableLiveData("")
    private var _associatedDeviceList = MutableLiveData<HashMap<String, VehicleProfileModel?>>()
    private val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository()
    }
    private val userServiceInterface: UserServiceInterface by lazy {
        UserServiceInterface.authService(activity)
    }
    private var isSignOutClicked = MutableLiveData(false)

    fun getTopBarTitle(): MutableLiveData<String> {
        return topBarTitle
    }

    fun setTopBarTitle(title: String) {
        topBarTitle.value = title
    }

    fun getAssociatedDeviceList(): LiveData<HashMap<String, VehicleProfileModel?>>{
        return _associatedDeviceList
    }

    fun fetchAssociateDeviceList() {
        _associatedDeviceList =  dashboardRepository.associateDeviceList()
    }

    fun signOutClick() {
        userServiceInterface.signOutWithAppAuth {
            isSignOutClicked.postValue(it.status.requestStatus)
        }
    }

    fun isSignOutClicked(): LiveData<Boolean> {
        return isSignOutClicked
    }

    fun subscribeNotificationConfig(
        userId: String,
        deviceData: HashMap<String, VehicleProfileModel?>
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                deviceData.keys.forEach { vehicleId ->
                    val channelData = ChannelData(arrayListOf(token), true)
                    val notificationConfigData =
                        NotificationConfigData(arrayListOf(channelData), true)
                    dashboardRepository.subscribeNotificationConfig(
                        userId,
                        vehicleId,
                        arrayListOf(notificationConfigData)
                    )
                }
            }
        }
    }
}