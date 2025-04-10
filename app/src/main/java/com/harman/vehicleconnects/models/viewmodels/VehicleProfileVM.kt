package com.harman.vehicleconnects.models.viewmodels

import android.app.Activity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.harman.vehicleconnects.repository.VehicleProfileRepository
import com.harman.androidvehicleconnectsdk.helper.response.CustomMessage
import com.harman.androidvehicleconnectsdk.vehicleservice.model.AssociatedDevice
import com.harman.androidvehicleconnectsdk.vehicleservice.model.TerminateDeviceData
import com.harman.androidvehicleconnectsdk.vehicleservice.model.vehicleprofile.PostVehicleAttributeData
import com.harman.androidvehicleconnectsdk.vehicleservice.model.vehicleprofile.VehicleAttributeDetail
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel

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
class VehicleProfileVM(activity: Activity) : AndroidViewModel(activity.application) {
    private var topBarTitle = MutableLiveData("")
    private var _onSaveBtnClick = MutableLiveData<PostVehicleAttributeData>()
    private var _isRemoveVehicleClicked = MutableLiveData(false)
    private val vehicleProfileRepository: VehicleProfileRepository by lazy {
        VehicleProfileRepository()
    }

    fun getTopBarTitle(): MutableLiveData<String> {
        return topBarTitle
    }

    fun setTopBarTitle(title: String) {
        topBarTitle.value = title
    }

    fun onSaveBtnClick(name: String, vehicleProfileModel: VehicleProfileModel) {
        val postVehicleAttributeData = PostVehicleAttributeData(
            VehicleAttributeDetail(
                vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.baseColor,
                vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.make,
                vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.model,
                vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.modelYear,
                vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.bodyType,
                name
            )
        )
        _onSaveBtnClick.postValue(
            postVehicleAttributeData
        )
    }

    fun isSaveBtnClicked(): LiveData<PostVehicleAttributeData> {
        return _onSaveBtnClick
    }

    fun updateVehicleProfileData(
        deviceId: String,
        postVehicleAttributeData: PostVehicleAttributeData
    ): MutableLiveData<CustomMessage<String>> {
        return vehicleProfileRepository.updateVehicleProfileData(deviceId, postVehicleAttributeData)
    }

    fun terminateVehicle(serialNumber: String, deviceId: String, imeiNumber: String): MutableLiveData<CustomMessage<String>>{
        return vehicleProfileRepository.terminateVehicle(TerminateDeviceData(serialNumber, deviceId, imeiNumber))
    }


    fun clickedOnRemoveVehicle(value: Boolean){
        _isRemoveVehicleClicked.postValue(value)
    }

    fun isRemoveVehicleClicked(): LiveData<Boolean>{
        return _isRemoveVehicleClicked
    }
}