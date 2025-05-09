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
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import org.eclipse.ecsp.notificationservice.model.AlertData
import org.eclipse.ecsp.roservice.model.RemoteOperationState
import org.eclipse.ecsp.helper.AppConstants
import org.eclipse.ecsp.helper.AppConstants.AJAR
import org.eclipse.ecsp.helper.AppConstants.PARTIAL_OPENED
import org.eclipse.ecsp.models.dataclass.RemoteOperationItem
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import org.eclipse.ecsp.models.routes.BottomNavItem
import org.eclipse.ecsp.models.viewmodels.DashboardVM
import org.eclipse.ecsp.models.viewmodels.NotificationVM
import org.eclipse.ecsp.models.viewmodels.RemoteOperationVM
import org.eclipse.ecsp.ui.theme.DarkGray
import org.eclipse.ecsp.ui.theme.LightBlue
import org.eclipse.ecsp.ui.theme.White
import org.eclipse.ecsp.ui.view.composes.deviceinstallationcompose.ProgressBar
import org.eclipse.ecsp.ui.view.composes.remoteoperationcompose.BottomSheetOptionsRoCompose
import org.eclipse.ecsp.ui.view.composes.remoteoperationcompose.RemoteOperationScreen

/**
 * DashboardMainCompose file is used to handle the Bottom Navigation items compose
 *
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        BottomNavItem::class.sealedSubclasses.map {
            it.objectInstance as BottomNavItem
        }.forEach { navItem ->
            val selected = currentRoute == navItem.route
            NavigationBarItem(
                selected = selected,
                modifier = Modifier.testTag("navigation_bar_tag"),
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(navItem.iconId),
                        contentDescription = null,
                        tint = if (selected) LightBlue else DarkGray,
                    )
                },
                label = {
                    Text(
                        navItem.label,
                        color = LightBlue,
                        modifier = Modifier.testTag("navigation_bar_text_tag"),
                    )
                },
                alwaysShowLabel = false,
            )
        }
    }
}

@Composable
fun Activity.NavHostContainer(
    activity: Activity,
    navController: NavHostController,
    dashboardVM: DashboardVM,
    remoteOperationVM: RemoteOperationVM,
    notificationVM: NotificationVM,
    vehicleProfileList: MutableState<HashMap<String, VehicleProfileModel?>>?,
    isProgressBarLoading: MutableState<Boolean>?,
    showBottomSheet: MutableState<Triple<Boolean, String, String>>?,
    openDialog: MutableState<Triple<Boolean, String, String>>?,
    selectedVehicleId: MutableState<Triple<String, String, String>>?,
    lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?,
    showVehicleList: MutableState<Pair<Boolean, ArrayList<VehicleProfileModel?>>>,
    openConfirmationDialog: MutableState<Boolean>?,
    notifyRoUpdate: MutableState<Int>,
    lifecycleOwner: LifecycleOwner,
    passwordChangeDialog: MutableState<Boolean>?
) {
    NavHost(
        navController,
        startDestination = BottomNavItem.RemoteOperation.route,
    ) {
        composable(BottomNavItem.RemoteOperation.route) {
            RemoteOperationScreen(
                activity,
                dashboardVM,
                remoteOperationVM,
                openDialog,
                showBottomSheet,
                selectedVehicleId,
                lazyStaggeredGridList,
                isProgressBarLoading,
                notifyRoUpdate,
                lifecycleOwner,
            )
        }
        composable(BottomNavItem.Settings.route) {
            SettingsMainCompose(
                this@NavHostContainer,
                dashboardVM,
                selectedVehicleId?.value?.first,
                vehicleProfileList,
                openConfirmationDialog,
                showVehicleList,
                passwordChangeDialog
            )
        }
        // disabled for current iteration
        /*composable(BottomNavItem.Notification.route) {
            NotificationMainCompose(
                dashboardVM,
                notificationVM,
                alertList,
                isProgressBarLoading,
                vehicleProfileList,
                selectedVehicleId,
                lifecycleOwner
            )
        }*/
    }
}

@Composable
fun mainCompose(
    activity: Activity,
    padding: PaddingValues,
    navController: NavHostController,
    dashboardVM: DashboardVM,
    remoteOperationVM: RemoteOperationVM,
    notificationVM: NotificationVM,
    vehicleProfileList: MutableState<HashMap<String, VehicleProfileModel?>>?,
    isProgressBarLoading: MutableState<Boolean>?,
    showBottomSheet: MutableState<Triple<Boolean, String, String>>?,
    openDialog: MutableState<Triple<Boolean, String, String>>?,
    selectedVehicleId: MutableState<Triple<String, String, String>>?,
    lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?,
    showVehicleList: MutableState<Pair<Boolean, ArrayList<VehicleProfileModel?>>>,
    openConfirmationDialog: MutableState<Boolean>?,
    passwordChangeDialog: MutableState<Boolean>?,
    alertList: MutableState<ArrayList<AlertData>>,
    notifyRoUpdate: MutableState<Int>,
    selectedVehicleIndex: MutableState<Int>?,
    lifecycleOwner: LifecycleOwner,
    launchActivity: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White)
                .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
//        if (showVehicleList.value.first) {
            VehicleSelectionListView(
                vehicleList = showVehicleList.value.second,
                selectedVehicleId?.value!!.first,
                selectedVehicleId.value.second,
                onVehicleSelection = { vehicleProfileModel, index ->
                    selectedVehicleId.value =
                        Triple(
                            vehicleProfileModel?.associatedDevice?.mDeviceId.toString(),
                            vehicleProfileModel?.vehicleDetailData?.vehicleAttributes?.name
                                ?: "No Name",
                            vehicleProfileModel?.vehicleDetailData?.vehicleId.toString(),
                        )
                    selectedVehicleIndex?.value = index
                    if (navController.currentBackStackEntry?.destination?.route == BottomNavItem.RemoteOperation.route) {
                        notifyRoUpdate.value = notifyRoUpdate.value + 1
                        remoteOperationVM.clickOnRoHistory(selectedVehicleId.value.first)
                    }
                },
            ) {
                launchActivity()
            }
//        }
        activity.NavHostContainer(
            activity,
            navController = navController, dashboardVM, remoteOperationVM, notificationVM,
            vehicleProfileList = vehicleProfileList, isProgressBarLoading, showBottomSheet,
            openDialog, selectedVehicleId, lazyStaggeredGridList,
            showVehicleList, openConfirmationDialog, notifyRoUpdate, lifecycleOwner,passwordChangeDialog
        )

        isProgressBarLoading?.value?.let {
            ProgressBar(loading = it)
        }

        if (showBottomSheet!!.value.first) {
            if (showBottomSheet.value.second == AppConstants.WINDOWS) {
                AppConstants.setWindowCurrentState(
                    activity,
                    showBottomSheet.value.third.ifEmpty { "Closed" },
                )
            }
            BottomSheetOptionsRoCompose(
                titleText = showBottomSheet.value.second,
                selectedState = showBottomSheet.value.third,
                onDismiss = {
                    showBottomSheet.value = Triple(false, "", "")
                },
            ) {
                if (showBottomSheet.value.third != it.second) {
                    showBottomSheet.value = Triple(false, "", "")
                    Toast.makeText(
                        activity,
                        "${it.first} : ${it.second}",
                        Toast.LENGTH_LONG,
                    ).show()
                    if (lazyStaggeredGridList != null) {
                        when (it.first) {
                            AppConstants.WINDOWS -> {
                                remoteOperationVM.clickOnUpdateRoStatus(
                                    when (it.second) {
                                        AppConstants.CLOSED -> RemoteOperationState.WindowClose
                                        AppConstants.OPENED -> RemoteOperationState.WindowOpen
                                        else -> RemoteOperationState.WindowsAjar
                                    },
                                    percentage = 60,
                                    duration = 8,
                                )
                            }

                            AppConstants.LIGHT -> {
                                remoteOperationVM.clickOnUpdateRoStatus(
                                    when (it.second) {
                                        AppConstants.ON -> RemoteOperationState.LightsOn
                                        AppConstants.OFF -> RemoteOperationState.LightsOff
                                        else -> RemoteOperationState.LightsFlash
                                    },
                                    duration = 10,
                                )
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "${showBottomSheet.value.second} already ${
                            if (showBottomSheet.value.third.lowercase() == PARTIAL_OPENED.lowercase()) {
                                AJAR
                            } else {
                                showBottomSheet.value.third
                            }}",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        }
    }
}
