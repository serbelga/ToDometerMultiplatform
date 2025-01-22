/*
 * Copyright 2022 Sergio Belda
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

package dev.sergiobelda.todometer.app.feature.addtasklist.ui

import androidx.lifecycle.viewModelScope
import dev.sergiobelda.todometer.common.domain.doIfError
import dev.sergiobelda.todometer.common.domain.doIfSuccess
import dev.sergiobelda.todometer.common.domain.usecase.tasklist.InsertTaskListUseCase
import dev.sergiobelda.todometer.common.ui.base.BaseEvent
import dev.sergiobelda.todometer.common.ui.base.BaseViewModel
import dev.sergiobelda.todometer.common.ui.error.mapToErrorUi
import kotlinx.coroutines.launch

class AddTaskListViewModel(
    private val insertTaskListUseCase: InsertTaskListUseCase,
) : BaseViewModel<AddTaskListState>(
    initialState = AddTaskListState(),
) {

    override fun handleEvent(event: BaseEvent) {
        when (event) {
            is AddTaskListEvents.InsertTaskList -> {
                insertTaskList(event.name)
            }
        }
    }

    private fun insertTaskList(name: String) = viewModelScope.launch {
        updateState { state ->
            state.copy(isAddingTaskList = true)
        }
        val result = insertTaskListUseCase.invoke(name)
        result.doIfSuccess {
            updateState { state ->
                state.copy(
                    isAddingTaskList = false,
                    errorUi = null,
                )
            }
        }.doIfError { error ->
            updateState { state ->
                state.copy(
                    isAddingTaskList = false,
                    errorUi = error.mapToErrorUi(),
                )
            }
        }
    }
}
