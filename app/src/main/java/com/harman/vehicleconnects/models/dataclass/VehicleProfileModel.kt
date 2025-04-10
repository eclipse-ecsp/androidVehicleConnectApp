package com.harman.vehicleconnects.models.dataclass

import android.os.Parcelable
import com.harman.androidvehicleconnectsdk.vehicleservice.model.AssociatedDevice
import com.harman.androidvehicleconnectsdk.vehicleservice.model.vehicleprofile.VehicleDetailData
import kotlinx.parcelize.Parcelize

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
@Parcelize
data class VehicleProfileModel(
    var associatedDevice: AssociatedDevice,
    var vehicleDetailData: VehicleDetailData?
): Parcelable
