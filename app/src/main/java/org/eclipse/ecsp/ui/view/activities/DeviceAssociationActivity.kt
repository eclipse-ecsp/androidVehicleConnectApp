package org.eclipse.ecsp.ui.view.activities
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
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.eclipse.ecsp.vehicleservice.service.VehicleServiceInterface
import org.eclipse.ecsp.helper.AppConstants.DEVICE_ASSOCIATION
import org.eclipse.ecsp.helper.AppConstants.ENTER_SERIAL_NUM
import org.eclipse.ecsp.models.viewmodels.AppViewModelFactory
import org.eclipse.ecsp.models.viewmodels.DeviceAssociationVM
import org.eclipse.ecsp.ui.view.composes.TopBar
import org.eclipse.ecsp.ui.view.composes.deviceinstallationcompose.EnterSerialNumberScreenCompose
import org.eclipse.ecsp.ui.view.composes.deviceinstallationcompose.InstallDeviceMainScreenCompose

/**
 * Device association activity contains the device association feature
 *
 */
class DeviceAssociationActivity : BaseAppActivity() {
    private lateinit var navController: NavHostController
    private var deviceAssociationVM: DeviceAssociationVM? = null
    private var vehicleServiceInterface = VehicleServiceInterface.vehicleServiceInterface()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceAssociationVM = AppViewModelFactory(this).create(DeviceAssociationVM::class.java)

        deviceAssociationVM?.clickedOnSerialNumberSubmission()?.observe(this) {
            triggerAssociateDevice(it)
        }

        setContent {
            val title = deviceAssociationVM?.getTopBarTitle()?.observeAsState()
            navController = rememberNavController()
            Scaffold(
                topBar = {
                    title?.value?.let {
                        TopBar(it) {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                },
            ) { content ->
                NavHost(navController = navController, startDestination = DEVICE_ASSOCIATION) {
                    composable(DEVICE_ASSOCIATION) {
                        InstallDeviceMainScreenCompose(content, navController, deviceAssociationVM)
                    }
                    composable(ENTER_SERIAL_NUM) {
                        EnterSerialNumberScreenCompose(content = content, deviceAssociationVM)
                    }
                }
            }
        }
    }

    private fun triggerIMEIVerificationApi(inputValue: String) {
        deviceAssociationVM?.verifyDeviceIMEI(
            vehicleServiceInterface,
            inputValue,
        )?.observe(this@DeviceAssociationActivity) {
            val successStatus = it?.status?.requestStatus ?: false
            if (successStatus) {
                triggerAssociateDevice(inputValue)
            } else {
                deviceAssociationVM?.setLoadingStatus(false)
                Toast.makeText(
                    this@DeviceAssociationActivity,
                    it?.error?.message,
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    private fun triggerAssociateDevice(serialNumber: String) {
        deviceAssociationVM?.associateDevice(vehicleServiceInterface, serialNumber)
            ?.observe(this@DeviceAssociationActivity) {
                deviceAssociationVM?.setLoadingStatus(false)
                val successStatus = it?.status?.requestStatus ?: false
                if (successStatus) {
                    launchActivity()
                } else {
                    deviceAssociationVM?.setLoadingStatus(false)
                    Toast.makeText(
                        this@DeviceAssociationActivity,
                        it?.error?.message,
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
    }

    private fun launchActivity() {
        startActivity(Intent(this@DeviceAssociationActivity, DashboardActivity::class.java))
        finish()
    }
}
