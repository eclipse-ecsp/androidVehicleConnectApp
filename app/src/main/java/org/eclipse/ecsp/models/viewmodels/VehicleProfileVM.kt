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
import org.eclipse.ecsp.helper.response.CustomMessage
import org.eclipse.ecsp.vehicleservice.model.TerminateDeviceData
import org.eclipse.ecsp.vehicleservice.model.vehicleprofile.PostVehicleAttributeData
import org.eclipse.ecsp.vehicleservice.model.vehicleprofile.VehicleAttributeDetail
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import org.eclipse.ecsp.repository.VehicleProfileRepository

/**
 * ViewModel class for Vehicle profile screen
 *
 * @constructor
 *
 * @param activity of Application lifecycle
 */
class VehicleProfileVM(activity: Activity) : AndroidViewModel(activity.application) {
    private var topBarTitle = MutableLiveData("")
    private var _onSaveBtnClick = MutableLiveData<PostVehicleAttributeData>()
    private var _isRemoveVehicleClicked = MutableLiveData(false)
    private val vehicleProfileRepository: VehicleProfileRepository by lazy {
        VehicleProfileRepository()
    }

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
     * Function is to trigger on click of SAVE button
     *
     * @param name vehicle name
     * @param vehicleProfileModel vehicle profile model data
     */
    fun onSaveBtnClick(
        name: String,
        vehicleProfileModel: VehicleProfileModel,
    ) {
        val postVehicleAttributeData =
            PostVehicleAttributeData(
                VehicleAttributeDetail(
                    vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.baseColor,
                    vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.make,
                    vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.model,
                    vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.modelYear,
                    vehicleProfileModel.vehicleDetailData?.vehicleAttributes?.bodyType,
                    name,
                ),
            )
        _onSaveBtnClick.postValue(
            postVehicleAttributeData,
        )
    }

    /**
     * Function is to invoke on click of Save button
     *
     * @return [LiveData] of [PostVehicleAttributeData]
     */
    fun isSaveBtnClicked(): LiveData<PostVehicleAttributeData> {
        return _onSaveBtnClick
    }

    /**
     * Function is to update the vehicle profile data
     *
     * @param deviceId device id as [String]
     * @param postVehicleAttributeData [PostVehicleAttributeData]
     * @return [MutableLiveData] of [CustomMessage]
     */
    fun updateVehicleProfileData(
        deviceId: String,
        postVehicleAttributeData: PostVehicleAttributeData,
    ): MutableLiveData<CustomMessage<String>> {
        return vehicleProfileRepository.updateVehicleProfileData(deviceId, postVehicleAttributeData)
    }

    /**
     * Function is to terminate vehicle using vehicle details
     *
     * @param serialNumber vehicle [serialNumber]
     * @param deviceId device id as [String]
     * @param imeiNumber IMEI number of vehicle
     * @return [MutableLiveData] of [CustomMessage]
     */
    fun terminateVehicle(
        serialNumber: String,
        deviceId: String,
        imeiNumber: String,
    ): MutableLiveData<CustomMessage<String>>  {
        return vehicleProfileRepository.terminateVehicle(TerminateDeviceData(serialNumber, deviceId, imeiNumber))
    }

    /**
     * Function is to set the [Boolean] value if remove vehicle is clicked
     *
     * @param value [Boolean]
     */
    fun clickedOnRemoveVehicle(value: Boolean)  {
        _isRemoveVehicleClicked.postValue(value)
    }

    /**
     * Function is to notify UI regarding the remove vehicle button click
     *
     * @return [LiveData] of [Boolean]
     */
    fun isRemoveVehicleClicked(): LiveData<Boolean>  {
        return _isRemoveVehicleClicked
    }
}
