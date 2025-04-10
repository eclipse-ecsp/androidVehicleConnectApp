package com.harman.vehicleconnects.models.viewmodels

import android.app.Activity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.harman.androidvehicleconnectsdk.helper.response.CustomMessage
import com.harman.androidvehicleconnectsdk.vehicleservice.model.DeviceVerificationData
import com.harman.androidvehicleconnectsdk.vehicleservice.model.deviceassociation.AssociatedDeviceInfo
import com.harman.androidvehicleconnectsdk.vehicleservice.service.VehicleServiceInterface
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
class DeviceAssociationVM(activity: Activity) : AndroidViewModel(activity.application) {
    private var topBarTitle = MutableLiveData("")
    private var isLoading = MutableLiveData(false)
    private val _verifyDeviceIMEI = MutableLiveData<CustomMessage<DeviceVerificationData>>()
    private val _associateDevice = MutableLiveData<CustomMessage<AssociatedDeviceInfo>>()
    private var verifyImei = MutableLiveData<String>()
    fun getTopBarTitle(): MutableLiveData<String> {
        return topBarTitle
    }

    fun setTopBarTitle(title: String) {
        topBarTitle.value = title
    }

    fun getLoadingStatus(): MutableLiveData<Boolean> {
        return isLoading
    }

    fun setLoadingStatus(status: Boolean) {
        isLoading.value = status
    }

    fun clickedOnVerifyImei(): LiveData<String> = verifyImei

    fun triggerImeiVerification(imeiString: String) = verifyImei.postValue(imeiString)

    fun verifyDeviceIMEI(
        vehicleServiceInterface: VehicleServiceInterface,
        imeiString: String
    ): MutableLiveData<CustomMessage<DeviceVerificationData>> {
        viewModelScope.launch {
            vehicleServiceInterface.verifyDeviceImei(imeiString) {
                _verifyDeviceIMEI.value = it
            }
        }
        return _verifyDeviceIMEI
    }
    fun associateDevice(
        vehicleServiceInterface: VehicleServiceInterface,
        imeiString: String
    ): MutableLiveData<CustomMessage<AssociatedDeviceInfo>> {
        viewModelScope.launch {
            vehicleServiceInterface.associateDevice(imeiString) {
                _associateDevice.value = it
            }
        }
        return _associateDevice
    }
}