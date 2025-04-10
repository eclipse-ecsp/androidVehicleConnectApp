package com.harman.vehicleconnects.models.viewmodels

import android.app.Activity
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.harman.androidvehicleconnectsdk.notificationservice.model.AlertData
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel
import com.harman.vehicleconnects.models.routes.BottomNavItem
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
class NotificationVM(activity: Activity) : AndroidViewModel(activity.application) {
    private val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository()
    }
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
        readStatus: String? = null
    ) {
        dashboardRepository.getAlertHistory(
            deviceId,
            alertTypes,
            since,
            till,
            size,
            page,
            readStatus
        ).observe(lifecycleOwner){
            isProgressBarLoading?.value = false
            if (it.response != null && it.response!!.alertList != null) {
                alertList.value = ArrayList(it.response!!.alertList!!)
            }
        }
    }

    fun updatingVehicleDetails(
        remoteOperationVM: RemoteOperationVM,
        selectedVehicleIndex: MutableState<Int>?,
        selectedVehicleId: MutableState<Triple<String, String, String>>?,
        showVehicleList: MutableState<Pair<Boolean, ArrayList<VehicleProfileModel?>>>,
        navController: NavHostController,
        values: ArrayList<VehicleProfileModel?>,
        keys: ArrayList<String?>,
        vehicleData: HashMap<String, VehicleProfileModel?>
    ) {
        val index =   when{
            selectedVehicleIndex!!.value == -1 -> 0
            (keys.size - 1 ) >= selectedVehicleIndex.value -> selectedVehicleIndex.value
            else -> 0
        }

        val vehicleName = vehicleData[keys[index]]?.vehicleDetailData?.vehicleAttributes?.name
        val vehicleId = vehicleData[keys[index]]?.vehicleDetailData?.vehicleId
        selectedVehicleId?.value = Triple(keys[index].toString(), vehicleName ?: "No Name", vehicleId?:"")

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