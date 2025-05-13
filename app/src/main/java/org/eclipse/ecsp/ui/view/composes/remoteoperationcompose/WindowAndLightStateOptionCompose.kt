package org.eclipse.ecsp.ui.view.composes.remoteoperationcompose
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eclipse.ecsp.R
import org.eclipse.ecsp.helper.AppConstants
import org.eclipse.ecsp.helper.AppConstants.PARTIAL_OPENED
import org.eclipse.ecsp.ui.theme.Black
import org.eclipse.ecsp.ui.theme.LightBlue

/**
 * WindowsAndLightStateOptionCompose contains RO's Windows and Light state related compose functions
 *
 */
@Composable
fun WindowsAndLightStateCompose(
    roType: String,
    selectedState: MutableState<Pair<String, Int>>,
    thirdItemColor: MutableState<Boolean>,
    secondItemColor: MutableState<Boolean>,
    firstItemColor: MutableState<Boolean>,
) {
    val firstText = if (roType == AppConstants.WINDOWS) AppConstants.CLOSE else AppConstants.OFF
    val secText = if (roType == AppConstants.WINDOWS) AppConstants.AJAR else AppConstants.FLASH
    val thirdText = if (roType == AppConstants.WINDOWS) AppConstants.OPEN else AppConstants.ON

    Row(
        modifier =
            Modifier
                .height(300.dp)
                .fillMaxWidth()
                .padding(top = 20.dp, start = 10.dp, end = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        FirstItem(
            roType = roType,
            title = firstText,
            selectedState = selectedState,
            thirdItemColor = thirdItemColor,
            secondItemColor = secondItemColor,
            firstItemColor = firstItemColor,
        )
        if (roType == AppConstants.WINDOWS) {
            SecondItem(
                roType = roType,
                title = secText,
                selectedState = selectedState,
                thirdItemColor = thirdItemColor,
                secondItemColor = secondItemColor,
                firstItemColor = firstItemColor,
            )
        }
        ThirdItem(
            roType = roType,
            title = thirdText,
            selectedState = selectedState,
            thirdItemColor = thirdItemColor,
            secondItemColor = secondItemColor,
            firstItemColor = firstItemColor,
        )
    }
}

@Composable
fun FirstItem(
    roType: String,
    title: String,
    selectedState: MutableState<Pair<String, Int>>,
    thirdItemColor: MutableState<Boolean>,
    secondItemColor: MutableState<Boolean>,
    firstItemColor: MutableState<Boolean>,
) {
    val status = if (roType == AppConstants.WINDOWS) AppConstants.CLOSED else AppConstants.OFF
    val icon =
        if (roType == AppConstants.WINDOWS) R.drawable.ic_windows_closed else R.drawable.ic_lights_off
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .wrapContentSize()
                .clickable {
                    firstItemColor.value = true
                    secondItemColor.value = false
                    thirdItemColor.value = false
                    selectedState.value = Pair(status, icon)
                }.testTag("first_item_click_action_tag"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val color =
            if (selectedState.value.first == status || firstItemColor.value) LightBlue else Black
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            tint = color,
        )
        Text(
            text = title,
            color = color,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 10.dp).testTag("first_item_text_tag"),
        )
    }
}

@Composable
fun SecondItem(
    roType: String,
    title: String,
    selectedState: MutableState<Pair<String, Int>>,
    thirdItemColor: MutableState<Boolean>,
    secondItemColor: MutableState<Boolean>,
    firstItemColor: MutableState<Boolean>,
) {
    val status = if (roType == AppConstants.WINDOWS) AppConstants.AJAR else AppConstants.FLASHING
    val icon =
        if (roType == AppConstants.WINDOWS) R.drawable.ic_windows_ajar else R.drawable.ic_flash_lights
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .wrapContentSize()
                .clickable {
                    firstItemColor.value = false
                    secondItemColor.value = true
                    thirdItemColor.value = false
                    selectedState.value = Pair(status, icon)
                }.testTag("second_item_click_action_tag"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val color =
            if (selectedState.value.first == status || secondItemColor.value) {
                LightBlue
            } else if (selectedState.value.first.lowercase() == PARTIAL_OPENED.lowercase()) {
                LightBlue
            } else {
                Black
            }
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            tint = color,
        )
        Text(
            text = title,
            color = color,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 10.dp).testTag("first_item_title_tag"),
        )
    }
}

@Composable
fun ThirdItem(
    roType: String,
    title: String,
    selectedState: MutableState<Pair<String, Int>>,
    thirdItemColor: MutableState<Boolean>,
    secondItemColor: MutableState<Boolean>,
    firstItemColor: MutableState<Boolean>,
) {
    val status = if (roType == AppConstants.WINDOWS) AppConstants.OPENED else AppConstants.ON
    val icon =
        if (roType == AppConstants.WINDOWS) R.drawable.ic_windows_open else R.drawable.ic_lights_on
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .wrapContentSize()
                .clickable {
                    firstItemColor.value = false
                    secondItemColor.value = false
                    thirdItemColor.value = true
                    selectedState.value = Pair(status, icon)
                }.testTag("third_item_click_action_tag"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val color =
            if (selectedState.value.first == status || thirdItemColor.value) LightBlue else Black
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            tint = color,
        )
        Text(
            text = title,
            color = color,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 10.dp).testTag("third_item_title_tag"),
        )
    }
}
