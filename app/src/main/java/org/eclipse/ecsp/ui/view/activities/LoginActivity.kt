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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.gson.Gson
import org.eclipse.ecsp.helper.AppManager
import org.eclipse.ecsp.userservice.model.UserProfile
import org.eclipse.ecsp.userservice.service.UserServiceInterface
import org.eclipse.ecsp.helper.AppConstants
import org.eclipse.ecsp.helper.dataToJson
import org.eclipse.ecsp.models.dataclass.EnvironmentListData
import org.eclipse.ecsp.models.viewmodels.AppViewModelFactory
import org.eclipse.ecsp.models.viewmodels.LoginActivityVM
import org.eclipse.ecsp.ui.view.composes.deviceinstallationcompose.ProgressBar
import org.eclipse.ecsp.ui.view.composes.logincompose.EnvironmentSpinner
import org.eclipse.ecsp.ui.view.composes.logincompose.ImageLogoCompose
import org.eclipse.ecsp.ui.view.composes.logincompose.SignInButton
import org.eclipse.ecsp.ui.view.composes.logincompose.SignUpButton

/**
 * LoginActivity Class is to display login UI and perform the login actions
 *
 */
class LoginActivity : ComponentActivity() {
    private val loginActivityVM: LoginActivityVM by lazy {
        AppViewModelFactory(this@LoginActivity).create(LoginActivityVM::class.java)
    }
    private val userServiceInterface: UserServiceInterface by lazy {
        UserServiceInterface.authService(this@LoginActivity)
    }
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var isProgressBarLoading: MutableState<Boolean>? = null

    companion object {
        private const val SIGN_IN_REQUEST_CODE = 101
        private const val SIGN_UP_REQUEST_CODE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!AppManager.isLoggedIn()) {
            var envList = EnvironmentListData(arrayListOf())
            loginActivityVM.getEnvironmentList().observe(this) { envData ->
                envList = envData
                envList.environmentList.first()
                    .let { loginActivityVM.configureEnvironment(it) }
            }

            launcher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    when (result.resultCode) {
                        SIGN_IN_REQUEST_CODE -> {
                            val customMessage =
                                result.data?.let { AppManager.authResponseFromIntent(it) }
                            if (customMessage?.status!!.requestStatus) {
                                isProgressBarLoading?.value = true
                                fetchUserProfileData()
                            } else if (customMessage.error != null) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    customMessage.error?.message,
                                    Toast.LENGTH_LONG,
                                ).show()
                            }
                        }

                        SIGN_UP_REQUEST_CODE -> {
                            val customMessage =
                                result.data?.let { AppManager.authResponseFromIntent(it) }
                            /*Toast.makeText(
                                this@LoginActivity,
                                if (customMessage?.status!!.requestStatus) {
                                    "Signed Up successfully"
                                } else {
                                    customMessage.error?.message
                                },
                                Toast.LENGTH_LONG,
                            ).show()*/
                        }
                    }
                }
            setContent {
                isProgressBarLoading =
                    remember {
                        mutableStateOf(false)
                    }
                MaterialTheme {
                    ImageLogoCompose()
                    EnvironmentSpinner(
                        envList,
                        preselected = envList.environmentList.first(),
                        onSelectionChanged = {
                            loginActivityVM.configureEnvironment(it)
                        },
                    )
                    isProgressBarLoading?.value?.let { ProgressBar(loading = it) }
                    SignInAndUpButtonCompose()
                }
            }
        } else {
            launchActivity()
        }
    }

    @Composable
    fun SignInAndUpButtonCompose() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SignInButton {
                launchSignInRequest()
            }
            SignUpButton {
                launchSignUpRequest()
            }
        }
    }

    private fun launchSignInRequest() {
        loginActivityVM.signInRequest(userServiceInterface, SIGN_IN_REQUEST_CODE, launcher)
    }

    private fun launchSignUpRequest() {
        loginActivityVM.signUpRequest(userServiceInterface, SIGN_UP_REQUEST_CODE, launcher)
    }

    private fun launchActivity() {
        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchUserProfileData() {
        loginActivityVM.fetchUserProfileData(userServiceInterface)
            .observe(this@LoginActivity) {
                if (it?.response != null) {
                    isProgressBarLoading?.value = false
                    val response = it.response as UserProfile
                    AppConstants.setUserProfile(this@LoginActivity, Gson().dataToJson(response))
                    launchActivity()
                }
            }
    }
}
