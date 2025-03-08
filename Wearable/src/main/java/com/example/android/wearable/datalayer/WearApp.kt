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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.google.android.horologist.compose.layout.AppScaffold

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
    val leftScore = mainViewModel.leftScore
    val rightScore = mainViewModel.rightScore

    if (!mainViewModel.initialized || leftScore == null || rightScore == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        ScoreScreen(
            leftScore = leftScore,
            rightScore = rightScore,
            onLeftScoreClick = { mainViewModel.incrementLeftScore() },
            onLeftScoreLongClick = { mainViewModel.decrementLeftScore() },
            onRightScoreClick = { mainViewModel.incrementRightScore() },
            onRightScoreLongClick = { mainViewModel.decrementRightScore() },
            onResetLongClick = { mainViewModel.resetScores() }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScoreScreen(
    leftScore: Int,
    rightScore: Int,
    onLeftScoreClick: () -> Unit,
    onLeftScoreLongClick: () -> Unit,
    onRightScoreClick: () -> Unit,
    onRightScoreLongClick: () -> Unit,
    onResetLongClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = leftScore.toString(),
                style = MaterialTheme.typography.display1.copy(
                    fontSize = 60.sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = onLeftScoreClick,
                        onLongClick = onLeftScoreLongClick
                    ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onResetLongClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "Reset scores",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = rightScore.toString(),
                style = MaterialTheme.typography.display1.copy(
                    fontSize = 60.sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = onRightScoreClick,
                        onLongClick = onRightScoreLongClick
                    ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainScreenPreview() {
    ScoreScreen(
        leftScore = 0,
        rightScore = 0,
        onLeftScoreClick = {},
        onLeftScoreLongClick = {},
        onRightScoreClick = {},
        onRightScoreLongClick = {},
        onResetLongClick = {}
    )
}
