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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class MainViewModel(
    application: Application
) :
    AndroidViewModel(application),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private var _initialized = mutableStateOf(false)
    val initialized: Boolean by _initialized

    private var _leftScore = mutableStateOf<Int?>(null)
    val leftScore: Int? by _leftScore

    private var _rightScore = mutableStateOf<Int?>(null)
    val rightScore: Int? by _rightScore

    init {
        // First check for existing data
        viewModelScope.launch {
            try {
                val dataItems = Wearable.getDataClient(getApplication())
                    .dataItems
                    .await()

                var foundData = false
                dataItems.forEach { dataItem ->
                    if (dataItem.uri.path == "/scores") {
                        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                        _leftScore.value = dataMap.getInt("left_score")
                        _rightScore.value = dataMap.getInt("right_score")
                        _initialized.value = true
                        foundData = true
                        Log.d(TAG, "Found existing data: ${_leftScore.value} - ${_rightScore.value}")
                        return@forEach
                    }
                }

                // If no existing data, request from phone
                if (!foundData) {
                    requestScores()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking existing data", e)
                requestScores()
            }
        }
    }

    private fun requestScores() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Requesting scores from phone")
                val nodes = Wearable.getNodeClient(getApplication())
                    .connectedNodes
                    .await()

                // Send request to all nodes
                nodes.map { node ->
                    async {
                        Wearable.getMessageClient(getApplication())
                            .sendMessage(node.id, "/request_scores", byteArrayOf())
                            .await()
                    }
                }.awaitAll()

                // Wait for response or timeout
                withTimeout(3000) {
                    while (!_initialized.value) {
                        delay(100)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting scores", e)
                // If we timeout or can't reach phone, show zeros
                _leftScore.value = 0
                _rightScore.value = 0
                _initialized.value = true
            }
        }
    }

    fun incrementLeftScore() {
        sendScoreCommand("/increment_left")
    }

    fun decrementLeftScore() {
        sendScoreCommand("/decrement_left")
    }

    fun incrementRightScore() {
        sendScoreCommand("/increment_right")
    }

    fun decrementRightScore() {
        sendScoreCommand("/decrement_right")
    }

    fun resetScores() {
        sendScoreCommand("/reset_scores")
    }

    private fun sendScoreCommand(path: String) {
        viewModelScope.launch {
            try {
                val nodes = Wearable.getNodeClient(getApplication())
                    .connectedNodes
                    .await()

                nodes.map { node ->
                    async {
                        Wearable.getMessageClient(getApplication())
                            .sendMessage(node.id, path, byteArrayOf())
                            .await()
                    }
                }.awaitAll()
            } catch (e: Exception) {
                Log.e(TAG, "Error sending command: $path", e)
            }
        }
    }

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { dataEvent ->
            if (dataEvent.type == DataEvent.TYPE_CHANGED && 
                dataEvent.dataItem.uri.path == "/scores") {
                val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                _leftScore.value = dataMap.getInt("left_score")
                _rightScore.value = dataMap.getInt("right_score")
                _initialized.value = true
                Log.d(TAG, "Received score update: ${_leftScore.value} - ${_rightScore.value}")
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Not used
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        // Not used
    }

    companion object {
        private const val TAG = "MainViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY]!!
                MainViewModel(
                    application
                )
            }
        }
    }
}
