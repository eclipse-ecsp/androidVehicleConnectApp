package org.eclipse.ecsp.models.routes
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
import org.eclipse.ecsp.helper.AppConstants.VEHICLE_EDIT_COLOR
import org.eclipse.ecsp.helper.AppConstants.VEHICLE_EDIT_NAME
import org.eclipse.ecsp.helper.AppConstants.VEHICLE_PROFILE

/**
 * Sealed class used to handle the Vehicle profile screen
 *
 * @property route screen name
 */
sealed class VehicleProfileRoute(val route: String) {
    data object VehicleProfile : VehicleProfileRoute(VEHICLE_PROFILE)

    data object VehicleEditName : VehicleProfileRoute(VEHICLE_EDIT_NAME)

    data object VehicleEditColor : VehicleProfileRoute(VEHICLE_EDIT_COLOR)
}
