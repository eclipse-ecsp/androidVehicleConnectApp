package org.eclipse.ecsp.helper

import android.app.Activity
import android.content.Context
import org.eclipse.ecsp.models.dataclass.RemoteOperationItem
import org.eclipse.ecsp.ui.view.composes.remoteoperationcompose.setStateIcon

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
object AppConstants {
    const val DEVICE_ID = "DeviceId"
    const val DEVICE_ASSOCIATION = "Device Installation"
    const val ENTER_SERIAL_NUM = "Enter Serial Number"
    const val ASSOCIATION_INITIATED = "ASSOCIATION_INITIATED"
    const val ASSOCIATED = "ASSOCIATED"
    const val SUSPENDED = "SUSPENDED"
    const val DISASSOCIATED = "DISASSOCIATED"

    const val OFF = "Off"
    const val ON = "On"
    const val FLASHING = "Flashing"
    const val FLASH = "Flash"
    const val CLOSED = "Closed"
    const val CLOSE = "Close"
    const val OPENED = "Opened"
    const val OPEN = "Open"
    const val AJAR = "Ajar"
    const val PARTIAL_OPENED = "PARTIAL_OPENED"
    const val LOCKED = "Locked"
    const val UNLOCKED = "Unlocked"
    const val STARTED = "Started"
    const val STOPPED = "Stopped"
    const val IGNITION_ENABLED = "IGNITION_ENABLED"
    const val IGNITION_DISABLED = "IGNITION_DISABLED"

    const val WINDOWS = "Windows"
    const val LIGHT = "Lights"
    const val ALARM = "Alarm Signal"
    const val DOOR = "Door"
    const val ENGINE = "Engine"
    const val TRUNK = "Trunk"

    internal const val DOOR_EVENT_ID = "RemoteOperationDoors"
    internal const val LIGHTS_EVENT_ID = "RemoteOperationLights"
    internal const val ENGINE_EVENT_ID = "RemoteOperationEngine"
    internal const val ALARM_EVENT_ID = "RemoteOperationAlarm"
    internal const val TRUNK_EVENT_ID = "RemoteOperationTrunk"
    internal const val WINDOW_EVENT_ID = "RemoteOperationWindows"
    internal const val PROCESSED_SUCCESS = "PROCESSED_SUCCESS"
    internal const val PENDING = "PENDING"
    internal const val FORCED_FAILURE = "FORCED_FAILURE"
    internal const val TTL_EXPIRED = "TTL_EXPIRED"
    internal const val PLEASE_WAIT = "Please wait"
    internal const val PROCESSED_FAILED = "PROCESSED_FAILED"

    // alert types
    const val ALERT_CURFEW = "CurfewViolation"
    const val ALERT_BOUNDARY = "GeoFence"
    const val ALERT_IDLE = "Idle"
    const val ALERT_SPEED = "OverSpeeding"
    const val ALERT_DONGLE_STATUS = "DongleStatus"
    const val ALERT_TOW = "Tow"
    const val ALERT_ACCIDENT = "Collisions"
    const val ALERT_DISTURBANCE = "Disturbance"
    private const val ALERT_BREAK_IN_WARNING = "BreakInWarning"
    const val ALERT_LOW_BATTERY = "LowBattery"
    const val ALERT_IMPACT_DETECTION = "Impact"
    const val ALERT_LOW_FUEL = "LowFuel"
    const val ALERT_FIRMWARE_UPGRADE = "FirmwareUpgraded"
    const val ALERT_FIRMWARE_DOWNLOADED = "FirmwareDownloaded"
    const val ALERT_AIRBAG_DEPLOY = "AirbagDeployed"
    const val ALERT_OIL_PRESSURE_WARNING = "OilPressureWarning"
    const val ALERT_BREAK_WARNING = "BrakeWarning"
    const val ALERT_TIRE_PRESSURE_WARNING = "TirePressureWarning"
    const val GLOBAL_DOOR_LOCK = "GlobalDoorLockAlert"
    const val EPID_TPMS_ALERT = "EPIDTPMSAlert"
    const val SEATBELT_ALERT = "SeatBeltAlert"
    private const val ALERT_GENERIC_NOTIFICATION = "GenericNotificationEvent"
    private const val WINDOW_CURRENT_STATE = "WindowCurrentState"
    const val VEHICLE_PROFILE = "VehicleProfile"
    const val VEHICLE_EDIT_NAME = "VehicleEditName"
    const val VEHICLE_EDIT_COLOR = "VehicleEditColor"

    // generic constant
    const val SELECTED_DEVICE = "selected_device"
    const val EXTRA_MESSAGE = "message"
    const val EXTRA_ALERT = "alert"
    const val EXTRA_VEHICLE_ID = "vehicle_id"

    val defaultRoValuesList =
        listOf(
            RemoteOperationItem.Window(
                CLOSED,
                WINDOWS, setStateIcon(
                    CLOSED,
                    WINDOWS
                )),
            RemoteOperationItem.Light(
                OFF,
                LIGHT, setStateIcon(
                    OFF,
                    LIGHT
                )),
            RemoteOperationItem.Alarm(
                OFF,
                ALARM, setStateIcon(
                    OFF,
                    ALARM
                )),
            RemoteOperationItem.Door(
                LOCKED,
                DOOR, setStateIcon(
                    LOCKED,
                    DOOR
                )),
            RemoteOperationItem.Engine(
                STOPPED,
                ENGINE, setStateIcon(
                    STOPPED,
                    ENGINE
                )),
            RemoteOperationItem.Trunk(
                LOCKED,
                TRUNK, setStateIcon(
                    LOCKED,
                    TRUNK
                )),
        )

    val ALERT_TYPES =
        listOf(
            ALERT_CURFEW,
            ALERT_BOUNDARY,
            ALERT_SPEED,
            ALERT_DONGLE_STATUS,
            ALERT_TOW,
            ALERT_LOW_FUEL,
            ALERT_IDLE,
            ALERT_DISTURBANCE,
            ALERT_FIRMWARE_UPGRADE,
            ALERT_FIRMWARE_DOWNLOADED,
            ALERT_BREAK_IN_WARNING,
            GLOBAL_DOOR_LOCK,
            SEATBELT_ALERT,
            EPID_TPMS_ALERT,
            ALERT_GENERIC_NOTIFICATION,
        )

    private const val SHARED_PREF = "shared_pref"
    private const val USER_PROFILE = "user_profile"
    private const val VEHICLE_LIST = "vehicle_list"

    fun getUserProfile(activity: Activity?): String {
        val sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPref?.getString(USER_PROFILE, "").toString()
    }

    fun setUserProfile(
        activity: Activity?,
        value: String?,
    ) {
        val sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(USER_PROFILE, value)
            apply()
        }
    }

    fun removeAll(activity: Activity?)  {
        val sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE) ?: return
        sharedPref.edit().clear().apply()
    }

    fun getVehicleList(activity: Activity?): String  {
        val sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPref?.getString(VEHICLE_LIST, "{}").toString()
    }

    fun setVehicleList(
        activity: Activity?,
        value: String?,
    ) {
        val sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(VEHICLE_LIST, value)
            apply()
        }
    }

    fun getWindowCurrentState(activity: Activity?): String  {
        val sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPref?.getString(WINDOW_CURRENT_STATE, "Closed").toString()
    }

    fun setWindowCurrentState(
        activity: Activity?,
        value: String?,
    ) {
        val sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(WINDOW_CURRENT_STATE, value)
            apply()
        }
    }

    fun getVehicleStatus(status: String?): String{
        return when(status){
            DISASSOCIATED -> "Disassociated"
            ASSOCIATED -> "Active"
            ASSOCIATION_INITIATED -> "Pending"
            SUSPENDED -> "Suspended"
            else -> "No Status"
        }
    }
}
