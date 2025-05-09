package org.eclipse.ecsp.ui.view.composes.vehicleprofilecompose
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.eclipse.ecsp.R
import org.eclipse.ecsp.helper.AppConstants.ASSOCIATED
import org.eclipse.ecsp.helper.AppConstants.ASSOCIATION_INITIATED
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import org.eclipse.ecsp.models.viewmodels.VehicleProfileVM
import org.eclipse.ecsp.ui.theme.Black
import org.eclipse.ecsp.ui.theme.DarkGray
import org.eclipse.ecsp.ui.theme.LightGray
import org.eclipse.ecsp.ui.theme.MildWhite
import org.eclipse.ecsp.ui.theme.White
import org.eclipse.ecsp.ui.view.composes.remoteoperationcompose.ShowAlertDialog

/**
 * VehicleProfileCompose contains vehicle profile screen related compose functions
 *
 */
@Composable
fun Activity.VehicleProfileMainCompose(
    vehicleProfileModel: VehicleProfileModel?,
    vehicleProfileVM: VehicleProfileVM?,
    navController: NavHostController,
    openDialog: MutableState<Boolean>?,
) {
    LaunchedEffect(Unit) {
        vehicleProfileVM?.setTopBarTitle(getString(R.string.vehicle_profile_text))
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            modifier =
                Modifier
                    .background(LightGray)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(30.dp).testTag("vehicle_profile_name_text_tag"),
            text =
                vehicleProfileModel?.vehicleDetailData?.vehicleAttributes?.name
                    ?: "",
            color = Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            modifier =
                Modifier
                    .background(MildWhite)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(40.dp),
            text = "Vehicle",
            color = Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
        )

        HorizontalDivider(
            color = LightGray,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(1.dp),
            thickness = 1.dp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp),
            text = "VIN",
            color = Black,
            fontSize = 17.sp,
        )
        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp).testTag("vin_text_tag"),
            text = vehicleProfileModel?.vehicleDetailData?.vin ?: "NA",
            color = DarkGray,
            fontSize = 16.sp,
        )

        HorizontalDivider(
            color = LightGray,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(1.dp),
            thickness = 1.dp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp),
            text = "Make/Model/Year",
            color = Black,
            fontSize = 17.sp,
        )
        val makeModelYear =
            "${vehicleProfileModel?.vehicleDetailData?.vehicleAttributes?.make} " +
                "${vehicleProfileModel?.vehicleDetailData?.vehicleAttributes?.model} " +
                "${vehicleProfileModel?.vehicleDetailData?.vehicleAttributes?.modelYear}"
        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp).testTag("make_model_year_text_tag"),
            text = makeModelYear,
            color = DarkGray,
            fontSize = 16.sp,
        )

        HorizontalDivider(
            color = LightGray,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(1.dp),
            thickness = 1.dp,
        )

        Column /*(modifier = Modifier.clickable {
            navController.navigate(VehicleProfileRoute.VehicleEditName.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        })*/ {
            Text(
                modifier =
                    Modifier
                        .background(White)
                        .fillMaxWidth()
                        .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                        .height(20.dp),
                text = "Nickname",
                color = Black,
                fontSize = 17.sp,
            )

            Text(
                modifier =
                    Modifier
                        .background(White)
                        .fillMaxWidth()
                        .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                        .height(20.dp).testTag("nick_name_text_tag"),
                text =
                    vehicleProfileModel?.vehicleDetailData?.vehicleAttributes?.name
                        ?: "NA",
                color = DarkGray,
                fontSize = 16.sp,
            )
        }

        HorizontalDivider(
            color = LightGray,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(1.dp),
            thickness = 1.dp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp),
            text = "Color",
            color = Black,
            fontSize = 17.sp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp).testTag("vehicle_color_tag"),
            text =
                vehicleProfileModel?.vehicleDetailData?.vehicleAttributes?.baseColor
                    ?: "NA",
            color = DarkGray,
            fontSize = 16.sp,
        )

        HorizontalDivider(
            color = LightGray,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(1.dp),
            thickness = 1.dp,
        )

        Text(
            modifier =
                Modifier
                    .background(MildWhite)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(40.dp),
            text = "Vehicle Connect Device",
            color = Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
        )

        HorizontalDivider(
            color = LightGray,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(1.dp),
            thickness = 1.dp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp),
            text = "Status",
            color = Black,
            fontSize = 17.sp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp).testTag("vehicle_status_tag"),
            text =
                when (vehicleProfileModel?.associatedDevice?.mAssociationStatus) {
                    ASSOCIATED -> "Active"
                    ASSOCIATION_INITIATED -> "Activation Pending"
                    else -> "Disassociated"
                },
            color = DarkGray,
            fontSize = 16.sp,
        )

        HorizontalDivider(
            color = LightGray,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(1.dp),
            thickness = 1.dp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp),
            text = "Serial Number",
            color = Black,
            fontSize = 17.sp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp).testTag("vehicle_imei_tag"),
            text = vehicleProfileModel?.associatedDevice?.mImei ?: "NA",
            color = DarkGray,
            fontSize = 16.sp,
        )

        HorizontalDivider(
            color = LightGray,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(1.dp),
            thickness = 1.dp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp),
            text = "Firmware Version",
            color = Black,
            fontSize = 17.sp,
        )

        Text(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(20.dp).testTag("vehicle_firmware_tag"),
            text =
                vehicleProfileModel?.associatedDevice?.mSoftwareVersion
                    ?: "NA",
            color = DarkGray,
            fontSize = 16.sp,
        )

        HorizontalDivider(
            color = LightGray,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(1.dp),
            thickness = 1.dp,
        )

        /*Text(
            modifier = Modifier
                .background(White)
                .fillMaxWidth()
                .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                .height(40.dp)
                .clickable {
                    openDialog?.value = true
                },
            text = "Remove Vehicle",
            color = Red,
            fontSize = 17.sp
        )*/
    }
}

@Composable
fun Activity.ShowVehicleTerminateDialogBox(
    vehicleProfileVM: VehicleProfileVM,
    openDialog: MutableState<Boolean>?,
) {
    ShowAlertDialog(
        title = getString(R.string.remove_vehicle_text),
        message = getString(R.string.remove_vehicle_sub_text),
        onDismiss = {
            openDialog?.value = false
        },
    ) {
        vehicleProfileVM.clickedOnRemoveVehicle(true)
        openDialog?.value = false
    }
}
