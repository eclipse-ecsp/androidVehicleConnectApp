package org.eclipse.ecsp.ui.view.composes.dashboardcompose
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
import org.eclipse.ecsp.userservice.model.UserProfile
import org.eclipse.ecsp.R
import org.eclipse.ecsp.helper.AppConstants
import org.eclipse.ecsp.helper.AppConstants.SELECTED_DEVICE
import org.eclipse.ecsp.helper.fromJson
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import org.eclipse.ecsp.models.viewmodels.DashboardVM
import org.eclipse.ecsp.ui.theme.Black
import org.eclipse.ecsp.ui.theme.DarkGray
import org.eclipse.ecsp.ui.theme.LightGray
import org.eclipse.ecsp.ui.theme.Orange
import org.eclipse.ecsp.ui.theme.White
import org.eclipse.ecsp.ui.view.activities.VehicleProfileActivity
import org.eclipse.ecsp.ui.view.composes.remoteoperationcompose.ShowAlertDialog
import org.eclipse.ecsp.userservice.service.UserServiceInterface

/**
 * SettingsCompose file contains the compose UI function used to handle the setting screen
 *
 */

@Composable
fun SettingsMainCompose(
    activity: Activity,
    dashboardVM: DashboardVM,
    deviceId: String?,
    vehicleProfileList: MutableState<java.util.HashMap<String, VehicleProfileModel?>>?,
    openConfirmationDialog: MutableState<Boolean>?,
    showVehicleList: MutableState<Pair<Boolean, ArrayList<VehicleProfileModel?>>>,
    passwordChangeDialog: MutableState<Boolean>?
) {
    LaunchedEffect(Unit) {
        dashboardVM.setTopBarTitle(activity.getString(R.string.settings_text))
    }
    val userProfile =
        Gson().fromJson<UserProfile?>(org.eclipse.ecsp.helper.AppConstants.getUserProfile(activity))
    val version =
        activity.packageManager.getPackageInfo(
            activity.packageName,
            0,
        ).versionName

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState()),
        // Parameters set to place the items in center
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            modifier =
                Modifier
                    .background(LightGray)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(40.dp).testTag("account_text_tag"),
            text = "Account",
            color = Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                    .heightIn(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_account),
                contentDescription = "Settings_account_ic",
                tint = DarkGray,
            )

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp)
                        .fillMaxHeight().testTag("email_text_tag"),
                text = userProfile?.mEmail ?: "",
                color = Black,
                fontSize = 16.sp,
            )
        }

        Row(
            modifier =
            Modifier
                .background(White)
                .fillMaxWidth()
                .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                .heightIn(50.dp)
                .clickable {
                    passwordChangeDialog?.value = true
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_account),
                contentDescription = "password_change_ic",
                tint = DarkGray,
            )

            Text(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp)
                    .fillMaxHeight().testTag("password_change_text_tag"),
                text = "Change Password",
                color = Black,
                fontSize = 16.sp,
            )
        }

        Text(
            modifier =
                Modifier
                    .background(LightGray)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(40.dp).testTag("vehicle_name_text_tag"),
            text =
                vehicleProfileList?.value?.get(deviceId)?.vehicleDetailData?.vehicleAttributes?.name
                    ?: "",
            color = Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                    .height(50.dp).testTag("vehicle_profile_tag")
                    .clickable {
                        showVehicleList.value = Pair(false, showVehicleList.value.second)
//                        if (!deviceId.isNullOrEmpty()) {
                            val intent = Intent(activity, VehicleProfileActivity::class.java)
                            intent.putExtra(SELECTED_DEVICE, vehicleProfileList?.value?.get(deviceId))
                            activity.startActivity(intent)
//                        } else
//                            {
//                                toastError(activity, "Please wait till the vehicle details are fetching.")
//                            }
                    },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_vehicle_selected),
                contentDescription = "Settings_vehicle_profile_ic",
                tint = DarkGray,
            )

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp)
                        .fillMaxHeight(),
                text = "Vehicle Profile",
                color = Black,
                fontSize = 16.sp,
            )
        }

        Text(
            modifier =
                Modifier
                    .background(LightGray)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(40.dp),
            text = "App",
            color = Black,
            fontSize = 18.sp,
        )

        Row(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                    .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_help),
                contentDescription = "Settings_help_ic",
                tint = DarkGray,
            )

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp)
                        .fillMaxHeight(),
                text = "Help and Support",
                color = Black,
                fontSize = 18.sp,
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

        Row(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                    .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_terms_of_use),
                contentDescription = "Settings_terms_of_use_ic",
                tint = DarkGray,
            )

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp)
                        .fillMaxHeight(),
                text = "Terms of Use",
                color = Black,
                fontSize = 18.sp,
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

        Row(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                    .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_terms_of_use),
                contentDescription = "Settings_privacy_ic",
                tint = DarkGray,
            )

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp)
                        .fillMaxHeight(),
                text = "Privacy Policy",
                color = Black,
                fontSize = 18.sp,
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

        Row(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                    .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_version),
                contentDescription = "Settings_app_version_ic",
                tint = DarkGray,
            )

            Text(
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .padding(top = 13.dp)
                        .fillMaxHeight(),
                text = "App Version",
                color = Black,
                fontSize = 18.sp,
            )
            Spacer(
                Modifier
                    .weight(3f)
                    .fillMaxHeight(),
            )
            Text(
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .padding(top = 14.dp)
                        .fillMaxHeight(),
                text = version ?: "--",
                color = DarkGray,
                fontSize = 18.sp,
            )
            Spacer(
                Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
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

        Row(
            modifier =
                Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 20.dp, end = 5.dp)
                    .height(50.dp).testTag("sign_out_layout_tag")
                    .clickable { openConfirmationDialog?.value = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = "Settings_sign_out_ic",
                tint = DarkGray,
            )

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp)
                        .fillMaxHeight(),
                text = "Sign Out",
                color = Orange,
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
fun ShowConfirmationDialogBox(
    userServiceInterface: UserServiceInterface,
    dashboardVM: DashboardVM,
    confirmationDialog: MutableState<Boolean>?,
    title: String, message: String
) {
    ShowAlertDialog(
        title = title,
        message = message,
        onDismiss = {
            confirmationDialog?.value = false
        },
    ) {
        dashboardVM.signOutClick(userServiceInterface)
        confirmationDialog?.value = false
    }
}

@Composable
fun ShowChangePasswordConfirmationDialogBox(
    dashboardVM: DashboardVM,
    confirmationDialog: MutableState<Boolean>?,
    title: String, message: String
) {
    ShowAlertDialog(
        title = title,
        message = message,
        onDismiss = {
            confirmationDialog?.value = false
        },
    ) {
        dashboardVM.setPasswordChangeTriggerValue(true)
        confirmationDialog?.value = false
    }
}

@Composable
fun ShowPasswordChangeStatusDialogBox(
    dialogMS: MutableState<Boolean>?,
    title: String, message: String
) {
    ShowAlertDialog(
        title = title,
        message = message,
        onDismiss = {
            dialogMS?.value = false
        },
    ) {
        dialogMS?.value = false
    }
}


