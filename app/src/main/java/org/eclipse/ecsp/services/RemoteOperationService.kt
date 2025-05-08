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
                org.eclipse.ecsp.helper.AppConstants.PROCESSED_SUCCESS -> events.roEvents.data?.mState ?: ""
                org.eclipse.ecsp.helper.AppConstants.PENDING -> org.eclipse.ecsp.helper.AppConstants.PLEASE_WAIT
                else ->
                    when ((events?.roEvents?.data?.mState)?.lowercase()) {
                        org.eclipse.ecsp.helper.AppConstants.ON.lowercase() -> org.eclipse.ecsp.helper.AppConstants.OFF
                        org.eclipse.ecsp.helper.AppConstants.OFF.lowercase() -> org.eclipse.ecsp.helper.AppConstants.ON
                        org.eclipse.ecsp.helper.AppConstants.LOCKED.lowercase() -> org.eclipse.ecsp.helper.AppConstants.UNLOCKED
                        org.eclipse.ecsp.helper.AppConstants.UNLOCKED.lowercase() -> org.eclipse.ecsp.helper.AppConstants.LOCKED
                        org.eclipse.ecsp.helper.AppConstants.CLOSED.lowercase() -> {
                            if (events.roEvents.eventID == org.eclipse.ecsp.helper.AppConstants.WINDOW_EVENT_ID)
                                {
                                    org.eclipse.ecsp.helper.AppConstants.getWindowCurrentState(activity)
                                } else {
                                org.eclipse.ecsp.helper.AppConstants.OPENED
                            }
                        }
                        org.eclipse.ecsp.helper.AppConstants.OPENED.lowercase() -> {
                            if (events.roEvents.eventID == org.eclipse.ecsp.helper.AppConstants.WINDOW_EVENT_ID)
                                {
                                    org.eclipse.ecsp.helper.AppConstants.getWindowCurrentState(activity)
                                } else {
                                org.eclipse.ecsp.helper.AppConstants.CLOSED
                            }
                        }
                        org.eclipse.ecsp.helper.AppConstants.STARTED.lowercase() -> org.eclipse.ecsp.helper.AppConstants.STOPPED
                        org.eclipse.ecsp.helper.AppConstants.STOPPED.lowercase() -> org.eclipse.ecsp.helper.AppConstants.STARTED
                        org.eclipse.ecsp.helper.AppConstants.PARTIAL_OPENED.lowercase() -> org.eclipse.ecsp.helper.AppConstants.getWindowCurrentState(activity)
                        else -> ""
                    }
            }
        when (eventId) {
            org.eclipse.ecsp.helper.AppConstants.WINDOW_EVENT_ID ->
                tempList!![0] =
                    RemoteOperationItem.Window(
                        state,
                        org.eclipse.ecsp.helper.AppConstants.WINDOWS, setStateIcon(state, org.eclipse.ecsp.helper.AppConstants.WINDOWS),
                    )

            org.eclipse.ecsp.helper.AppConstants.LIGHTS_EVENT_ID ->
                tempList!![1] =
                    RemoteOperationItem.Light(
                        state,
                        org.eclipse.ecsp.helper.AppConstants.LIGHT, setStateIcon(state, org.eclipse.ecsp.helper.AppConstants.LIGHT),
                    )

            org.eclipse.ecsp.helper.AppConstants.ALARM_EVENT_ID ->
                tempList!![2] =
                    RemoteOperationItem.Alarm(
                        state,
                        org.eclipse.ecsp.helper.AppConstants.ALARM, setStateIcon(state, org.eclipse.ecsp.helper.AppConstants.ALARM),
                    )

            org.eclipse.ecsp.helper.AppConstants.DOOR_EVENT_ID ->
                tempList!![3] =
                    RemoteOperationItem.Door(
                        state,
                        org.eclipse.ecsp.helper.AppConstants.DOOR, setStateIcon(state, org.eclipse.ecsp.helper.AppConstants.DOOR),
                    )

            org.eclipse.ecsp.helper.AppConstants.ENGINE_EVENT_ID ->
                tempList!![4] =
                    RemoteOperationItem.Engine(
                        state,
                        org.eclipse.ecsp.helper.AppConstants.ENGINE, setStateIcon(state, org.eclipse.ecsp.helper.AppConstants.ENGINE),
                    )

            org.eclipse.ecsp.helper.AppConstants.TRUNK_EVENT_ID ->
                tempList!![5] =
                    RemoteOperationItem.Trunk(
                        state,
                        org.eclipse.ecsp.helper.AppConstants.TRUNK, setStateIcon(state, org.eclipse.ecsp.helper.AppConstants.TRUNK),
                    )
        }
        return tempList
    }
}
