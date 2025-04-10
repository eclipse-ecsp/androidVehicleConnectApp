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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Represents the View model factory contains all the ViewModels
 *
 * @property activity activity reference
 */
class AppViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            LoginActivityVM::class.java -> LoginActivityVM(activity = activity) as T
            DeviceAssociationVM::class.java -> DeviceAssociationVM(activity = activity) as T
            DashboardVM::class.java -> DashboardVM(activity = activity) as T
            VehicleProfileVM::class.java -> VehicleProfileVM(activity = activity) as T
            NotificationVM::class.java -> NotificationVM(activity = activity) as T
            RemoteOperationVM::class.java -> RemoteOperationVM(activity = activity) as T
            else -> throw Throwable("Unsupported view model")
        }
    }
}
