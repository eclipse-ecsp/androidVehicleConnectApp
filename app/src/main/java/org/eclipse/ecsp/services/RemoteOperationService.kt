package org.eclipse.ecsp.services
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
import org.eclipse.ecsp.roservice.model.RoEventHistoryResponse
import org.eclipse.ecsp.helper.AppConstants
import org.eclipse.ecsp.models.dataclass.RemoteOperationItem
import org.eclipse.ecsp.ui.view.composes.remoteoperationcompose.setStateIcon

/**
 * RemoteOperationService class is used to handle the UI logic for remote operation screen
 *
 */
class RemoteOperationService {
    internal fun updateGridItem(
        events: RoEventHistoryResponse?,
        tempList: ArrayList<RemoteOperationItem>?,
        activity: Activity,
    ): ArrayList<RemoteOperationItem>? {
        val eventId = events?.roEvents?.eventID
        val state =
            when (events?.roStatus) {
                AppConstants.PROCESSED_SUCCESS -> events.roEvents.data?.mState ?: ""
                AppConstants.PENDING -> AppConstants.PLEASE_WAIT
                else ->
                    when ((events?.roEvents?.data?.mState)?.lowercase()) {
                        AppConstants.ON.lowercase() -> AppConstants.OFF
                        AppConstants.OFF.lowercase() -> AppConstants.ON
                        AppConstants.LOCKED.lowercase() -> AppConstants.UNLOCKED
                        AppConstants.UNLOCKED.lowercase() -> AppConstants.LOCKED
                        AppConstants.CLOSED.lowercase() -> {
                            if (events.roEvents.eventID == AppConstants.WINDOW_EVENT_ID)
                                {
                                    AppConstants.getWindowCurrentState(activity)
                                } else {
                                AppConstants.OPENED
                            }
                        }
                        AppConstants.OPENED.lowercase() -> {
                            if (events.roEvents.eventID == AppConstants.WINDOW_EVENT_ID)
                                {
                                    AppConstants.getWindowCurrentState(activity)
                                } else {
                                AppConstants.CLOSED
                            }
                        }
                        AppConstants.STARTED.lowercase() -> AppConstants.STOPPED
                        AppConstants.STOPPED.lowercase() -> AppConstants.STARTED
                        AppConstants.PARTIAL_OPENED.lowercase() -> AppConstants.getWindowCurrentState(activity)
                        else -> ""
                    }
            }
        when (eventId) {
            AppConstants.WINDOW_EVENT_ID ->
                tempList!![0] =
                    RemoteOperationItem.Window(
                        state,
                        AppConstants.WINDOWS, setStateIcon(state, AppConstants.WINDOWS),
                    )

            AppConstants.LIGHTS_EVENT_ID ->
                tempList!![1] =
                    RemoteOperationItem.Light(
                        state,
                        AppConstants.LIGHT, setStateIcon(state, AppConstants.LIGHT),
                    )

            AppConstants.ALARM_EVENT_ID ->
                tempList!![2] =
                    RemoteOperationItem.Alarm(
                        state,
                        AppConstants.ALARM, setStateIcon(state, AppConstants.ALARM),
                    )

            AppConstants.DOOR_EVENT_ID ->
                tempList!![3] =
                    RemoteOperationItem.Door(
                        state,
                        AppConstants.DOOR, setStateIcon(state, AppConstants.DOOR),
                    )

            AppConstants.ENGINE_EVENT_ID ->
                tempList!![4] =
                    RemoteOperationItem.Engine(
                        state,
                        AppConstants.ENGINE, setStateIcon(state, AppConstants.ENGINE),
                    )

            AppConstants.TRUNK_EVENT_ID ->
                tempList!![5] =
                    RemoteOperationItem.Trunk(
                        state,
                        AppConstants.TRUNK, setStateIcon(state, AppConstants.TRUNK),
                    )
        }
        return tempList
    }
}
