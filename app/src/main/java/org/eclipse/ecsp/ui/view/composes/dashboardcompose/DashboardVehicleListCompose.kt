package org.eclipse.ecsp.ui.view.composes.dashboardcompose
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eclipse.ecsp.R
import org.eclipse.ecsp.models.dataclass.VehicleProfileModel
import org.eclipse.ecsp.ui.theme.Black
import org.eclipse.ecsp.ui.theme.LightBlue
import org.eclipse.ecsp.ui.theme.MildWhite
import org.eclipse.ecsp.ui.theme.White

/**
 * DashboardVehicleListCompose file contains compose functions, which is used to handle the dashboard
 *
 */

@Composable
fun VehicleSelectionListView(
    vehicleList: ArrayList<VehicleProfileModel?>,
    selectedVehicleId: String,
    selectedVehicleName: String,
    onVehicleSelection: (VehicleProfileModel?, Int) -> Unit,
    onAddNewClick: () -> Unit,
) {
    val isSelectViewVisible = remember { mutableStateOf(vehicleList.isEmpty()) }
    val isSelectedViewVisible = remember { mutableStateOf(vehicleList.isNotEmpty()) }
    val isListViewVisible = remember { mutableStateOf(vehicleList.isEmpty()) }
    val selectedItem =
        remember {
            mutableStateOf(selectedVehicleId)
        }
    val selectedItemName =
        remember {
            mutableStateOf(selectedVehicleName)
        }
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(White)
                .padding(bottom = 10.dp, start = 5.dp),
    ) {
        if (isSelectViewVisible.value) {
            Card(
                modifier = Modifier.testTag("vehicle_card_view_tag"),
                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 6.dp,
                    ),
                shape = RectangleShape,
            ) {
                Row(
                    modifier =
                        Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .background(MildWhite),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxHeight()
                                    .wrapContentWidth()
                                    .padding(20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_cross),
                                contentDescription = null,
                                modifier =
                                    Modifier
                                        .clickable {
                                            isSelectViewVisible.value = false
                                            isSelectedViewVisible.value = true
                                            isListViewVisible.value = false
                                        }
                                        .padding(start = 15.dp, end = 20.dp).testTag("close_icon_tag"),
                            )
                            Text(
                                text = "Select Vehicle",
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp).testTag("select_vehicle_text_tag"),
                            )
                        }
                    }

                    Text(
                        text = "ADD NEW", color = LightBlue,
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentWidth()
                            .padding(20.dp)
                            .clickable {
                                onAddNewClick()
                            },
                    )
                }
            }
        }

        if (isSelectedViewVisible.value) {
            Card(
                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 6.dp,
                    ),
                shape = RectangleShape,
            ) {
                Row(
                    modifier =
                        Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .clickable {
                                isSelectViewVisible.value = true
                                isSelectedViewVisible.value = false
                                isListViewVisible.value = true
                            }
                            .background(MildWhite).testTag("vehicle_click_tag"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = selectedItemName.value,
                        color = Black,
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .wrapContentWidth()
                                .padding(20.dp).testTag("vehicle_text_tag"),
                        textAlign = TextAlign.Start,
                    )

                    Icon(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .wrapContentWidth()
                                .padding(20.dp).testTag("vehicle_group_down_arrow_icon_tag"),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_down_arrow),
                        contentDescription = null,
                    )
                }
            }
        }
        if (isListViewVisible.value) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                itemsIndexed(vehicleList) { index, item ->
                    Card(
                        modifier =
                            Modifier
                                .padding(8.dp).testTag("vehicle_list_item_card_view_tag")
                                .clickable {
                                    isSelectViewVisible.value = false
                                    isSelectedViewVisible.value = true
                                    isListViewVisible.value = false
                                    selectedItemName.value =
                                        item?.vehicleDetailData?.vehicleAttributes?.name
                                            ?: item?.associatedDevice?.mDeviceId.toString()
                                    selectedItem.value = item?.associatedDevice?.mDeviceId.toString()
                                    onVehicleSelection(item, index)
                                }
                                .background(MildWhite)
                                .fillMaxWidth()
                                .height(50.dp),
                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation = 6.dp,
                            ),
                    ) {
                        Text(
                            text = (
                                item?.vehicleDetailData?.vehicleAttributes?.name
                                    ?: "No Device Id"
                            ),
                            modifier =
                                Modifier
                                    .padding(start = 4.dp, top = 15.dp, end = 4.dp)
                                    .fillMaxWidth()
                                    .height(30.dp).testTag("vehicle_list_item_text_tag"),
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
