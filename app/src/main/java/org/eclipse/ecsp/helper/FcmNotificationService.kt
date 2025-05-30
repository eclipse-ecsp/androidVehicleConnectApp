package org.eclipse.ecsp.helper
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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.eclipse.ecsp.R
import org.eclipse.ecsp.helper.AppConstants.EXTRA_ALERT
import org.eclipse.ecsp.helper.AppConstants.EXTRA_MESSAGE
import org.eclipse.ecsp.helper.AppConstants.EXTRA_VEHICLE_ID
import org.eclipse.ecsp.notificationservice.model.AlertData
import org.eclipse.ecsp.ui.theme.LightBlue
import org.eclipse.ecsp.ui.view.activities.DashboardActivity
import org.eclipse.ecsp.ui.view.activities.LoginActivity

/**
 * Represents the notification service listener, using firebase messaging service.
 * All push notifications receives here
 *
 */
class FcmNotificationService : FirebaseMessagingService() {
    companion object {
        private const val CHANNEL_NAME = "General"
    }

    /**
     * represents the message receiver callback
     *
     * @param message provide the [RemoteMessage] class, which contains the
     */
    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.isNotEmpty()) {
            val data = message.data
            Log.d("FCM_SERVICE", data.toString())
            val body: String = data["body"].toString()
            val chanelId =
                if (data.keys.contains("channelIdentifier"))
                    data["channelIdentifier"]  ?: ""
                else
                    ""
            showNotification(chanelId, body, )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "Refreshed token: $token")
    }

    /**
     * Function is used to generate the push notification using notification manager
     *
     * @param channelId used to  notification compat builder
     * @param message which is the body need to display on the notification
     * @param vehicleId is the vehicle unique id
     * @param alert is the Alert data related to vehicle id
     */
    private fun showNotification(
        channelId: String,
        message: String,
//        vehicleId: String,
//        alert: AlertData
    ) {
        val notificationId = (System.currentTimeMillis() and 0xfffffffL).toInt()
        val intent = getPendingIntent(message)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                notificationId,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
            )
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(
                applicationContext,
                channelId,
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setVibrate(
                    longArrayOf(
                        1000,
                        1000,
                        1000,
                        1000,
                        1000,
                    ),
                )
                .setOnlyAlertOnce(true)
                .setContentTitle("Vehicle Connect")
                .setContentText(message)
                .setColor(LightBlue.toArgb())
                .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(
                NOTIFICATION_SERVICE,
            ) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    channelId,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                )
            notificationManager.createNotificationChannel(
                notificationChannel,
            )
        }
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     *Function to create the pending intent related to the notification
     *
     * @param message which is the body need to display on the notification
     * @param alert is the Alert data related to vehicle id
     * @param vehicleId is the vehicle unique id
     * @return [Intent] which is created based on the params
     */
    private fun getPendingIntent(
        message: String,
//        alert: AlertData,
        vehicleId: String?=null
    ): Intent {
        val intent: Intent
        if (AppManager.isLoggedIn()) {
            intent = Intent(applicationContext, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        } else {
            intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra(EXTRA_MESSAGE, message)
//        intent.putExtra(EXTRA_ALERT, alert)
        intent.putExtra(EXTRA_VEHICLE_ID, vehicleId)
        intent.action = System.currentTimeMillis().toString()
        return intent
    }
}
