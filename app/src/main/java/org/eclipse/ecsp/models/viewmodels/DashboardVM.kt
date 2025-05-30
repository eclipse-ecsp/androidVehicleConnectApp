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
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import org.eclipse.ecsp.repository.DashboardRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.eclipse.ecsp.helper.response.CustomMessage
import org.eclipse.ecsp.notificationservice.model.ChannelData
import org.eclipse.ecsp.notificationservice.model.NotificationConfigData
import org.eclipse.ecsp.userservice.service.UserServiceInterface
import java.lang.ref.WeakReference

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
    private var isSignOutClicked = MutableLiveData(false)
    private var _isPasswordChangeTriggered = MutableLiveData(false)
    private var passwordChangeStatus = MutableLiveData<CustomMessage<Any>>()
    private var weakReference = WeakReference(activity)

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
    fun getAssociatedDeviceList(): LiveData<HashMap<String, VehicleProfileModel?>> {
        return _associatedDeviceList
    }

    /**
     * Represents to set the associated vehicle list to [_associatedDeviceList]
     * Function is calling both associated vehicle list and respective vehicles profile data API
     *
     */
    fun fetchAssociateDeviceList() {
        viewModelScope.launch {
            val deviceList = dashboardRepository.getAssociatedDeviceList()
            if (deviceList != null) {
                val vehicleProfileData = dashboardRepository.getVehicleProfileData(deviceList)
                if (vehicleProfileData.isNotEmpty())
                    _associatedDeviceList.postValue(vehicleProfileData)
                else
                    Toast.makeText(weakReference.get(), "Vehicle Profile API failed during operation ", Toast.LENGTH_SHORT)
                        .show()
            } else {
                Toast.makeText(weakReference.get(), "Device list API failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Represents to do SIGN_OUT
     *
     */
    fun signOutClick(userServiceInterface: UserServiceInterface) {
        dashboardRepository.doSignOut(userServiceInterface) {
            isSignOutClicked.postValue(it)
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

    fun isPasswordChangeTriggered(): LiveData<Boolean> = _isPasswordChangeTriggered

    fun setPasswordChangeTriggerValue(value: Boolean) {
        _isPasswordChangeTriggered.value = value
    }

    fun changePasswordApiCall(userServiceInterface: UserServiceInterface): LiveData<CustomMessage<Any>> {
        val exception =
            CoroutineExceptionHandler { _, exception ->
                Log.e("Password change request API failed: ", exception.cause.toString())
            }
        viewModelScope.launch(exception) {
            passwordChangeStatus.postValue(
                dashboardRepository.requestForChangePassword(
                    userServiceInterface
                )
            )
        }
        return passwordChangeStatus
    }
}
