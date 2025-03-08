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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun WearApp(mainViewModel: MainViewModel) {
    AppScaffold {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(navController = navController, startDestination = "main") {
            composable("main") {
                MainScreen(
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel
) {
    MainScreen(
        mainViewModel.leftScore,
        mainViewModel.rightScore,
        onLeftScoreClick = { mainViewModel.incrementLeftScore() },
        onRightScoreClick = { mainViewModel.incrementRightScore() }
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MainScreen(
    leftScore: Int,
    rightScore: Int,
    onLeftScoreClick: () -> Unit,
    onRightScoreClick: () -> Unit
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
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
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainScreenPreview() {
    MainScreen(
        leftScore = 0,
        rightScore = 0,
        onLeftScoreClick = {},
        onRightScoreClick = {}
    )
}
