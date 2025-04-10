package com.harman.vehicleconnects.services
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
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.harman.vehicleconnects.services.ConnectionManager.ConnectionListener

/**
 * ConnectionManager class is used to get the network connectivity information
 *
 * @property connectionListener [ConnectionListener] is interface
 * @constructor
 *
 * @param context application context
 */
class ConnectionManager(context: Context, val connectionListener: ConnectionListener) {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                connectionListener.onAvailable()
                // indicates that the device is connected to a new network that satisfies the capabilities
                // and transport type requirements specified in the NetworkRequest
            }

            override fun onLost(network: Network) {
                connectionListener.onLost()
                // indicates that the device has lost connection to the network.
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                // indicates that the capabilities of the network have changed.
            }
        }

    fun subscribe() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        /*
        or:

        val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
         */
    }

    fun unsubscribe() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    interface ConnectionListener {
        fun onLost()

        fun onAvailable()
    }
}
