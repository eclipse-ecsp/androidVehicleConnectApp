package com.harman.vehicleconnects.ui.view.composes.remoteoperationcompose

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.harman.androidvehicleconnectsdk.roservice.model.RemoteOperationState
import com.harman.androidvehicleconnectsdk.userservice.model.UserProfile
import com.harman.vehicleconnects.R
import com.harman.vehicleconnects.helper.AppConstants
import com.harman.vehicleconnects.helper.fromJson
import com.harman.vehicleconnects.helper.isInternetAvailable
import com.harman.vehicleconnects.helper.toastError
import com.harman.vehicleconnects.models.dataclass.RemoteOperationItem
import com.harman.vehicleconnects.models.viewmodels.DashboardVM
import com.harman.vehicleconnects.models.viewmodels.RemoteOperationVM

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
fun Activity.RemoteOperationScreen(
    activity: Activity,
    dashboardVM: DashboardVM,
    remoteOperationVM: RemoteOperationVM,
    openDialog: MutableState<Triple<Boolean, String, String>>?,
    showBottomSheet: MutableState<Triple<Boolean, String, String>>?,
    selectedVehicleId: MutableState<Triple<String, String, String>>?,
    lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?,
    isProgressBarLoading: MutableState<Boolean>?,
    notifyRoUpdate: MutableState<Int>,
    lifecycleOwner: LifecycleOwner,
) {
    LaunchedEffect(Unit) {
        dashboardVM.setTopBarTitle(getString(R.string.remote_control_text))
    }

    LaunchedEffect(notifyRoUpdate.value) {
        Log.e("RO UPDATED", "Ro list updated")
    }

    RemoteOperationGridViewCompose(lazyStaggeredGridList?.value) {
        if (isInternetAvailable(activity)) {
            if (selectedVehicleId?.value?.first != null && selectedVehicleId.value.first.isNotEmpty()
                && isProgressBarLoading?.value == false
            ) {
                remoteOperationVM.gridItemClick(
                    it.itemName,
                    it.statusText,
                    showBottomSheet,
                    openDialog
                )
            }
        } else {
            toastError(activity, "No internet connectivity")
        }
    }

    if (openDialog!!.value.first) {
        when (openDialog.value.third) {
            AppConstants.ALARM -> {
                ShowAlertDialog(title = if (openDialog.value.second.lowercase() == AppConstants.OFF.lowercase())
                    getString(R.string.alarm_confirm_activation_text)
                else getString(R.string.alarm_confirm_deactivation_text), onDismiss = {
                    closeAlertDialog(openDialog)
                }) {
                    when (openDialog.value.second.lowercase()) {
                        AppConstants.ON.lowercase() -> onClickAction(
                            remoteOperationVM,
                            this,
                            isProgressBarLoading,
                            lifecycleOwner,
                            selectedVehicleId!!.value.first,
                            RemoteOperationState.AlarmOff,
                            8
                        )

                        AppConstants.OFF.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.AlarmOn,
                                8
                            )
                    }
                    closeAlertDialog(openDialog)
                }
            }

            AppConstants.DOOR -> {
                ShowAlertDialog(title = if (openDialog.value.second.lowercase() == AppConstants.LOCKED.lowercase())
                    getString(R.string.door_unlock_confirm_text)
                else getString(R.string.door_lock_confirm_text), onDismiss = {
                    closeAlertDialog(openDialog)
                }) {
                    when (openDialog.value.second.lowercase()) {
                        AppConstants.LOCKED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.DoorsUnLocked,
                                null
                            )

                        AppConstants.UNLOCKED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.DoorsLocked,
                                null
                            )
                    }
                    closeAlertDialog(openDialog)
                }
            }

            AppConstants.ENGINE -> {
                ShowAlertDialog(
                    title = if (openDialog.value.second.lowercase() ==
                        AppConstants.STOPPED.lowercase()
                    ) getString(
                        R.string.engin_start_confirm_text
                    )
                    else getString(R.string.engin_stop_confirm_text), onDismiss = {
                        closeAlertDialog(openDialog)
                    }) {
                    when (openDialog.value.second.lowercase()) {
                        AppConstants.STARTED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.EngineStop,
                                8
                            )

                        AppConstants.STOPPED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.EngineStart,
                                8
                            )
                    }
                    closeAlertDialog(openDialog)
                }
            }

            AppConstants.TRUNK -> {
                ShowAlertDialog(title = if (openDialog.value.second.lowercase() == AppConstants.LOCKED.lowercase()) getString(
                    R.string.trunk_opening_text
                )
                else getString(R.string.trunk_closing_text), onDismiss = {
                    closeAlertDialog(openDialog)
                }) {
                    when (openDialog.value.second.lowercase()) {
                        AppConstants.UNLOCKED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.TrunkLocked,
                                null
                            )

                        AppConstants.LOCKED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.TrunkUnLocked,
                                null
                            )
                    }
                    closeAlertDialog(openDialog)
                }
            }
        }
    }
}

private fun isAnyOperationRunning(lazyStaggeredGridList: MutableState<ArrayList<RemoteOperationItem>>?): Pair<Boolean, String> {
    var isRunning = false
    var eventType = ""
    lazyStaggeredGridList?.value?.forEach {
        if (it.statusText.lowercase() == AppConstants.PLEASE_WAIT.lowercase()) {
            isRunning = true
            eventType = it.itemName
            return@forEach
        }
    }
    return Pair(isRunning, eventType)
}

private fun onClickAction(
    remoteOperationVM: RemoteOperationVM,
    activity: Activity,
    isProgressBarLoading: MutableState<Boolean>?,
    lifecycleOwner: LifecycleOwner,
    vehicleId: String, remoteOperationState: RemoteOperationState, duration: Int?
) {
    val userProfile =
        Gson().fromJson<UserProfile?>(AppConstants.getUserProfile(activity))
    isProgressBarLoading?.value = true
    userProfile?.mId.let {
        if (it != null) {
            remoteOperationVM.updateRoState(
                activity, lifecycleOwner,
                it,
                vehicleId,
                remoteOperationState,
                isProgressBarLoading,
                null,
                duration,
                true
            )
        }
    }
}

private fun closeAlertDialog(openDialog: MutableState<Triple<Boolean, String, String>>?) {
    openDialog?.value = Triple(false, "", "")
}

fun setStateIcon(state: String, event: String): Int {
    var iconId = 0
    when (event) {
        AppConstants.WINDOWS -> {
            iconId = when (state.lowercase()) {
                AppConstants.OPENED.lowercase() -> R.drawable.ic_windows_open
                AppConstants.CLOSED.lowercase() -> R.drawable.ic_windows_closed
                else -> R.drawable.ic_windows_ajar
            }
        }

        AppConstants.LIGHT -> {
            iconId = when (state.lowercase()) {
                AppConstants.ON.lowercase() -> R.drawable.ic_lights_on
                AppConstants.OFF.lowercase() -> R.drawable.ic_lights_off
                else -> R.drawable.ic_flash_lights
            }
        }

        AppConstants.DOOR -> {
            iconId = when (state.lowercase()) {
                AppConstants.LOCKED.lowercase() -> R.drawable.ic_door_locked
                else -> R.drawable.ic_door_unlocked
            }
        }

        AppConstants.TRUNK -> {
            iconId = when (state.lowercase()) {
                AppConstants.UNLOCKED.lowercase() -> R.drawable.ic_trunk_open
                else -> R.drawable.ic_trunk_close
            }
        }

        AppConstants.ALARM -> iconId = R.drawable.ic_alarm
        AppConstants.ENGINE -> iconId = R.drawable.ic_ignition

    }
    return iconId
}