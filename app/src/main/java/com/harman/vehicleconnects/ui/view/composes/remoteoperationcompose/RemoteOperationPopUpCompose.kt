package com.harman.vehicleconnects.ui.view.composes.remoteoperationcompose
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harman.vehicleconnects.ui.theme.DarkGray
import com.harman.vehicleconnects.ui.theme.LightBlue
import com.harman.vehicleconnects.ui.theme.White

/**
 * RemoteOperationPopUpCompose contains RO pop compose functions
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetOptionsRoCompose(
    titleText: String,
    selectedState: String,
    onDismiss: () -> Unit,
    onApply: (Triple<String, String, Int>) -> Unit,
) {
    val selectedMutableState =
        remember {
            mutableStateOf(Pair(selectedState, 0))
        }
    val modalBottomSheetState = rememberModalBottomSheetState()
    val firstItemColor =
        remember {
            mutableStateOf(false)
        }
    val thirdItemColor =
        remember {
            mutableStateOf(false)
        }
    val secondItemColor =
        remember {
            mutableStateOf(false)
        }
    LaunchedEffect(key1 = Unit) {
        modalBottomSheetState.expand()
    }
    if (modalBottomSheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = modalBottomSheetState,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(White),
            ) {
                Row(
                    modifier =
                        Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .drawWithContent {
                                drawContent()
                                val strokeWidth = 2.dp.value * density
                                val y = size.height - strokeWidth / 2
                                drawLine(
                                    DarkGray,
                                    Offset((6.dp).toPx(), y),
                                    Offset(size.width - 6.dp.toPx(), y),
                                )
                            },
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = titleText,
                        color = DarkGray,
                        fontWeight = FontWeight.Bold,
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .wrapContentWidth()
                                .padding(15.dp).testTag("bottom_sheet_title_text_tag"),
                        textAlign = TextAlign.Start,
                        fontSize = 18.sp,
                    )
                    Text(
                        text = "APPLY",
                        color =
                            if (firstItemColor.value ||
                                secondItemColor.value ||
                                thirdItemColor.value
                            ) {
                                LightBlue
                            } else {
                                DarkGray
                            },
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .wrapContentWidth()
                                .padding(15.dp).testTag("apply_btn_tag")
                                .clickable {
                                    onApply(
                                        Triple(
                                            titleText,
                                            selectedMutableState.value.first,
                                            selectedMutableState.value.second,
                                        ),
                                    )
                                },
                        textAlign = TextAlign.End,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                WindowsAndLightStateCompose(
                    roType = titleText,
                    selectedState = selectedMutableState,
                    thirdItemColor = thirdItemColor,
                    secondItemColor = secondItemColor,
                    firstItemColor = firstItemColor,
                )
            }
        }
    }
}
