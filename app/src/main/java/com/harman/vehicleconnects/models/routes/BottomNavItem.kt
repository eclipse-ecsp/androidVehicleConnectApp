package com.harman.vehicleconnects.models.routes
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
import com.harman.vehicleconnects.R

/**
 * Sealed class used to handle the Bottom navigation item
 *
 * @property route route value as string
 * @property iconId drawable id
 * @property label name of the item
 */
sealed class BottomNavItem(val route: String, val iconId: Int, val label: String) {
    data object RemoteOperation : BottomNavItem("RemoteOperation", R.drawable.ic_vehicle_selected, "Remote Operation")

    data object Settings : BottomNavItem("settings", R.drawable.ic_settings_selected, "Settings")
//    data object Notification : BottomNavItem("notification", R.drawable.ic_notification, "Notifications")
}
