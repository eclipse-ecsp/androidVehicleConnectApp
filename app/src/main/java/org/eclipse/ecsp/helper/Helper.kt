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
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * This function is used to convert the generic data class object to json string
 *
 * @param T Generic Data class Reference
 * @param data  Generic data class object
 * @return Json String value of converted data class
 */
internal inline fun <reified T> Gson.dataToJson(data: T): String = toJson(data)

/**
 * This function is used to convert json string to Respective data class
 *
 * @param T Generic data class
 * @param json json String as input
 * @return Respective data class object
 */
internal inline fun <reified T> Gson.fromJson(json: String): T = fromJson<T>(json, object : TypeToken<T>() {}.type)

/**
 * This function is to toast the message
 *
 * @param message error information to toast
 */
fun toastError(
    context: Context,
    message: String,
) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

/**
 * Function represents to convert the ISO time to milliseconds
 *
 * @param isoTime string value comes in ISO format
 * @return [Long] time in milli seconds
 */
fun convertISO8601TimeToMillis(isoTime: String?): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.getDefault())
    val tempConversion = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        val parsedDate = isoTime?.let { dateFormat.parse(it) }
        val tempDate = parsedDate?.let { tempConversion.format(it) }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = tempDate?.let { sdf.parse(it) }
        return date?.time ?: -1
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return -1
}

/**
 * Generic function which is used to start an activity
 *
 * @param context application context
 * @param classes Activity which we need to invoke
 */
fun launchActivity(
    context: Context,
    classes: Class<*>,
)  {
    val intent = Intent(context, classes)
    context.startActivity(intent)
}

/**
 * Function is to parse the value which is bundled with Intent
 *
 * @param T Generic class type
 * @param key key value used in Intent KEY-VALUE pair
 * @return [T] generic class type
 * */
inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? =
    when {
        SDK_INT >= 33 -> {
            if (hasExtra(key)) {
                getParcelableExtra(key, T::class.java)
            } else {
                null
            }
        }
        else ->
            @Suppress("DEPRECATION")
            getParcelableExtra(key)
                as? T
    }

/**
 * Function used to check if the RO request is pending or not
 *
 * @param timeStamp timestamp value in [Long]
 * @return Compared value in [Boolean]
 */
fun isRequestPendingLong(timeStamp: Long): Boolean {
    val startDate = Date(timeStamp)
    val endDate = Date()
    val difference = endDate.time - startDate.time
    val sec = TimeUnit.MILLISECONDS.toSeconds(difference)
    return sec > 180
}

/**
 * Function is to check the internet connectivity
 *
 * @param context application context
 * @return result in [Boolean] value
 */
fun isInternetAvailable(context: Context): Boolean {
    val result: Boolean
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result =
        when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

    return result
}
