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
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import org.eclipse.ecsp.models.routes.BottomNavItem
import org.eclipse.ecsp.repository.DashboardRepository
import org.eclipse.ecsp.notificationservice.model.AlertData

/**
 * Represents the View Model class used for Notification screen
 *
 * @constructor
 *
 * @param activity of Application lifecycle
 */
class NotificationVM(activity: Activity) : AndroidViewModel(activity.application) {
    private val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository()
    }

    /**
     * Function is get the Alert history data from SDK API
     *
     * @param lifecycleOwner application lifecycle owner object
     * @param isProgressBarLoading [MutableState] of [Boolean] value
     * @param deviceId device id as [String] value
     * @param alertTypes [String] [List] of Alert types
     * @param alertList [MutableState] of [AlertData] [ArrayList]
     * @param since device associated date as [Long]
     * @param till till date or required date till the data is required as [Long]
     * @param size Data size as [Integer]
     * @param page Record page number as [Integer]
     * @param readStatus notification read status as [Boolean]
     */
    fun getAlertHistory(
        lifecycleOwner: LifecycleOwner,
        isProgressBarLoading: MutableState<Boolean>?,
        deviceId: String,
        alertTypes: List<String>,
        alertList: MutableState<ArrayList<AlertData>>,
        since: Long,
        till: Long,
        size: Int? = null,
        page: Int? = null,
        readStatus: String? = null,
    ) {
        dashboardRepository.getAlertHistory(
            deviceId,
            alertTypes,
            since,
            till,
            size,
            page,
            readStatus,
        ).observe(lifecycleOwner) {
            isProgressBarLoading?.value = false
            if (it.response != null && it.response!!.alertList != null) {
                alertList.value = ArrayList(it.response!!.alertList!!)
            }
        }
    }

    /**
     * Function is to update the vehicle details using SDK API
     *
     * @param remoteOperationVM [RemoteOperationVM] object
     * @param selectedVehicleIndex [MutableState] of [Integer]
     * @param selectedVehicleId [MutableState] of [Triple] <Device Id, Vehicle Name, Vehicle Profile vehicle ID>
     * @param showVehicleList [MutableState] of [Pair] <[Boolean], [VehicleProfileModel]>
     * @param navController [NavHostController] object
     * @param values [ArrayList] of [VehicleProfileModel]
     * @param keys [ArrayList] of [String]
     * @param vehicleData [HashMap] of [String] and [VehicleProfileModel]
     */
    fun updatingVehicleDetails(
        remoteOperationVM: RemoteOperationVM,
        selectedVehicleIndex: MutableState<Int>?,
        selectedVehicleId: MutableState<Triple<String, String, String>>?,
        showVehicleList: MutableState<Pair<Boolean, ArrayList<VehicleProfileModel?>>>,
        navController: NavHostController,
        values: ArrayList<VehicleProfileModel?>,
        keys: ArrayList<String?>,
        vehicleData: HashMap<String, VehicleProfileModel?>,
    ) {
        val index =
            when {
                selectedVehicleIndex!!.value == -1 -> 0
                (keys.size - 1) >= selectedVehicleIndex.value -> selectedVehicleIndex.value
                else -> 0
            }

        val vehicleName = vehicleData[keys[index]]?.vehicleDetailData?.vehicleAttributes?.name
        val vehicleId = vehicleData[keys[index]]?.vehicleDetailData?.vehicleId
        selectedVehicleId?.value = Triple(keys[index].toString(), vehicleName ?: "No Name", vehicleId ?: "")

        showVehicleList.value = Pair(true, values)
        if (selectedVehicleId!!.value.first.isNotEmpty()) {
            when (navController.currentBackStackEntry?.destination?.route) {
                BottomNavItem.RemoteOperation.route -> {
                    remoteOperationVM.clickOnRoHistory(selectedVehicleId.value.first)
                }

//                BottomNavItem.Notification.route -> {}
            }
        }
    }
}
