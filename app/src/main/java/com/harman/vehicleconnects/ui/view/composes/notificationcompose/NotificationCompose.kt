package com.harman.vehicleconnects.ui.view.composes.notificationcompose

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.harman.vehicleconnects.R
import com.harman.vehicleconnects.helper.AppConstants.ALERT_ACCIDENT
import com.harman.vehicleconnects.helper.AppConstants.ALERT_AIRBAG_DEPLOY
import com.harman.vehicleconnects.helper.AppConstants.ALERT_BOUNDARY
import com.harman.vehicleconnects.helper.AppConstants.ALERT_BREAK_WARNING
import com.harman.vehicleconnects.helper.AppConstants.ALERT_CURFEW
import com.harman.vehicleconnects.helper.AppConstants.ALERT_DISTURBANCE
import com.harman.vehicleconnects.helper.AppConstants.ALERT_DONGLE_STATUS
import com.harman.vehicleconnects.helper.AppConstants.ALERT_FIRMWARE_DOWNLOADED
import com.harman.vehicleconnects.helper.AppConstants.ALERT_FIRMWARE_UPGRADE
import com.harman.vehicleconnects.helper.AppConstants.ALERT_IDLE
import com.harman.vehicleconnects.helper.AppConstants.ALERT_IMPACT_DETECTION
import com.harman.vehicleconnects.helper.AppConstants.ALERT_LOW_BATTERY
import com.harman.vehicleconnects.helper.AppConstants.ALERT_LOW_FUEL
import com.harman.vehicleconnects.helper.AppConstants.ALERT_OIL_PRESSURE_WARNING
import com.harman.vehicleconnects.helper.AppConstants.ALERT_SPEED
import com.harman.vehicleconnects.helper.AppConstants.ALERT_TIRE_PRESSURE_WARNING
import com.harman.vehicleconnects.helper.AppConstants.ALERT_TOW
import com.harman.vehicleconnects.helper.AppConstants.EPID_TPMS_ALERT
import com.harman.vehicleconnects.helper.AppConstants.GLOBAL_DOOR_LOCK
import com.harman.vehicleconnects.helper.AppConstants.SEATBELT_ALERT
import com.harman.vehicleconnects.ui.theme.Gray
import com.harman.vehicleconnects.ui.theme.LightGray
import com.harman.vehicleconnects.ui.theme.MildGray
import com.harman.vehicleconnects.ui.theme.MildWhite
import com.harman.androidvehicleconnectsdk.notificationservice.model.AlertData
import com.harman.vehicleconnects.helper.AppConstants
import com.harman.vehicleconnects.helper.convertISO8601TimeToMillis
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel
import com.harman.vehicleconnects.models.viewmodels.DashboardVM
import com.harman.vehicleconnects.models.viewmodels.NotificationVM
import com.harman.vehicleconnects.models.viewmodels.RemoteOperationVM
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

@Composable
fun Activity.NotificationMainCompose(
    dashboardVM: DashboardVM,
    notificationVM: NotificationVM,
    alertList: MutableState<ArrayList<AlertData>>,
    isProgressBarLoading: MutableState<Boolean>?,
    vehicleProfileDataList: MutableState<HashMap<String, VehicleProfileModel?>>?,
    selectedVehicleId: MutableState<Pair<String, String>>?,
    lifecycleOwner: LifecycleOwner
) {

    LaunchedEffect(Unit) {
        dashboardVM.setTopBarTitle(getString(R.string.notification_text))
    }
    LaunchedEffect(selectedVehicleId?.value?.first) {
        getAlertHistoryData(
            notificationVM,
            isProgressBarLoading,
            vehicleProfileDataList,
            selectedVehicleId,
            alertList,
            lifecycleOwner
        )
    }

    if (alertList.value.isNotEmpty()) {
        val list = alertList.value
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            itemsIndexed(list) { _, item ->
                Card(
                    modifier = Modifier
                        .padding(2.dp)
                        .background(MildWhite)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .background(LightGray)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .wrapContentWidth()
                                    .padding(20.dp)
                                    .background(Color.White),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(setItemIcon(item)),
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 15.dp, end = 20.dp).testTag("notification_item_icon_tag"),
                                    tint = Gray
                                )

                                Column(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .background(Color.White)
                                ) {

                                    Text(
                                        text = item.alertType ?: "No Alert Type Available",
                                        color = Color.Black,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(start = 10.dp, end = 10.dp).testTag("notification_type_text_tag")
                                    )

                                    Text(
                                        text = item.alertMessage ?: "",
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 10.dp, end = 10.dp).testTag("notification_message_text_tag")
                                    )

                                    Text(
                                        text = if (item.timestamp != null) convertToTime(item.timestamp!!) else "",
                                        color = MildGray,
                                        fontSize = 15.sp,
                                        modifier = Modifier.padding(start = 10.dp, end = 10.dp).testTag("notification_timestamp_text_tag")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun setItemIcon(alertData: AlertData): Int {
    return when (alertData.alertType) {
        ALERT_CURFEW -> R.drawable.icn_out_past_curfew
        ALERT_BOUNDARY -> R.drawable.icn_left_geofence_boundaries
        ALERT_IDLE -> R.drawable.ic_ideal
        ALERT_SPEED -> R.drawable.icn_speed_alert
        ALERT_DONGLE_STATUS -> R.drawable.icn_dongle_disconnected
        ALERT_TOW -> R.drawable.ic_icn_menu_vehicle_assistance2_pressed
        ALERT_ACCIDENT -> R.drawable.icn_accident_recorded
        ALERT_LOW_FUEL -> R.drawable.icn_dashboard_fuel
        ALERT_DISTURBANCE -> R.drawable.ic_icn_distubance_while_parked
        ALERT_LOW_BATTERY -> R.drawable.ic_battery_vehicle_health
        ALERT_FIRMWARE_UPGRADE -> R.drawable.icn_firmware_available
        ALERT_FIRMWARE_DOWNLOADED -> R.drawable.ic_firmware_downloaded
        ALERT_IMPACT_DETECTION -> R.drawable.ic_impact
        ALERT_AIRBAG_DEPLOY -> R.drawable.ic_airbag
        ALERT_OIL_PRESSURE_WARNING -> R.drawable.ic_engine_oil_vehicle_health
        ALERT_BREAK_WARNING -> R.drawable.ic_breaks
        ALERT_TIRE_PRESSURE_WARNING -> R.drawable.ic_tire_pressure
        GLOBAL_DOOR_LOCK -> R.drawable.ic_car_door_unlocked
        SEATBELT_ALERT -> R.drawable.ic_seat_belt
        EPID_TPMS_ALERT -> R.drawable.ic_tire_pressure
        else -> R.drawable.ic_vehicle_selected
    }
}

private fun convertToTime(timeStamp: Long): String {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return dateFormat.format(calendar.time)
}

private fun getAlertHistoryData(
    notificationVM: NotificationVM,
    isProgressBarLoading: MutableState<Boolean>?,
    vehicleProfileDataList: MutableState<HashMap<String, VehicleProfileModel?>>?,
    selectedVehicleId: MutableState<Pair<String, String>>?,
    alertList: MutableState<ArrayList<AlertData>>,
    lifecycleOwner: LifecycleOwner
) {
    isProgressBarLoading?.value = true
    val since =
        convertISO8601TimeToMillis(vehicleProfileDataList?.value?.get(selectedVehicleId?.value?.first)?.associatedDevice?.mAssociatedOn)
    val until = System.currentTimeMillis()
    selectedVehicleId?.value?.first?.let { deviceId ->
        notificationVM.getAlertHistory(lifecycleOwner, isProgressBarLoading,deviceId,
            AppConstants.ALERT_TYPES, alertList, since, until)
    }
}