package com.harman.vehicleconnects.ui.view.composes.deviceinstallationcompose
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.harman.vehicleconnects.R
import com.harman.vehicleconnects.ui.theme.Black
import com.harman.vehicleconnects.ui.theme.DarkGray
import com.harman.vehicleconnects.ui.theme.LightBlue
import com.harman.vehicleconnects.ui.theme.MildGray
import com.harman.vehicleconnects.ui.theme.White
import com.harman.vehicleconnects.ui.view.composes.TextFieldState

/**
 * EnterIMEICompose file contains all the compose function related to IMEI entering screen
 *
 */
@Composable
fun Activity.TextFieldCompose(inputValue: TextFieldState = remember { TextFieldState() }) {
    Text(
        text = getString(R.string.enter_imei_sub_text),
        color = DarkGray,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
        textAlign = TextAlign.Center,
    )
    val textFieldPadding = 6.dp
    TextField(
        value = inputValue.textInput,
        onValueChange = { if (it.isDigitsOnly()) inputValue.textInput = it },
        placeholder = { Text("IMEI", color = MildGray) },
        colors =
            TextFieldDefaults.colors(
                focusedTextColor = Black,
                unfocusedTextColor = Black,
                focusedContainerColor = White,
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(20.dp).testTag("imei_edit_text_tag")
                .drawWithContent {
                    drawContent()
                    val strokeWidth = 1.dp.value * density
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        LightBlue,
                        Offset((textFieldPadding).toPx(), y),
                        Offset(size.width - textFieldPadding.toPx(), y),
                    )
                },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@Composable
fun EnterIMEIDotImageCompose() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 50.dp, bottom = 5.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.unfilled_dot_img),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .padding(5.dp),
            )
            Image(
                painter = painterResource(R.drawable.filled_dot_img),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .padding(5.dp),
            )
        }
    }
}

@Composable
fun Activity.EnterIMEINextButtonCompose(onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = LightBlue,
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(50.dp).testTag("add_device_btn_tag"),
        shape = RectangleShape,
    ) {
        Text(getString(R.string.add_device_btn_text), color = White)
    }
}

@Composable
fun ProgressBar(loading: Boolean) {
    if (!loading) return
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .width(55.dp)
                        .wrapContentSize(Alignment.Center).testTag("progress_bar_tag"),
                color = LightBlue,
            )
        }
    }
}
