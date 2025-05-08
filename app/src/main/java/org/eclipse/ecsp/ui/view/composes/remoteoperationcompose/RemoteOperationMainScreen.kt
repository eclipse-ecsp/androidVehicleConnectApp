package org.eclipse.ecsp.ui.view.composes.remoteoperationcompose

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
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import org.eclipse.ecsp.roservice.model.RemoteOperationState
import org.eclipse.ecsp.userservice.model.UserProfile
import org.eclipse.ecsp.R
import org.eclipse.ecsp.helper.AppConstants
import org.eclipse.ecsp.helper.fromJson
import org.eclipse.ecsp.helper.isInternetAvailable
import org.eclipse.ecsp.helper.toastError
import org.eclipse.ecsp.models.dataclass.RemoteOperationItem
import org.eclipse.ecsp.models.viewmodels.DashboardVM
import org.eclipse.ecsp.models.viewmodels.RemoteOperationVM

/**
 * RemoteOperationMainScreen contains RO main screen compose functions
 *
 */

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

    RemoteOperationGridViewCompose(
        (selectedVehicleId?.value?.first != null && selectedVehicleId.value.first.isNotEmpty()),
        lazyStaggeredGridList?.value
    ) {
        if (isInternetAvailable(activity)) {
            if (selectedVehicleId?.value?.first != null && selectedVehicleId.value.first.isNotEmpty() &&
                isProgressBarLoading?.value == false
            ) {
                remoteOperationVM.gridItemClick(
                    it.itemName,
                    it.statusText,
                    showBottomSheet,
                    openDialog,
                )
            }
        } else {
            toastError(activity, "No internet connectivity")
        }
    }

    if (openDialog!!.value.first) {
        when (openDialog.value.third) {
            org.eclipse.ecsp.helper.AppConstants.ALARM -> {
                ShowAlertDialog(
                    title =
                    if (openDialog.value.second.lowercase() == org.eclipse.ecsp.helper.AppConstants.OFF.lowercase()) {
                        getString(R.string.alarm_confirm_activation_text)
                    } else {
                        getString(R.string.alarm_confirm_deactivation_text)
                    },
                    onDismiss = {
                        closeAlertDialog(openDialog)
                    },
                ) {
                    when (openDialog.value.second.lowercase()) {
                        org.eclipse.ecsp.helper.AppConstants.ON.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.AlarmOff,
                                8,
                            )

                        org.eclipse.ecsp.helper.AppConstants.OFF.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.AlarmOn,
                                8,
                            )
                    }
                    closeAlertDialog(openDialog)
                }
            }

            org.eclipse.ecsp.helper.AppConstants.DOOR -> {
                ShowAlertDialog(
                    title =
                    if (openDialog.value.second.lowercase() == org.eclipse.ecsp.helper.AppConstants.LOCKED.lowercase()) {
                        getString(R.string.door_unlock_confirm_text)
                    } else {
                        getString(R.string.door_lock_confirm_text)
                    },
                    onDismiss = {
                        closeAlertDialog(openDialog)
                    },
                ) {
                    when (openDialog.value.second.lowercase()) {
                        org.eclipse.ecsp.helper.AppConstants.LOCKED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.DoorsUnLocked,
                                null,
                            )

                        org.eclipse.ecsp.helper.AppConstants.UNLOCKED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.DoorsLocked,
                                null,
                            )
                    }
                    closeAlertDialog(openDialog)
                }
            }

            org.eclipse.ecsp.helper.AppConstants.ENGINE -> {
                ShowAlertDialog(
                    title =
                    if (openDialog.value.second.lowercase() ==
                        org.eclipse.ecsp.helper.AppConstants.STOPPED.lowercase()
                    ) {
                        getString(
                            R.string.engin_start_confirm_text,
                        )
                    } else {
                        getString(R.string.engin_stop_confirm_text)
                    },
                    onDismiss = {
                        closeAlertDialog(openDialog)
                    },
                ) {
                    when (openDialog.value.second.lowercase()) {
                        org.eclipse.ecsp.helper.AppConstants.STARTED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.EngineStop,
                                8,
                            )

                        org.eclipse.ecsp.helper.AppConstants.STOPPED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.EngineStart,
                                8,
                            )
                    }
                    closeAlertDialog(openDialog)
                }
            }

            org.eclipse.ecsp.helper.AppConstants.TRUNK -> {
                ShowAlertDialog(
                    title =
                    if (openDialog.value.second.lowercase() == org.eclipse.ecsp.helper.AppConstants.LOCKED.lowercase()) {
                        getString(
                            R.string.trunk_opening_text,
                        )
                    } else {
                        getString(R.string.trunk_closing_text)
                    },
                    onDismiss = {
                        closeAlertDialog(openDialog)
                    },
                ) {
                    when (openDialog.value.second.lowercase()) {
                        org.eclipse.ecsp.helper.AppConstants.UNLOCKED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.TrunkLocked,
                                null,
                            )

                        org.eclipse.ecsp.helper.AppConstants.LOCKED.lowercase() ->
                            onClickAction(
                                remoteOperationVM,
                                this,
                                isProgressBarLoading,
                                lifecycleOwner,
                                selectedVehicleId!!.value.first,
                                RemoteOperationState.TrunkUnLocked,
                                null,
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
        if (it.statusText.lowercase() == org.eclipse.ecsp.helper.AppConstants.PLEASE_WAIT.lowercase()) {
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
    vehicleId: String,
    remoteOperationState: RemoteOperationState,
    duration: Int?,
) {
    val userProfile =
        Gson().fromJson<UserProfile?>(org.eclipse.ecsp.helper.AppConstants.getUserProfile(activity))
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
                true,
            )
        }
    }
}

private fun closeAlertDialog(openDialog: MutableState<Triple<Boolean, String, String>>?) {
    openDialog?.value = Triple(false, "", "")
}

fun setStateIcon(
    state: String,
    event: String,
): Int {
    var iconId = 0
    when (event) {
        org.eclipse.ecsp.helper.AppConstants.WINDOWS -> {
            iconId =
                when (state.lowercase()) {
                    org.eclipse.ecsp.helper.AppConstants.OPENED.lowercase() -> R.drawable.ic_windows_open
                    org.eclipse.ecsp.helper.AppConstants.CLOSED.lowercase() -> R.drawable.ic_windows_closed
                    else -> R.drawable.ic_windows_ajar
                }
        }

        org.eclipse.ecsp.helper.AppConstants.LIGHT -> {
            iconId =
                when (state.lowercase()) {
                    org.eclipse.ecsp.helper.AppConstants.ON.lowercase() -> R.drawable.ic_lights_on
                    org.eclipse.ecsp.helper.AppConstants.OFF.lowercase() -> R.drawable.ic_lights_off
                    else -> R.drawable.ic_flash_lights
                }
        }

        org.eclipse.ecsp.helper.AppConstants.DOOR -> {
            iconId =
                when (state.lowercase()) {
                    org.eclipse.ecsp.helper.AppConstants.LOCKED.lowercase() -> R.drawable.ic_door_locked
                    else -> R.drawable.ic_door_unlocked
                }
        }

        org.eclipse.ecsp.helper.AppConstants.TRUNK -> {
            iconId =
                when (state.lowercase()) {
                    org.eclipse.ecsp.helper.AppConstants.UNLOCKED.lowercase() -> R.drawable.ic_trunk_open
                    else -> R.drawable.ic_trunk_close
                }
        }

        org.eclipse.ecsp.helper.AppConstants.ALARM -> iconId = R.drawable.ic_alarm
        org.eclipse.ecsp.helper.AppConstants.ENGINE -> iconId = R.drawable.ic_ignition
    }
    return iconId
}
