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
import android.util.Log
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

    init {
        // Check for existing data
        viewModelScope.launch {
            try {
                val dataItems = Wearable.getDataClient(getApplication())
                    .dataItems
                    .await()

                dataItems.forEach { dataItem ->
                    if (dataItem.uri.path == "/scores") {
                        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                        leftScore = dataMap.getInt("left_score")
                        rightScore = dataMap.getInt("right_score")
                        Log.d(TAG, "Found existing data: $leftScore - $rightScore")
                        return@forEach
                    }
                }

                // If no data exists, initialize with zeros
                Log.d(TAG, "No existing data, initializing with zeros")
                sendScoreUpdate()
            } catch (e: Exception) {
                Log.e(TAG, "Error checking existing data", e)
            }
        }
    }

    fun updateScores(newLeft: Int, newRight: Int) {
        if (leftScore != newLeft || rightScore != newRight) {
            _scoreHistory.add(0, Pair(leftScore, rightScore))
            if (_scoreHistory.size > 5) {
                _scoreHistory.removeAt(5)
            }
        }
        
        leftScore = newLeft
        rightScore = newRight
        sendScoreUpdate()
    }

    private fun sendScoreUpdate() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Sending score update: $leftScore - $rightScore")
                val request = PutDataMapRequest.create("/scores").apply {
                    dataMap.putInt("left_score", leftScore)
                    dataMap.putInt("right_score", rightScore)
                }.asPutDataRequest()
                    .setUrgent()

                Wearable.getDataClient(getApplication()).putDataItem(request).await()
            } catch (e: Exception) {
                Log.e(TAG, "Error sending score update", e)
            }
        }
    }

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        // Phone is source of truth, ignore data changes
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "Message received: ${messageEvent.path}")
        when (messageEvent.path) {
            "/request_scores" -> {
                sendScoreUpdate()
            }
            "/increment_left" -> {
                updateScores(leftScore + 1, rightScore)
            }
            "/decrement_left" -> {
                if (leftScore > 0) {
                    updateScores(leftScore - 1, rightScore)
                }
            }
            "/increment_right" -> {
                updateScores(leftScore, rightScore + 1)
            }
            "/decrement_right" -> {
                if (rightScore > 0) {
                    updateScores(leftScore, rightScore - 1)
                }
            }
            "/reset_scores" -> {
                updateScores(0, 0)
            }
        }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        // Not used
    }

    companion object {
        private const val TAG = "ClientDataViewModel"
    }
}
