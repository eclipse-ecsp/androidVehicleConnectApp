package com.harman.vehicleconnects.models.viewmodels
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessaging
import com.harman.androidvehicleconnectsdk.notificationservice.model.ChannelData
import com.harman.androidvehicleconnectsdk.notificationservice.model.NotificationConfigData
import com.harman.androidvehicleconnectsdk.userservice.service.UserServiceInterface
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel
import com.harman.vehicleconnects.repository.DashboardRepository

/**
 * Represents the Dashboard ViewModel
 *
 * @constructor
 *
 * @param activity is used to initialize the object inside the view model
 */
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

    /**
     * Represents to get the title value of the screens
     *
     * @return [MutableLiveData] of title value
     */
    fun getTopBarTitle(): MutableLiveData<String> {
        return topBarTitle
    }

    /**
     * Represents to set the title value of the screens
     *
     * @param title value as string
     */
    fun setTopBarTitle(title: String) {
        topBarTitle.value = title
    }

    /**
     * Represents to get the associated vehicle list
     *
     * @return [HashMap] of [VehicleProfileModel] LiveData
     */
    fun getAssociatedDeviceList(): LiveData<HashMap<String, VehicleProfileModel?>>  {
        return _associatedDeviceList
    }

    /**
     * Represents to set the associated vehicle list to [_associatedDeviceList]
     *
     */
    fun fetchAssociateDeviceList() {
        _associatedDeviceList = dashboardRepository.associateDeviceList()
    }

    /**
     * Represents to do SIGN_OUT
     *
     */
    fun signOutClick() {
        userServiceInterface.signOutWithAppAuth {
            isSignOutClicked.postValue(it.status.requestStatus)
        }
    }

    /**
     * Represents to check if sign out is clicked or not
     *
     * @return [LiveData] of [Boolean] value
     */
    fun isSignOutClicked(): LiveData<Boolean> {
        return isSignOutClicked
    }

    /**
     * Represents to configure the notification details using [FirebaseMessaging] service
     *
     * @param userId user unique id
     * @param deviceData [HashMap] of [VehicleProfileModel]
     */
    fun subscribeNotificationConfig(
        userId: String,
        deviceData: HashMap<String, VehicleProfileModel?>,
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
                        arrayListOf(notificationConfigData),
                    )
                }
            }
        }
    }
}
