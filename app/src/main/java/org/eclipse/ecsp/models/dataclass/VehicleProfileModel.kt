package org.eclipse.ecsp.models.dataclass
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
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.eclipse.ecsp.vehicleservice.model.AssociatedDevice
import org.eclipse.ecsp.vehicleservice.model.vehicleprofile.VehicleDetailData

/**
 * Parcelize Data class used to hold the Vehicle profile details
 *
 * @property associatedDevice holds [AssociatedDevice] data
 * @property vehicleDetailData holds [VehicleDetailData] data
 */
@Parcelize
data class VehicleProfileModel(
    var associatedDevice: AssociatedDevice,
    var vehicleDetailData: VehicleDetailData?=null,
) : Parcelable
