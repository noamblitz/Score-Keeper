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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainApp(
    leftScore: Int,
    rightScore: Int,
    scoreHistory: List<Pair<Int, Int>>,
    onStartWearableActivityClick: () -> Unit,
    onLeftScoreClick: () -> Unit = {},
    onLeftScoreLongClick: () -> Unit = {},
    onRightScoreClick: () -> Unit = {},
    onRightScoreLongClick: () -> Unit = {},
    onResetLongClick: () -> Unit = {}
) {
    val isPortrait = LocalConfiguration.current.screenWidthDp < LocalConfiguration.current.screenHeightDp
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = leftScore.toString(),
                    style = MaterialTheme.typography.h1.copy(
                        fontSize = calculateFontSize(leftScore, isPortrait).sp
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
                
                // Score History
                Column(
                    modifier = Modifier
                        .width(60.dp)
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        text = "History",
                        style = MaterialTheme.typography.caption,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    scoreHistory.forEach { (left, right) ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = left.toString(),
                                style = MaterialTheme.typography.caption,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = right.toString(),
                                style = MaterialTheme.typography.caption,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                Text(
                    text = rightScore.toString(),
                    style = MaterialTheme.typography.h1.copy(
                        fontSize = calculateFontSize(rightScore, isPortrait).sp
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
            
            androidx.compose.material.Button(
                onClick = onStartWearableActivityClick,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(stringResource(id = R.string.start_wearable_activity))
            }
        }
    }
}

private fun calculateFontSize(score: Int, isPortrait: Boolean): Int {
    val digitCount = score.toString().length
    return if (isPortrait) {
        when (digitCount) {
            1 -> 200
            2 -> 160
            else -> 120
        }
    } else {
        when (digitCount) {
            1 -> 120
            2 -> 100
            else -> 80
        }
    }
}

@Preview
@Composable
fun MainAppPreview() {
    MainApp(
        leftScore = 0,
        rightScore = 0,
        scoreHistory = listOf(
            Pair(1, 2),
            Pair(1, 1),
            Pair(0, 1),
            Pair(0, 0)
        ),
        onStartWearableActivityClick = {}
    )
}
