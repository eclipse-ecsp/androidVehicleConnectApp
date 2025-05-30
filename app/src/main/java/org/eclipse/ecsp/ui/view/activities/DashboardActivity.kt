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
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import org.eclipse.ecsp.R
import org.eclipse.ecsp.notificationservice.model.AlertData
import org.eclipse.ecsp.roservice.model.RemoteOperationState
import org.eclipse.ecsp.userservice.model.UserProfile
import org.eclipse.ecsp.helper.AppConstants
import org.eclipse.ecsp.helper.AppConstants.defaultRoValuesList
import org.eclipse.ecsp.helper.AppConstants.getUserProfile
import org.eclipse.ecsp.helper.fromJson
import org.eclipse.ecsp.helper.isInternetAvailable
import org.eclipse.ecsp.helper.launchActivity
import org.eclipse.ecsp.helper.toastError
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import org.eclipse.ecsp.models.viewmodels.AppViewModelFactory
import org.eclipse.ecsp.models.viewmodels.DashboardVM
import org.eclipse.ecsp.models.viewmodels.NotificationVM
import org.eclipse.ecsp.models.viewmodels.RemoteOperationVM
import org.eclipse.ecsp.ui.theme.White
import org.eclipse.ecsp.ui.view.composes.TopBar
import org.eclipse.ecsp.ui.view.composes.dashboardcompose.BottomNavigationBar
import org.eclipse.ecsp.ui.view.composes.dashboardcompose.ShowChangePasswordConfirmationDialogBox
import org.eclipse.ecsp.ui.view.composes.dashboardcompose.ShowConfirmationDialogBox
import org.eclipse.ecsp.ui.view.composes.dashboardcompose.MainCompose
import org.eclipse.ecsp.userservice.service.UserServiceInterface

/**
 * Dashboard activity class used to do UI for dashboard activities, which contains all the bottom menu.
 *
 *
 */
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
    private val userServiceInterface: UserServiceInterface by lazy {
        UserServiceInterface.authService(this@DashboardActivity)
    }
    private var showBottomSheet: MutableState<Triple<Boolean, String, String>>? = null
    private var showVehicleList =
        mutableStateOf(
            Pair<Boolean, ArrayList<VehicleProfileModel?>>(
                false,
                arrayListOf(),
            ),
        )
    private lateinit var navController: NavHostController
    private val isProgressBarLoading = mutableStateOf(false)
    private var lazyStaggeredGridList = mutableStateOf(ArrayList(defaultRoValuesList))
    private var selectedVehicleId: MutableState<Triple<String, String, String>>? = null
    private var openDialog: MutableState<Triple<Boolean, String, String>>? = null
    private var openConfirmationDialog: MutableState<Boolean>? = null
    private lateinit var passwordChangeDialog: MutableState<Boolean>
    private val dialogMS = mutableStateOf(Pair(false, ""))
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

        dashboardVM.isPasswordChangeTriggered().observe(this@DashboardActivity) {
            if (it) {
                isProgressBarLoading.value = true
                dashboardVM.changePasswordApiCall(userServiceInterface)
                    .observe(this@DashboardActivity) { customMessage ->
                        isProgressBarLoading.value = false
                        if (customMessage.status.requestStatus) {
                            dialogMS.value =
                                Pair(true, getString(R.string.password_change_success_status_text))
                        } else {
                            dialogMS.value =
                                Pair(true, getString(R.string.password_change_failure_status_text))
                        }
                    }
            }
        }
        askNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        setContent {
            remoteOperationVM.getRoHistoryData().observeAsState().value.let {
                if (isInternetAvailable(this)) {
                    if (it != null)
                        getRoHistory(it)
                } else {
                    toastError(this, "No internet connectivity")
                }
            }

            val userProfile = Gson().fromJson<UserProfile?>(getUserProfile(this))
            userId = userProfile?.mId ?: ""
            selectedVehicleId = remember { mutableStateOf(Triple("", "", "")) }
            openDialog = remember { mutableStateOf(Triple(false, "", "")) }
            showBottomSheet = remember { mutableStateOf(Triple(false, "", "")) }
            openConfirmationDialog = remember { mutableStateOf(false) }
            passwordChangeDialog = remember { mutableStateOf(false) }
            navController = rememberNavController()
            selectedVehicleIndex =
                remember {
                    mutableStateOf(-1)
                }
            processVehicleList(
                HashMap(Gson().fromJson<Map<String, VehicleProfileModel?>>(
                    AppConstants.getVehicleList(
                        this@DashboardActivity,
                    )
                ))
            )
            dashboardVM.fetchAssociateDeviceList()
            LaunchedEffect(Unit) {
                triggerDeviceAssociationListApi(userProfile?.mEmail ?: "")
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
                        },
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        },
                        content = { padding ->
                            MainCompose(
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
                                passwordChangeDialog,
                                alertList,
                                notifyRoUpdate,
                                selectedVehicleIndex,
                                this@DashboardActivity,
                            ) { launchActivity(this, DeviceAssociationActivity::class.java) }
                        },
                    )
                }
                if (openConfirmationDialog != null && openConfirmationDialog!!.value) {
                    ShowConfirmationDialogBox(
                        userServiceInterface,
                        dashboardVM,
                        openConfirmationDialog,
                        getString(R.string.sign_out_text),
                        getString(R.string.sign_out_sub_text)
                    )
                }
                if (passwordChangeDialog.value) {
                    ShowChangePasswordConfirmationDialogBox(
                        dashboardVM = dashboardVM,
                        confirmationDialog = passwordChangeDialog,
                        title = getString(R.string.password_change_title_text),
                        message = getString(R.string.password_change_sub_text)
                    )
                }
                if (dialogMS.value.first) {
                    AlertDialog(
                        onDismissRequest = {
                            dialogMS.value = Pair(false, "")
                        },
                        title = {
                            Text(text = dialogMS.value.second)
                        },
                        confirmButton = {
                            Button(
                                modifier = Modifier.testTag("confirm_btn_tag"),
                                onClick = {
                                    dialogMS.value = Pair(false, "")
                                },
                            ) {
                                Text("Close")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun processVehicleList(hashMap: HashMap<String, VehicleProfileModel?>) {
        if (hashMap.isNotEmpty()) {
            vehicleProfileDataList.value = hashMap
            if (ArrayList(hashMap.keys).isNotEmpty()) {
                notificationVM.updatingVehicleDetails(
                    remoteOperationVM,
                    selectedVehicleIndex,
                    selectedVehicleId,
                    showVehicleList,
                    navController,
                    ArrayList(hashMap.values),
                    ArrayList(hashMap.keys),
                    hashMap,
                )
            } // else launchActivity(this, DeviceAssociationActivity::class.java)
        }
    }

    private fun triggerDeviceAssociationListApi(emailId: String) {
        isProgressBarLoading.value = true
        dashboardVM.getAssociatedDeviceList().observe(this@DashboardActivity) {
            dashboardVM.subscribeNotificationConfig(emailId, it)
            isProgressBarLoading.value = false
            it.let {
                AppConstants.setVehicleList(this@DashboardActivity, Gson().toJson(it))
                processVehicleList(it)
            }
        }
    }

    private fun checkRoRequestStatus(
        vehicleId: String,
        roRequestId: String,
        isFromLoop: Boolean,
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
            isFromLoop,
        )
    }

    private fun updateROState(
        remoteOperationState: RemoteOperationState,
        percentage: Int? = null,
        duration: Int? = null,
    ) {
        isProgressBarLoading?.value = true
        remoteOperationVM.updateRoState(
            this@DashboardActivity,
            this@DashboardActivity,
            userId ?: "",
            selectedVehicleId?.value?.first ?: "",
            remoteOperationState,
            isProgressBarLoading,
            percentage,
            duration,
        )
    }

    private fun getRoHistory(vehicleId: String) {
        isProgressBarLoading?.value = true
        remoteOperationVM.getRemoteOperationHistory(
            this@DashboardActivity,
            this@DashboardActivity,
            isProgressBarLoading,
            lazyStaggeredGridList,
            notifyRoUpdate,
            userId ?: "",
            vehicleId,
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Notifications permission not granted", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
