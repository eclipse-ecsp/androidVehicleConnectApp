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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eclipse.ecsp.helper.AppConstants.AJAR
import org.eclipse.ecsp.helper.AppConstants.CLOSED
import org.eclipse.ecsp.helper.AppConstants.LOCKED
import org.eclipse.ecsp.helper.AppConstants.OFF
import org.eclipse.ecsp.helper.AppConstants.ON
import org.eclipse.ecsp.helper.AppConstants.OPENED
import org.eclipse.ecsp.helper.AppConstants.PARTIAL_OPENED
import org.eclipse.ecsp.helper.AppConstants.PLEASE_WAIT
import org.eclipse.ecsp.helper.AppConstants.STARTED
import org.eclipse.ecsp.helper.AppConstants.STOPPED
import org.eclipse.ecsp.helper.AppConstants.UNLOCKED
import org.eclipse.ecsp.models.dataclass.RemoteOperationItem
import org.eclipse.ecsp.ui.theme.Black
import org.eclipse.ecsp.ui.theme.LightBlue
import org.eclipse.ecsp.ui.theme.MildGray

/**
 * RemoteOperationGridViewCompose file contains RO grid view related compose functions
 *
 */
@Composable
fun RemoteOperationGridViewCompose(
    isEnabled: Boolean,
    itemList: ArrayList<RemoteOperationItem>?,
    onClickedItem: (RemoteOperationItem) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        Modifier
            .padding(10.dp)
            .alpha(if (isEnabled) 1f else 0.5f)
            .testTag("lazy_grid_view_tag"),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (itemList != null) {
            items(itemList) { item ->
                val color =
                    when (item.statusText.lowercase()) {
                        OFF.lowercase(), CLOSED.lowercase(), LOCKED.lowercase(), STOPPED.lowercase() -> Black
                        ON.lowercase(), OPENED.lowercase(), UNLOCKED.lowercase(), PARTIAL_OPENED.lowercase(),
                        AJAR.lowercase(), PLEASE_WAIT.lowercase(), STARTED.lowercase(),
                        -> LightBlue

                        else -> Black
                    }
                Column(
                    modifier =
                        Modifier
                            .border(2.dp, if (color == Black) MildGray else LightBlue)
                            .padding(10.dp)
                            .wrapContentSize()
                            .testTag("grid_item_click_tag")
                            .clickable {
                                if (isEnabled && item.statusText.lowercase() != PLEASE_WAIT.lowercase()) {
                                    onClickedItem(item)
                                }
                            },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    GridItemCompose(color, remoteOperationItem = item)
                }
            }
        }
    }
}

@Composable
fun GridItemCompose(
    color: Color,
    remoteOperationItem: RemoteOperationItem,
) {
    Icon(
        imageVector = ImageVector.vectorResource(remoteOperationItem.imageId),
        contentDescription = null,
        tint = color,
        modifier = Modifier.testTag("remote_icon_tag"),
    )
    Text(
        text =
            if (remoteOperationItem.statusText.lowercase() == PARTIAL_OPENED.lowercase()) {
                AJAR
            } else {
                remoteOperationItem.statusText
            },
        color = color,
        fontSize = 15.sp,
        modifier = Modifier.testTag("remote_item_status_text_tag"),
    )
    Text(
        text = remoteOperationItem.itemName,
        fontSize = 13.sp,
        color = color,
        modifier = Modifier.testTag("remote_item__text_tag"),
    )
}

@Composable
fun ShowAlertDialog(
    title: String,
    message: String? = null,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.testTag("alert_dialog_tag"),
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = title)
        },
        text = {
            if (message != null) {
                Text(text = message)
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.testTag("confirm_btn_tag"),
                onClick = {
                    onConfirmClick()
                },
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                modifier = Modifier.testTag("dismiss_btn_tag"),
                onClick = {
                    onDismiss()
                },
            ) {
                Text("Cancel")
            }
        },
    )
}
