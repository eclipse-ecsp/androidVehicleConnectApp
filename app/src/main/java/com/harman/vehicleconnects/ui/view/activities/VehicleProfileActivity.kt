package com.harman.vehicleconnects.ui.view.activities
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
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harman.androidvehicleconnectsdk.vehicleservice.model.vehicleprofile.PostVehicleAttributeData
import com.harman.vehicleconnects.helper.AppConstants
import com.harman.vehicleconnects.helper.parcelable
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel
import com.harman.vehicleconnects.models.routes.VehicleProfileRoute
import com.harman.vehicleconnects.models.viewmodels.AppViewModelFactory
import com.harman.vehicleconnects.models.viewmodels.VehicleProfileVM
import com.harman.vehicleconnects.ui.theme.White
import com.harman.vehicleconnects.ui.view.composes.TextFieldState
import com.harman.vehicleconnects.ui.view.composes.TopBar
import com.harman.vehicleconnects.ui.view.composes.deviceinstallationcompose.ProgressBar
import com.harman.vehicleconnects.ui.view.composes.vehicleprofilecompose.VehicleProfileMainCompose

/**
 * Vehicle Profile activity is used to hold the vehicle profile screen and actions
 *
 */
class VehicleProfileActivity : BaseAppActivity() {
    private lateinit var navController: NavHostController
    private val vehicleProfileVM: VehicleProfileVM by lazy {
        AppViewModelFactory(this@VehicleProfileActivity).create(VehicleProfileVM::class.java)
    }
    private var isProgressBarLoading: MutableState<Boolean>? = null
    private var vehicleProfileModel: VehicleProfileModel? = null
    private var inputValue: TextFieldState? = null
    private var openDialog: MutableState<Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vehicleProfileModel = intent.parcelable(AppConstants.SELECTED_DEVICE)
    }

    override fun onResume() {
        super.onResume()
        setContent {
            isProgressBarLoading = remember { mutableStateOf(false) }
            inputValue =
                remember {
                    TextFieldState()
                }
            vehicleProfileVM.isSaveBtnClicked().observe(this) {
                vehicleProfileModel?.associatedDevice?.mDeviceId?.let { it1 ->
                    updateVehicleProfileData(
                        it1,
                        it,
                    )
                }
            }
            vehicleProfileVM.isRemoveVehicleClicked().observe(this) {
                if (it) {
                    terminateVehicle()
                }
            }
            MaterialTheme {
                navController = rememberNavController()
                openDialog =
                    remember {
                        mutableStateOf(false)
                    }
                val title = vehicleProfileVM.getTopBarTitle().observeAsState()
                Surface(color = White) {
                    Scaffold(
                        topBar = {
                            title.value?.let {
                                TopBar(it) {
                                    onBackPressedDispatcher.onBackPressed()
                                }
                            }
                        },
                        content = { padding ->
                            mainCompose(padding = padding, navController)
                        },
                    )
                }
                /*if (openDialog != null && openDialog!!.value) {
                    ShowVehicleTerminateDialogBox(vehicleProfileVM, openDialog)
                }*/
            }
        }
    }

    @Composable
    fun mainCompose(
        padding: PaddingValues,
        navController: NavHostController,
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
            NavHostContainer(navController)
            isProgressBarLoading?.value?.let {
                ProgressBar(loading = it)
            }
        }
    }

    @Composable
    fun NavHostContainer(navController: NavHostController) {
        NavHost(
            navController,
            startDestination = VehicleProfileRoute.VehicleProfile.route,
        ) {
            composable(VehicleProfileRoute.VehicleProfile.route) {
                VehicleProfileMainCompose(
                    vehicleProfileModel,
                    vehicleProfileVM,
                    navController,
                    openDialog,
                )
            }
            /*composable(VehicleProfileRoute.VehicleEditName.route) {
                VehicleNameEditMainCompose(
                    inputValue = inputValue,
                    vehicleProfileVM = vehicleProfileVM,
                    vehicleProfileModel = vehicleProfileModel,
                )
            }*/
//            composable(VehicleProfileRoute.VehicleEditColor.route) { }
        }
    }

    private fun updateVehicleProfileData(
        deviceId: String,
        postVehicleAttributeData: PostVehicleAttributeData,
    ) {
        isProgressBarLoading?.value = true
        vehicleProfileVM.updateVehicleProfileData(deviceId, postVehicleAttributeData)
            .observe(this@VehicleProfileActivity) {
                isProgressBarLoading?.value = false
                Toast.makeText(
                    this@VehicleProfileActivity,
                    "Successfully updated",
                    Toast.LENGTH_LONG,
                ).show()
            }
    }

    private fun terminateVehicle() {
        if (vehicleProfileModel?.associatedDevice?.mDeviceId != null &&
            vehicleProfileModel?.associatedDevice?.mImei != null &&
            vehicleProfileModel?.associatedDevice?.mSerialNumber != null
        ) {
            isProgressBarLoading?.value = true
            vehicleProfileVM.terminateVehicle(
                vehicleProfileModel?.associatedDevice?.mSerialNumber!!,
                vehicleProfileModel?.associatedDevice?.mImei!!,
                vehicleProfileModel?.associatedDevice?.mSerialNumber!!,
            ).observe(this@VehicleProfileActivity) {
                isProgressBarLoading?.value = false
                if (it.response != null) {
                    Toast.makeText(
                        this@VehicleProfileActivity,
                        "Successfully Removed",
                        Toast.LENGTH_LONG,
                    ).show()
                } else
                    {
                        Toast.makeText(
                            this@VehicleProfileActivity,
                            "Error while removing vehicle",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
            }
        } else {
            Toast.makeText(
                this@VehicleProfileActivity,
                "Vehicle data is not available",
                Toast.LENGTH_LONG,
            ).show()
        }
    }
}
