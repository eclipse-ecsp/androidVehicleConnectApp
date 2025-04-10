package com.harman.vehicleconnects.ui.view.composes.deviceinstallationcompose

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.harman.vehicleconnects.R
import com.harman.vehicleconnects.helper.AppConstants
import com.harman.vehicleconnects.models.viewmodels.DeviceAssociationVM
import com.harman.vehicleconnects.ui.view.composes.TextFieldState
import com.harman.vehicleconnects.ui.theme.Black
import com.harman.vehicleconnects.ui.theme.DarkGray
import com.harman.vehicleconnects.ui.theme.LightBlue
import com.harman.vehicleconnects.ui.theme.LightGray
import com.harman.vehicleconnects.ui.theme.White

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

@Composable
fun Activity.InstallDeviceMainScreenCompose(
    content: PaddingValues,
    navController: NavController,
    deviceAssociationVM: DeviceAssociationVM?
) {
    LaunchedEffect(Unit) {
        deviceAssociationVM?.setTopBarTitle(getString(R.string.device_association_text))
    }
    Column(
        modifier = Modifier
            .padding(content)
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InstallDeviceImageTextCompose()

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FindObdPortCompose()
                NextButtonCompose {
                    //do click action
                    navController.navigate(AppConstants.ENTER_IMEI) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

@Composable
fun Activity.InstallDeviceImageTextCompose() {
    Image(
        painter = painterResource(id = R.drawable.obd_port_img),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    Text(
        text = getString(R.string.install_device_text), color = Black,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        textAlign = TextAlign.Center
    )
    Text(
        text = getString(R.string.install_device_sub_text), color = DarkGray,
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(10.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun DotImageCompose() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.filled_dot_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(5.dp)
        )
        Image(
            painter = painterResource(R.drawable.unfilled_dot_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(5.dp)
        )
    }
}

@Composable
fun Activity.EnterImeiScreenCompose(content: PaddingValues, deviceAssociationVM: DeviceAssociationVM? ) {
    LaunchedEffect(Unit) {
        deviceAssociationVM?.setTopBarTitle(getString(R.string.enter_imei_text))
    }
    val loading = deviceAssociationVM?.getLoadingStatus()?.observeAsState()
    Column(
        modifier = Modifier
            .padding(content)
            .background(White)
    ) {
        val inputValue = remember { TextFieldState() }
        TextFieldCompose(inputValue)
        Divider(
            color = LightGray, thickness = 2.dp, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        loading?.value?.let { ProgressBar(it) }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EnterIMEINextButtonCompose {
                if (inputValue.textInput.isNotEmpty()) {
                    deviceAssociationVM?.setLoadingStatus(true)
                    deviceAssociationVM?.triggerImeiVerification(inputValue.textInput)
                } else {
                    Toast.makeText(
                        this@EnterImeiScreenCompose, "Enter IMEI number",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

@Composable
fun Activity.NextButtonCompose(onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = LightBlue
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp).testTag("next_btn_tag"),
        shape = RectangleShape,
    ) {
        Text(getString(R.string.next_btn_text), color = White)
    }
}

@Composable
fun Activity.FindObdPortCompose() {
    Text(
        text = getString(R.string.find_obdii_port_text), color = LightBlue,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp).testTag("help_me_obdii_port_tag")
            .clickable {
                /*startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("")
                    )
                )*/
            },
        textAlign = TextAlign.Center
    )
}
