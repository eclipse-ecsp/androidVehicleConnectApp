package com.harman.vehicleconnects.models.viewmodels

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
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harman.androidvehicleconnectsdk.environment.Environment
import com.harman.androidvehicleconnectsdk.environment.EnvironmentManager
import com.harman.androidvehicleconnectsdk.helper.response.CustomMessage
import com.harman.androidvehicleconnectsdk.userservice.model.UserProfile
import com.harman.androidvehicleconnectsdk.userservice.service.UserServiceInterface
import com.harman.vehicleconnects.models.dataclass.EnvironmentListData
import com.harman.vehicleconnects.repository.LoginRepository
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

/**
 * Class represents the Login Activity screen ViewModel
 *
 * @constructor
 *
 * @param activity of Application lifecycle
 */
class LoginActivityVM(activity: Activity) : AndroidViewModel(activity.application) {
    private var weakReference = WeakReference(activity)
    private var environmentList: MutableLiveData<EnvironmentListData>? = null

    /**
     * Function is to do SIGN IN request
     *
     * @param userServiceInterface SDK interface to call SIGN IN API
     * @param requestCode Activity result request code
     * @param launcher [ActivityResultLauncher] reference object
     */
    fun signInRequest(
        userServiceInterface: UserServiceInterface,
        requestCode: Int,
        launcher: ActivityResultLauncher<Intent>?,
    ) {
        userServiceInterface.signInWithAppAuth(requestCode, launcher!!)
    }

    /**
     *
     *
     * @param userServiceInterface
     * @param requestCode
     * @param launcher
     */
    fun signUpRequest(
        userServiceInterface: UserServiceInterface,
        requestCode: Int,
        launcher: ActivityResultLauncher<Intent>?,
    ) {
        userServiceInterface.signUpWithAppAuth(requestCode, launcher!!)
    }

    /**
     * Function is to get the list of environment
     *
     * @return the [MutableLiveData] of [EnvironmentListData]
     */
    fun getEnvironmentList(): MutableLiveData<EnvironmentListData> {
        return environmentList ?: readFileAndConvertToEnvironment()
    }

    /**
     * Function is to read and format the data from [environment_file.json] file to get [MutableLiveData] of [EnvironmentListData]
     *
     * @return [MutableLiveData] of [EnvironmentListData]
     */
    private fun readFileAndConvertToEnvironment(): MutableLiveData<EnvironmentListData> {
        runBlocking {
            environmentList =
                try {
                    val fileInString: String? =
                        weakReference.get()?.let {
                            it.assets.open("environment_file.json")
                                .bufferedReader().use { br ->
                                    br.readText()
                                }
                        }
                    MutableLiveData(
                        convertJsonStringToData(fileInString),
                    )
                } catch (e: Exception) {
                    MutableLiveData(convertJsonStringToData(Gson().toJson(arrayListOf<Environment>())))
                }
        }
        return environmentList as MutableLiveData<EnvironmentListData>
    }

    private fun convertJsonStringToData(envString: String?): EnvironmentListData {
        val environmentListData: ArrayList<Environment>
        val tokenType = object : TypeToken<ArrayList<Environment>>() {}.type
        environmentListData = Gson().fromJson(envString, tokenType)
        return EnvironmentListData(environmentListData)
    }

    /**
     * Function is to configure the environment details
     *
     * @param environment is Data class holds the environment details
     */
    fun configureEnvironment(environment: Environment) = EnvironmentManager.configure(environment)

    /**
     * Represents to get the user profile data using SDK API
     *
     * @param activity Application activity obejct
     * @return [MutableLiveData] of [CustomMessage]
     */
    fun fetchUserProfileData(activity: Activity): MutableLiveData<CustomMessage<UserProfile>> {
        return LoginRepository(activity).fetchUserProfileData()
    }
}
