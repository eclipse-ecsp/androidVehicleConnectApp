package com.harman.vehicleconnects.ui.view.composes.dashboardcompose

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.harman.androidvehicleconnectsdk.userservice.model.UserProfile
import com.harman.vehicleconnects.R
import com.harman.vehicleconnects.helper.AppConstants
import com.harman.vehicleconnects.helper.AppConstants.SELECTED_DEVICE
import com.harman.vehicleconnects.helper.fromJson
import com.harman.vehicleconnects.helper.toastError
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel
import com.harman.vehicleconnects.models.viewmodels.DashboardVM
import com.harman.vehicleconnects.ui.theme.Black
import com.harman.vehicleconnects.ui.theme.DarkGray
import com.harman.vehicleconnects.ui.theme.LightGray
import com.harman.vehicleconnects.ui.theme.Orange
import com.harman.vehicleconnects.ui.theme.White
import com.harman.vehicleconnects.ui.view.activities.VehicleProfileActivity
import com.harman.vehicleconnects.ui.view.composes.remoteoperationcompose.ShowAlertDialog

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
fun SettingsMainCompose(
    activity: Activity,
    dashboardVM: DashboardVM,
    deviceId: String?,
    vehicleProfileList: MutableState<java.util.HashMap<String, VehicleProfileModel?>>?,
    openConfirmationDialog: MutableState<Boolean>?,
    showVehicleList: MutableState<Pair<Boolean, ArrayList<VehicleProfileModel?>>>
) {
    LaunchedEffect(Unit) {
        dashboardVM.setTopBarTitle(activity.getString(R.string.settings_text))
    }
    val userProfile =
        Gson().fromJson<UserProfile?>(AppConstants.getUserProfile(activity))
    val version = activity.packageManager.getPackageInfo(
        activity.packageName, 0
    ).versionName

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState()),
        // Parameters set to place the items in center
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            modifier = Modifier
                .background(LightGray)
                .fillMaxWidth()
                .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                .height(40.dp).testTag("account_text_tag"),
            text = "Account", color = Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .background(White)
                .fillMaxWidth()
                .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                .heightIn(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_account),
                contentDescription = "Settings_account_ic",
                tint = DarkGray
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp)
                    .fillMaxHeight().testTag("email_text_tag"),
                text = userProfile?.mEmail ?: "", color = Black,

                fontSize = 16.sp
            )
        }

        Text(
            modifier = Modifier
                .background(LightGray)
                .fillMaxWidth()
                .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                .height(40.dp).testTag("vehicle_name_text_tag"),
            text = vehicleProfileList?.value?.get(deviceId)?.vehicleDetailData?.vehicleAttributes?.name
                ?: "",
            color = Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .background(White)
                .fillMaxWidth()
                .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                .height(50.dp).testTag("vehicle_profile_tag")
                .clickable {
                    showVehicleList.value = Pair(false, showVehicleList.value.second)
                    if(!deviceId.isNullOrEmpty()) {
                        val intent = Intent(activity, VehicleProfileActivity::class.java)
                        intent.putExtra(SELECTED_DEVICE, vehicleProfileList?.value?.get(deviceId))
                        activity.startActivity(intent)
                    } else{
                        toastError(activity, "Please wait till the vehicle details are fetching.")
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_vehicle_selected),
                contentDescription = "Settings_vehicle_profile_ic",
                tint = DarkGray
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp)
                    .fillMaxHeight(),
                text = "Vehicle Profile", color = Black,
                fontSize = 16.sp
            )
        }

        Text(
            modifier = Modifier
                .background(LightGray)
                .fillMaxWidth()
                .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                .height(40.dp),
            text = "App", color = Black,
            fontSize = 18.sp
        )

        Row(
            modifier = Modifier
                .background(White)
                .fillMaxWidth()
                .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_help),
                contentDescription = "Settings_help_ic",
                tint = DarkGray
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp)
                    .fillMaxHeight(),
                text = "Help and Support", color = Black,
                fontSize = 18.sp
            )
        }

        HorizontalDivider(
            color = LightGray, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(1.dp), thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .background(White)
                .fillMaxWidth()
                .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_terms_of_use),
                contentDescription = "Settings_terms_of_use_ic",
                tint = DarkGray
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp)
                    .fillMaxHeight(),
                text = "Terms of Use", color = Black,
                fontSize = 18.sp
            )
        }

        HorizontalDivider(
            color = LightGray, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(1.dp), thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .background(White)
                .fillMaxWidth()
                .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_terms_of_use),
                contentDescription = "Settings_privacy_ic",
                tint = DarkGray
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp)
                    .fillMaxHeight(),
                text = "Privacy Policy", color = Black,
                fontSize = 18.sp
            )
        }

        HorizontalDivider(
            color = LightGray, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(1.dp), thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .background(White)
                .fillMaxWidth()
                .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_version),
                contentDescription = "Settings_app_version_ic",
                tint = DarkGray
            )

            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 13.dp)
                    .fillMaxHeight(),
                text = "App Version", color = Black,
                fontSize = 18.sp
            )
            Spacer(
                Modifier
                    .weight(3f)
                    .fillMaxHeight()
            )
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 14.dp)
                    .fillMaxHeight(),
                text = version ?: "--", color = DarkGray,
                fontSize = 18.sp
            )
            Spacer(
                Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
            )
        }

        HorizontalDivider(
            color = LightGray, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(1.dp), thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .background(White)
                .fillMaxWidth()
                .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                .height(50.dp).testTag("sign_out_layout_tag")
                .clickable { openConfirmationDialog?.value = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier.padding(end = 10.dp),
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = "Settings_sign_out_ic",
                tint = DarkGray
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp)
                    .fillMaxHeight(),
                text = "Sign Out", color = Orange,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun Activity.ShowConfirmationDialogBox(
    dashboardVM: DashboardVM,
    openConfirmationDialog: MutableState<Boolean>?
) {
    ShowAlertDialog(
        title = getString(R.string.sign_out_text),
        message = getString(R.string.sign_out_sub_text),
        onDismiss = {
            openConfirmationDialog?.value = false
        }) {
        dashboardVM.signOutClick()
        openConfirmationDialog?.value = false
    }
}