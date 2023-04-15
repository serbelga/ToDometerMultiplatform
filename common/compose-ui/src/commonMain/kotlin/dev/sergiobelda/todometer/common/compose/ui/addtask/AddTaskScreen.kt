/*
 * Copyright 2023 Sergio Belda
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

package dev.sergiobelda.todometer.common.compose.ui.addtask

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sergiobelda.todometer.common.compose.ui.components.AddChecklistItemField
import dev.sergiobelda.todometer.common.compose.ui.components.DateTimeSelector
import dev.sergiobelda.todometer.common.compose.ui.components.TagSelector
import dev.sergiobelda.todometer.common.compose.ui.designsystem.components.ToDometerDivider
import dev.sergiobelda.todometer.common.compose.ui.designsystem.components.ToDometerTitledTextField
import dev.sergiobelda.todometer.common.compose.ui.designsystem.theme.ToDometerTheme
import dev.sergiobelda.todometer.common.domain.model.Tag
import dev.sergiobelda.todometer.common.resources.MR
import dev.sergiobelda.todometer.common.resources.ToDometerIcons
import dev.sergiobelda.todometer.common.resources.painterResource
import dev.sergiobelda.todometer.common.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navigateBack: () -> Unit,
    insertTask: (taskTitle: String, selectedTag: Tag, taskDescription: String, taskDueDate: Long?) -> Unit,
    addTaskUiState: AddTaskUiState,
    taskChecklistItems: List<String>,
    onAddTaskCheckListItem: (String) -> Unit,
    onDeleteTaskCheckListItem: (index: Int) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    var taskTitle by rememberSaveable { mutableStateOf("") }
    var taskTitleInputError by remember { mutableStateOf(false) }
    var taskDescription by rememberSaveable { mutableStateOf("") }
    val tags = enumValues<Tag>()
    var selectedTag by rememberSaveable { mutableStateOf(tags.firstOrNull() ?: Tag.GRAY) }
    var taskDueDate: Long? by rememberSaveable { mutableStateOf(null) }

    if (addTaskUiState.isAdded) {
        navigateBack()
    }

    if (addTaskUiState.errorUi != null) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = addTaskUiState.errorUi.message ?: ""
            )
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painterResource(ToDometerIcons.ArrowBack),
                            contentDescription = stringResource(MR.strings.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = !addTaskUiState.isAddingTask,
                        onClick = {
                            if (taskTitle.isBlank()) {
                                taskTitleInputError = true
                            } else {
                                insertTask(
                                    taskTitle,
                                    selectedTag,
                                    taskDescription,
                                    taskDueDate
                                )
                            }
                        }
                    ) {
                        Icon(
                            painterResource(ToDometerIcons.Check),
                            contentDescription = "Save",
                            tint = if (addTaskUiState.isAddingTask) ToDometerTheme.toDometerColors.onSurfaceMediumEmphasis else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                title = { Text(stringResource(MR.strings.add_task)) },
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            if (addTaskUiState.isAddingTask) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(state = lazyListState, modifier = Modifier.padding(paddingValues)) {
                item {
                    ToDometerTitledTextField(
                        title = stringResource(MR.strings.name),
                        value = taskTitle,
                        onValueChange = {
                            taskTitle = it
                            taskTitleInputError = false
                        },
                        placeholder = { Text(stringResource(MR.strings.enter_task_name)) },
                        isError = taskTitleInputError,
                        errorMessage = stringResource(MR.strings.field_not_empty),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                    )
                }
                item {
                    TagSelector(selectedTag) { tag ->
                        selectedTag = tag
                    }
                }
                item {
                    DateTimeSelector(
                        taskDueDate,
                        onDateTimeSelected = { taskDueDate = it },
                        onClearDateTimeClick = { taskDueDate = null }
                    )
                }
                item {
                    Text(
                        text = stringResource(MR.strings.checklist),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 32.dp, top = 16.dp)
                    )
                }
                itemsIndexed(taskChecklistItems) { index, item ->
                    TaskChecklistItem(
                        item,
                        onDeleteTaskCheckListItem = { onDeleteTaskCheckListItem(index) }
                    )
                }
                item {
                    AddChecklistItemField(
                        placeholder = { Text(stringResource(MR.strings.add_element_optional)) },
                        onAddTaskCheckListItem = onAddTaskCheckListItem
                    )
                }
                item {
                    ToDometerTitledTextField(
                        title = stringResource(MR.strings.description),
                        value = taskDescription,
                        onValueChange = { taskDescription = it },
                        placeholder = { Text(stringResource(MR.strings.enter_description)) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        ),
                        maxLines = 4
                    )
                }
                item {
                    ToDometerDivider()
                }
            }
        }
    )
}

@Composable
private fun TaskChecklistItem(
    taskChecklistItem: String,
    onDeleteTaskCheckListItem: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 32.dp)
    ) {
        Text(
            text = taskChecklistItem,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(onClick = onDeleteTaskCheckListItem) {
            Icon(
                painterResource(ToDometerIcons.Close),
                contentDescription = stringResource(MR.strings.clear),
                tint = ToDometerTheme.toDometerColors.onSurfaceMediumEmphasis
            )
        }
    }
}
