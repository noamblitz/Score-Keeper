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

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClientDataViewModel(application: Application) :
    AndroidViewModel(application),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    var leftScore by mutableStateOf(0)
        private set

    var rightScore by mutableStateOf(0)
        private set

    private val _scoreHistory = mutableStateListOf<Pair<Int, Int>>()
    val scoreHistory: List<Pair<Int, Int>> = _scoreHistory

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { dataEvent ->
            if (dataEvent.type == DataEvent.TYPE_CHANGED && 
                dataEvent.dataItem.uri.path == "/scores") {
                val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                val newLeftScore = dataMap.getInt("left_score")
                val newRightScore = dataMap.getInt("right_score")
                
                // Only add to history if scores changed
                if (leftScore != newLeftScore || rightScore != newRightScore) {
                    _scoreHistory.add(0, Pair(leftScore, rightScore))
                    if (_scoreHistory.size > 5) {
                        _scoreHistory.removeAt(5)
                    }
                }
                
                leftScore = newLeftScore
                rightScore = newRightScore
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            "/request_scores" -> {
                // Send current scores to the watch
                viewModelScope.launch {
                    try {
                        val request = PutDataMapRequest.create("/scores").apply {
                            dataMap.putInt("left_score", leftScore)
                            dataMap.putInt("right_score", rightScore)
                        }.asPutDataRequest()
                            .setUrgent()

                        Wearable.getDataClient(getApplication()).putDataItem(request).await()
                    } catch (e: Exception) {
                        // Handle error
                    }
                }
            }
        }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        // Not used
    }
}
