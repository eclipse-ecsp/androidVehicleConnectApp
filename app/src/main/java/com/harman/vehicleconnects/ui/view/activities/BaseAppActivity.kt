package com.harman.vehicleconnects.ui.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.harman.androidvehicleconnectsdk.helper.AppManager
import com.harman.vehicleconnects.helper.AppConstants
import com.harman.vehicleconnects.helper.toastError
import com.harman.vehicleconnects.services.ConnectionManager


/**
 * Copyright (c) 2023-24 Harman International
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
open class BaseAppActivity : ComponentActivity(), ConnectionManager.ConnectionListener {

    private val connectivityManager : ConnectionManager by lazy {
        ConnectionManager(this@BaseAppActivity, this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityManager.subscribe()
        AppManager.isRefreshTokenFailed().observe(this) { isRefreshTokenFailed ->
            if (isRefreshTokenFailed) {
                toastError(this@BaseAppActivity, "Your session expired, please login again.")
                AppConstants.removeAll(this@BaseAppActivity)
                val intent = Intent(this@BaseAppActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unsubscribe()
    }

    override fun onLost() {
        Log.d("Network Status", "Connection lost")
//        toastError(this@BaseAppActivity, "No Internet connectivity")
    }

    override fun onAvailable() {
        Log.d("Network Status", "Connection available")
//        toastError(this@BaseAppActivity, "Internet connectivity Available")
    }

}