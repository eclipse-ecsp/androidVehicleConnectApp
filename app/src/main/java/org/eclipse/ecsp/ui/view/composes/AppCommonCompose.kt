package org.eclipse.ecsp.ui.view.composes

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.eclipse.ecsp.R
import org.eclipse.ecsp.ui.theme.Black
import org.eclipse.ecsp.ui.theme.DarkGray
import org.eclipse.ecsp.ui.theme.White

/**
 * AppCommonCompose contains common compose functions
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    topBarTextString: String,
    onClicked: () -> Unit,
) {
    val textFieldPadding = 6.dp
    TopAppBar(
        title = {
            Text(topBarTextString, color = Black, fontWeight = FontWeight.Bold)
        },
        navigationIcon = {
            if (topBarTextString != "Remote Control")
                IconButton(onClick = onClicked) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Back",
                        modifier = Modifier.testTag("top_bar_back_arrow_icon_tag"),
                    )
                }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = White),
        modifier =
            Modifier
                .drawWithContent {
                    drawContent()
                    val strokeWidth = 2.dp.value * density
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        DarkGray,
                        Offset((textFieldPadding).toPx(), y),
                        Offset(size.width - textFieldPadding.toPx(), y),
                    )
                }
                .testTag("topBar_tag"),
    )
}

class TextFieldState {
    var textInput: String by mutableStateOf("")
}
