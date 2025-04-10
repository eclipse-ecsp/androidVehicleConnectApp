package com.harman.vehicleconnects.ui.view.composes.vehicleprofilecompose
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
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harman.vehicleconnects.R
import com.harman.vehicleconnects.models.dataclass.VehicleProfileModel
import com.harman.vehicleconnects.models.viewmodels.VehicleProfileVM
import com.harman.vehicleconnects.ui.theme.Black
import com.harman.vehicleconnects.ui.theme.LightBlue
import com.harman.vehicleconnects.ui.theme.LightGray
import com.harman.vehicleconnects.ui.theme.MildGray
import com.harman.vehicleconnects.ui.theme.White
import com.harman.vehicleconnects.ui.view.composes.TextFieldState

/**
 * VehicleNameEditCompose contains Vehicle profile editing screen related compose functions
 *
 */
@Composable
fun Activity.VehicleNameEditMainCompose(
    inputValue: TextFieldState? = remember { TextFieldState() },
    vehicleProfileVM: VehicleProfileVM?,
    vehicleProfileModel: VehicleProfileModel?,
) {
    LaunchedEffect(Unit) {
        vehicleProfileVM?.setTopBarTitle(getString(R.string.edit_nickname_text))
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            modifier =
                Modifier
                    .background(LightGray)
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(30.dp).testTag("vehicle_name_tag"),
            text =
                vehicleProfileModel?.vehicleDetailData?.vehicleAttributes?.name
                    ?: "",
            color = Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        val textFieldPadding = 6.dp
        TextField(
            value = inputValue?.textInput ?: "",
            onValueChange = { inputValue?.textInput = it },
            placeholder = { Text("Nick Name", color = MildGray) },
            colors =
                TextFieldDefaults.colors(
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    focusedContainerColor = White,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp).testTag("nick_name_text_field_tag")
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        SubmitButtonCompose {
            keyboardController?.hide()
            if (inputValue?.textInput != null && vehicleProfileModel != null) {
                vehicleProfileVM?.onSaveBtnClick(inputValue.textInput, vehicleProfileModel)
            } else {
                Toast.makeText(
                    this@VehicleNameEditMainCompose,
                    "Entered name is invalid",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }
}

@Composable
fun Activity.SubmitButtonCompose(onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = LightBlue,
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp).testTag("save_btn_tag"),
        shape = RectangleShape,
    ) {
        Text(getString(R.string.save_text), color = White)
    }
}
