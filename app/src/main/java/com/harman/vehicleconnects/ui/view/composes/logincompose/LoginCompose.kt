package com.harman.vehicleconnects.ui.view.composes.logincompose
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harman.androidvehicleconnectsdk.environment.Environment
import com.harman.vehicleconnects.R
import com.harman.vehicleconnects.models.dataclass.EnvironmentListData
import com.harman.vehicleconnects.ui.theme.LightBlue
import com.harman.vehicleconnects.ui.theme.White

/**
 * LoginCompose contains all the login screen related compose functions
 *
 */
@Composable
fun EnvironmentSpinner(
    list: EnvironmentListData,
    preselected: Environment,
    onSelectionChanged: (selection: Environment) -> Unit,
) {
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) }

    Box {
        Column(modifier = Modifier.testTag("environmentDropDown")) {
            OutlinedTextField(
                value = (selected.toString()),
                onValueChange = { },
                label = { Text(text = "Environment", color = Color.Blue) },
                modifier =
                    Modifier
                        .width(200.dp)
                        .padding(16.dp)
                        .testTag("OutlineTextDropDown"),
                //                trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                readOnly = true,
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    unfocusedBorderColor = Color.Blue
//                )
            )
            DropdownMenu(
                modifier =
                    Modifier
                        .width(200.dp)
                        .testTag("EnvironmentDropdownMenu"),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                list.environmentList.forEach { entry ->
                    DropdownMenuItem(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .testTag("EnvironmentDropdownMenuItem"),
                        onClick = {
                            selected = entry
                            expanded = false
                            onSelectionChanged(entry)
                        },
                        text = {
                            Text(
                                text = (entry.toString()),
                                color = Color.Blue,
                                modifier =
                                    Modifier
                                        .wrapContentWidth()
                                        .align(Alignment.Start)
                                        .testTag("env_text_tag"),
                            )
                        },
                    )
                }
            }
        }
        Spacer(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .padding(10.dp),
                /*.clickable(
                    onClick = { expanded = !expanded }
                )*/
        )
    }
}

@Composable
fun Activity.SignInButton(onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = LightBlue,
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag("SignInTextButton").testTag("btn_sign_in"),
        shape = RectangleShape,
    ) {
        Text(getString(R.string.sign_in_text), color = White)
    }
}

@Composable
fun Activity.SignUpButton(onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = LightBlue,
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag("SignUpTextButton"),
        shape = RectangleShape,
    ) {
        Text(getString(R.string.sign_up_text), color = White)
    }
}

@Composable
fun ImageLogoCompose()  {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val testTag = R.drawable.ic_vehicle_connect_logo.toString()
        Image(
            modifier = Modifier.testTag(testTag).fillMaxWidth(),
            painter = painterResource(R.drawable.ic_vehicle_connect_logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}
