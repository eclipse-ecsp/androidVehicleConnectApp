package com.harman.vehicleconnects.repository

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harman.androidvehicleconnectsdk.helper.response.CustomMessage
import com.harman.androidvehicleconnectsdk.userservice.model.UserProfile
import com.harman.androidvehicleconnectsdk.userservice.service.UserServiceInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
class LoginRepository(val activity: Activity) {
    private val userServiceInterface : UserServiceInterface by lazy {
        UserServiceInterface.authService(activity)
    }

    fun fetchUserProfileData(): MutableLiveData<CustomMessage<UserProfile>> {
        val data =  MutableLiveData<CustomMessage<UserProfile>>()
        val exception = CoroutineExceptionHandler { _, exception ->
            Log.e("User Profile API: ", exception.cause.toString())
        }
        CoroutineScope(Dispatchers.IO).launch(exception) {
            userServiceInterface.fetchUserProfile {
                data.postValue(it)
            }
        }
        return data
    }
}