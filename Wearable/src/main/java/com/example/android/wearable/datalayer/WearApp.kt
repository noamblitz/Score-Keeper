/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wearable.datalayer

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip

@Composable
fun WearApp(mainViewModel: MainViewModel) {
    AppScaffold {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(navController = navController, startDestination = "main") {
            composable("main") {
                MainScreen(
                    onShowNodesList = { navController.navigate("nodeslist") },
                    onShowCameraNodesList = { navController.navigate("cameraNodeslist") },
                    mainViewModel = mainViewModel
                )
            }
            composable("nodeslist") {
                ConnectedNodesScreen()
            }
            composable("cameraNodeslist") {
                CameraNodesScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    onShowNodesList: () -> Unit,
    onShowCameraNodesList: () -> Unit,
    mainViewModel: MainViewModel
) {
    MainScreen(
        mainViewModel.image,
        mainViewModel.leftScore,
        mainViewModel.rightScore,
        onShowNodesList,
        onShowCameraNodesList,
        onLeftScoreClick = { mainViewModel.incrementLeftScore() },
        onRightScoreClick = { mainViewModel.incrementRightScore() }
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MainScreen(
    image: Bitmap?,
    leftScore: Int,
    rightScore: Int,
    onShowNodesList: () -> Unit,
    onShowCameraNodesList: () -> Unit,
    onLeftScoreClick: () -> Unit,
    onRightScoreClick: () -> Unit
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Chip,
            last = ItemType.Text
        )
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = Modifier
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = leftScore.toString(),
                        style = MaterialTheme.typography.display2,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onLeftScoreClick),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = rightScore.toString(),
                        style = MaterialTheme.typography.display2,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onRightScoreClick),
                        textAlign = TextAlign.Center
                    )
                }
            }
            item {
                Chip(
                    label = stringResource(id = R.string.query_other_devices),
                    onClick = onShowNodesList
                )
            }
            item {
                Chip(
                    label = stringResource(id = R.string.query_mobile_camera),
                    onClick = onShowCameraNodesList
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(32.dp)
                ) {
                    if (image == null) {
                        Image(
                            painterResource(id = R.drawable.photo_placeholder),
                            contentDescription = stringResource(
                                id = R.string.photo_placeholder
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            image.asImageBitmap(),
                            contentDescription = stringResource(
                                id = R.string.captured_photo
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainScreenPreviewEvents() {
    MainScreen(
        image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.WHITE)
        },
        leftScore = 0,
        rightScore = 0,
        onShowCameraNodesList = {},
        onShowNodesList = {},
        onLeftScoreClick = {},
        onRightScoreClick = {}
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainScreenPreviewEmpty() {
    MainScreen(
        image = null,
        leftScore = 0,
        rightScore = 0,
        onShowCameraNodesList = {},
        onShowNodesList = {},
        onLeftScoreClick = {},
        onRightScoreClick = {}
    )
}
