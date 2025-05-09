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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.eclipse.ecsp.helper.response.CustomMessage
import org.eclipse.ecsp.vehicleservice.model.DeviceVerificationData
import org.eclipse.ecsp.vehicleservice.model.deviceassociation.AssociatedDeviceInfo
import org.eclipse.ecsp.vehicleservice.service.VehicleServiceInterface

/**
 * View Model class to handle the Device association screen
 *
 * @constructor
 *
 * @param activity
 */
class DeviceAssociationVM(activity: Activity) : AndroidViewModel(activity.application) {
    private var topBarTitle = MutableLiveData("")
    private var isLoading = MutableLiveData(false)
    private val _verifyDeviceIMEI = MutableLiveData<CustomMessage<DeviceVerificationData>>()
    private val _associateDevice = MutableLiveData<CustomMessage<AssociatedDeviceInfo>>()
    private var serialNumberValue = MutableLiveData<String>()

    /**
     * Represents to get the [MutableLiveData] of title
     *
     * @return [MutableLiveData] of String value
     */
    fun getTopBarTitle(): MutableLiveData<String> {
        return topBarTitle
    }

    /**
     * Represents to set the title value
     *
     * @param title as String
     */
    fun setTopBarTitle(title: String) {
        topBarTitle.value = title
    }

    /**
     * Function is to get the loading status
     *
     * @return [MutableLiveData] of status
     */
    fun getLoadingStatus(): MutableLiveData<Boolean> {
        return isLoading
    }

    /**
     * Function is to set the loading status
     *
     * @param status as [Boolean]
     */
    fun setLoadingStatus(status: Boolean) {
        isLoading.value = status
    }

    /**
     * Function will invoke once user clicked on IMEI verification
     *
     * @return IMEI string value as [LiveData]
     */
    fun clickedOnSerialNumberSubmission(): LiveData<String> = serialNumberValue

    /**
     * Function to set the IMEI value
     *
     * @param serialNumString as [String]
     */
    fun triggerSerialNumberVerification(serialNumString: String) = serialNumberValue.postValue(serialNumString)

    /**
     * Function is to call IMEI Verification API
     *
     * @param vehicleServiceInterface SDK interface
     * @param imeiString IMEI value
     * @return [MutableLiveData] of SDK [CustomMessage] with [DeviceVerificationData]
     */
    fun verifyDeviceIMEI(
        vehicleServiceInterface: VehicleServiceInterface,
        imeiString: String,
    ): MutableLiveData<CustomMessage<DeviceVerificationData>> {
        viewModelScope.launch {
            vehicleServiceInterface.verifyDeviceImei(imeiString) {
                _verifyDeviceIMEI.value = it
            }
        }
        return _verifyDeviceIMEI
    }

    /**
     * Function is to call association API to associate a device using IMEI
     *
     * @param vehicleServiceInterface SDK interface used to trigger the API call
     * @param serialString IMEI value in [String] format
     * @return [MutableLiveData] of SDK [CustomMessage] with [AssociatedDeviceInfo]
     */
    fun associateDevice(
        vehicleServiceInterface: VehicleServiceInterface,
        serialString: String,
    ): MutableLiveData<CustomMessage<AssociatedDeviceInfo>> {
        viewModelScope.launch {
            vehicleServiceInterface.associateDevice(serialString) {
                _associateDevice.value = it
            }
        }
        return _associateDevice
    }
}
