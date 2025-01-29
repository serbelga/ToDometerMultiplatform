/*
 * Copyright 2025 Sergio Belda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.sergiobelda.todometer.common.ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<UIState : BaseUIState>(
    initialUIState: UIState,
) : ViewModel(), BaseEventHandler {

    private var _uiState by mutableStateOf(initialUIState)

    val uiState: UIState get() = _uiState

    abstract override fun handleEvent(event: BaseEvent)

    protected fun updateUIState(block: (UIState) -> UIState) {
        _uiState = block.invoke(uiState)
    }
}
