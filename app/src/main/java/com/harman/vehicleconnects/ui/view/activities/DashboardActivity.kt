package com.harman.vehicleconnects.ui.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.harman.androidvehicleconnectsdk.notificationservice.model.AlertData
import com.harman.androidvehicleconnectsdk.roservice.model.RemoteOperationState
import com.harman.androidvehicleconnectsdk.userservice.model.UserProfile
import com.harman.vehicleconnects.helper.AppConstants
import com.harman.vehicleconnects.helper.AppConstants.defaultRoValueList
import com.harman.vehicleconnects.helper.AppConstants.defaultRoValuesList
import com.harman.vehicleconnects.helper.AppConstants.getUserProfile
import com.harman.vehicleconnects.helper.fromJson
import com.harman.vehicleconnects.helper.isInternetAvailable
import com.harman.vehicleconnects.helper.launchActivity
import com.harman.vehicleconnects.helper.toastError
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel
import com.harman.vehicleconnects.models.viewmodels.AppViewModelFactory
import com.harman.vehicleconnects.models.viewmodels.DashboardVM
import com.harman.vehicleconnects.models.viewmodels.NotificationVM
import com.harman.vehicleconnects.models.viewmodels.RemoteOperationVM
import com.harman.vehicleconnects.ui.theme.White
import com.harman.vehicleconnects.ui.view.composes.TopBar
import com.harman.vehicleconnects.ui.view.composes.dashboardcompose.BottomNavigationBar
import com.harman.vehicleconnects.ui.view.composes.dashboardcompose.ShowConfirmationDialogBox
import com.harman.vehicleconnects.ui.view.composes.dashboardcompose.mainCompose

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
class DashboardActivity : BaseAppActivity() {
    private val dashboardVM: DashboardVM by lazy {
        AppViewModelFactory(this@DashboardActivity).create(DashboardVM::class.java)
    }
    private val remoteOperationVM: RemoteOperationVM by lazy {
        AppViewModelFactory(this@DashboardActivity).create(RemoteOperationVM::class.java)
    }
    private val notificationVM: NotificationVM by lazy {
        AppViewModelFactory(this@DashboardActivity).create(NotificationVM::class.java)
    }
    private var showBottomSheet: MutableState<Triple<Boolean, String, String>>? = null
    private var showVehicleList = mutableStateOf(
        Pair<Boolean, ArrayList<VehicleProfileModel?>>(
            false,
            arrayListOf()
        )
    )
    private lateinit var navController: NavHostController
    private var isProgressBarLoading: MutableState<Boolean>? = null
    private var lazyStaggeredGridList = mutableStateOf(ArrayList(defaultRoValuesList))
    private var selectedVehicleId: MutableState<Triple<String, String, String>>? = null
    private var openDialog: MutableState<Triple<Boolean, String, String>>? = null
    private var openConfirmationDialog: MutableState<Boolean>? = null
    private var userId: String? = null
    private var vehicleProfileDataList = mutableStateOf(HashMap<String, VehicleProfileModel?>())
    private val alertList = mutableStateOf<ArrayList<AlertData>>(arrayListOf())
    private var notifyRoUpdate = mutableStateOf(0)
    private var selectedVehicleIndex: MutableState<Int>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        remoteOperationVM.clickedOnCheckRoRequest().observe(this@DashboardActivity) {
            if (it != null) checkRoRequestStatus(it.first, it.second, it.third)
        }
        remoteOperationVM.updateRoStatusData().observe(this@DashboardActivity) {
            if (it != null) updateROState(it.first, it.second, it.third)
        }
//        askNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        setContent {
            remoteOperationVM.getRoHistoryData().observeAsState().value.let {
                if(isInternetAvailable(this)) {
                    if (it != null) getRoHistory(it)
                } else toastError(this, "No internet connectivity")
            }

            val userProfile = Gson().fromJson<UserProfile?>(getUserProfile(this))
            userId = userProfile?.mId ?: ""
            selectedVehicleId = remember { mutableStateOf(Triple("", "", "")) }
            openDialog = remember { mutableStateOf(Triple(false, "", "")) }
            showBottomSheet = remember { mutableStateOf(Triple(false, "", "")) }
            isProgressBarLoading = remember { mutableStateOf(true) }
            openConfirmationDialog = remember { mutableStateOf(false) }
            navController = rememberNavController()
            selectedVehicleIndex = remember {
                mutableStateOf(-1)
            }
            processVehicleList(
                Gson().fromJson(
                    AppConstants.getVehicleList(
                        this@DashboardActivity
                    )
                )
            )
            LaunchedEffect(Unit) {
                dashboardVM.fetchAssociateDeviceList()
                triggerDeviceAssociationListApi()
            }
            dashboardVM.isSignOutClicked().observe(this@DashboardActivity) { isSignOut ->
                if (isSignOut) {
                    AppConstants.removeAll(this@DashboardActivity)
                    val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }
            }

            MaterialTheme {
                val title = dashboardVM.getTopBarTitle().observeAsState()
                Surface(color = White) {
                    Scaffold(
                        topBar = {
                            title.value?.let {
                                TopBar(it) { onBackPressedDispatcher.onBackPressed() }
                            }
                        }, bottomBar = {
                            BottomNavigationBar(navController = navController)
                        }, content = { padding ->
                            mainCompose(
                                this,
                                padding = padding,
                                navController,
                                dashboardVM,
                                remoteOperationVM,
                                notificationVM,
                                vehicleProfileDataList,
                                isProgressBarLoading,
                                showBottomSheet,
                                openDialog,
                                selectedVehicleId,
                                lazyStaggeredGridList,
                                showVehicleList,
                                openConfirmationDialog,
                                alertList,
                                notifyRoUpdate,
                                selectedVehicleIndex,
                                this@DashboardActivity
                            ) { launchActivity(this, DeviceAssociationActivity::class.java) }
                        }
                    )
                }
                if (openConfirmationDialog != null && openConfirmationDialog!!.value) {
                    ShowConfirmationDialogBox(dashboardVM, openConfirmationDialog)
                }
            }
        }
    }

    private fun processVehicleList(hashMap: HashMap<String, VehicleProfileModel?>) {
        if (hashMap.isNotEmpty()) {
            vehicleProfileDataList.value = hashMap
            if (ArrayList(hashMap.keys).isNotEmpty()) {
                notificationVM.updatingVehicleDetails(
                    remoteOperationVM, selectedVehicleIndex,
                    selectedVehicleId, showVehicleList,
                    navController, ArrayList(hashMap.values), ArrayList(hashMap.keys), hashMap
                )
            }/*else launchActivity(this, DeviceAssociationActivity::class.java)*/
        }
    }

    private fun triggerDeviceAssociationListApi() {
        isProgressBarLoading?.value = true
        dashboardVM.getAssociatedDeviceList().observe(this@DashboardActivity) {
//            dashboardVM.subscribeNotificationConfig(emailId?:"", it)
            isProgressBarLoading?.value = false
            it.let {
                AppConstants.setVehicleList(this@DashboardActivity, Gson().toJson(it))
                processVehicleList(it)
            }
        }
    }

    private fun checkRoRequestStatus(
        vehicleId: String,
        roRequestId: String,
        isFromLoop: Boolean
    ) {
        remoteOperationVM.checkRoRequestStatus(
            this@DashboardActivity,
            this@DashboardActivity,
            userId ?: "",
            vehicleId,
            roRequestId,
            isProgressBarLoading,
            lazyStaggeredGridList,
            notifyRoUpdate,
            isFromLoop
        )
    }

    private fun updateROState(
        remoteOperationState: RemoteOperationState,
        percentage: Int? = null,
        duration: Int? = null
    ) {
        isProgressBarLoading?.value = true
        remoteOperationVM.updateRoState(
            this@DashboardActivity, this@DashboardActivity,
            userId ?: "", selectedVehicleId?.value?.first ?: "",
            remoteOperationState, isProgressBarLoading,
            percentage, duration
        )
    }

    private fun getRoHistory(vehicleId: String) {
        isProgressBarLoading?.value = true
        remoteOperationVM.getRemoteOperationHistory(
            this@DashboardActivity,
            this@DashboardActivity, isProgressBarLoading, lazyStaggeredGridList,
            notifyRoUpdate, userId ?: "", vehicleId
        )
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Notifications permission not granted", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        )
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

}