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
import org.eclipse.ecsp.helper.response.CustomMessage
import org.eclipse.ecsp.vehicleservice.model.TerminateDeviceData
import org.eclipse.ecsp.vehicleservice.model.vehicleprofile.PostVehicleAttributeData
import org.eclipse.ecsp.vehicleservice.service.VehicleServiceInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *Vehicle Profile repository class is to perform all network calls related to vehicle profile activity
 */
class VehicleProfileRepository {
    private val vehicleServiceInterface: VehicleServiceInterface by lazy {
        VehicleServiceInterface.vehicleServiceInterface()
    }

    /**
     * Function is to update the vehicle profile data
     *
     * @param deviceId as [String]
     * @param postVehicleAttributeData [PostVehicleAttributeData]
     * @return [MutableLiveData] of [CustomMessage]
     */
    fun updateVehicleProfileData(
        deviceId: String,
        postVehicleAttributeData: PostVehicleAttributeData,
    ): MutableLiveData<CustomMessage<String>> {
        val data = MutableLiveData<CustomMessage<String>>()
        val exception =
            CoroutineExceptionHandler { _, exception ->
                Log.e("Device association list API: ", exception.cause.toString())
            }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            data.postValue(vehicleServiceInterface.updateVehicleProfile(deviceId, postVehicleAttributeData))
        }
        return data
    }

    /**
     * Function is to terminate the vehicle using SDK Api
     *
     * @param terminateDeviceData [TerminateDeviceData]
     * @return [MutableLiveData] of [CustomMessage]
     */
    fun terminateVehicle(terminateDeviceData: TerminateDeviceData): MutableLiveData<CustomMessage<String>>  {
        val data = MutableLiveData<CustomMessage<String>>()
        val exception =
            CoroutineExceptionHandler { _, exception ->
                Log.e("Device termination API: ", exception.cause.toString())
            }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            data.postValue(vehicleServiceInterface.terminateVehicle(terminateDeviceData))
        }
        return data
    }
}
