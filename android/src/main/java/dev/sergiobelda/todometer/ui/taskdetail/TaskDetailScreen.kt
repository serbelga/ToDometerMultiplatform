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

package dev.sergiobelda.todometer.ui.taskdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sergiobelda.todometer.R
import dev.sergiobelda.todometer.common.compose.ui.components.HorizontalDivider
import dev.sergiobelda.todometer.common.compose.ui.components.ToDometerCheckbox
import dev.sergiobelda.todometer.common.compose.ui.mapper.composeColorOf
import dev.sergiobelda.todometer.common.compose.ui.task.TaskDueDateChip
import dev.sergiobelda.todometer.common.compose.ui.theme.TodometerColors
import dev.sergiobelda.todometer.common.compose.ui.theme.TodometerTypography
import dev.sergiobelda.todometer.common.compose.ui.theme.onSurfaceMediumEmphasis
import dev.sergiobelda.todometer.common.domain.model.Task
import dev.sergiobelda.todometer.common.domain.model.TaskChecklistItem
import dev.sergiobelda.todometer.common.domain.model.TaskChecklistItemState
import dev.sergiobelda.todometer.ui.components.ToDometerContentLoadingProgress

private const val SECTION_PADDING = 32

@Composable
fun TaskDetailScreen(
    editTask: () -> Unit,
    navigateUp: () -> Unit,
    taskDetailViewModel: TaskDetailViewModel
) {
    val lazyListState = rememberLazyListState()
    val taskDetailUiState = taskDetailViewModel.taskDetailUiState
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (lazyListState.firstVisibleItemScrollOffset > 100) {
                        if (!taskDetailUiState.isLoadingTask && taskDetailUiState.task != null) {
                            Text(taskDetailUiState.task.title)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TodometerColors.onSurfaceMediumEmphasis
                        )
                    }
                },
                actions = {
                    if (!taskDetailUiState.isLoadingTask && taskDetailUiState.task != null) {
                        IconButton(onClick = editTask) {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = "Edit task",
                                tint = TodometerColors.primary
                            )
                        }
                    }
                },
                elevation = 0.dp,
                backgroundColor = TodometerColors.surface,
                contentColor = contentColorFor(backgroundColor = TodometerColors.surface)
            )
        },
        content = {
            if (taskDetailUiState.isLoadingTask) {
                ToDometerContentLoadingProgress()
            } else {
                taskDetailUiState.task?.let { task ->
                    LazyColumn(state = lazyListState) {
                        taskTitle(task)
                        taskChips(task)
                        taskChecklist(
                            taskDetailUiState.taskChecklistItems,
                            onTaskChecklistItemClick = { id, checked ->
                                if (checked) taskDetailViewModel.setTaskChecklistItemChecked(id) else taskDetailViewModel.setTaskChecklistItemUnchecked(
                                    id
                                )
                            },
                            onAddTaskCheckListItem = { text ->
                                taskDetailViewModel.insertTaskChecklistItem(text)
                            },
                            onDeleteTaskCheckListItem = { id ->
                                taskDetailViewModel.deleteTaskChecklistItem(id)
                            }
                        )
                        taskDescription(task.description)
                    }
                }
            }
        }
    )
}

private fun LazyListScope.taskTitle(task: Task) {
    item {
        Surface(modifier = Modifier.height(64.dp)) {
            Row(
                modifier = Modifier.padding(start = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(TodometerColors.composeColorOf(task.tag))
                )
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = task.title,
                        style = TodometerTypography.h6,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                        maxLines = 1
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

private fun LazyListScope.taskChips(task: Task) {
    item {
        task.dueDate?.let {
            TaskDueDateChip(it, modifier = Modifier.padding(start = 24.dp, top = 24.dp))
        }
    }
}

fun LazyListScope.taskChecklist(
    taskChecklistItems: List<TaskChecklistItem>,
    onTaskChecklistItemClick: (String, Boolean) -> Unit,
    onAddTaskCheckListItem: (String) -> Unit,
    onDeleteTaskCheckListItem: (String) -> Unit
) {
    // TODO: padding top 24.dp
    item {
        TaskDetailSectionTitle(stringResource(R.string.checklist))
    }
    items(taskChecklistItems) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable {
                onTaskChecklistItemClick(
                    it.id,
                    it.state == TaskChecklistItemState.UNCHECKED
                )
            }
        ) {
            ToDometerCheckbox(
                checked = it.state == TaskChecklistItemState.CHECKED,
                onCheckedChange = { checked ->
                    onTaskChecklistItemClick(
                        it.id,
                        checked
                    )
                },
                modifier = Modifier.scale(0.85f).padding(start = 16.dp)
            )
            Text(
                text = it.text,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = { onDeleteTaskCheckListItem(it.id) }) {
                Icon(
                    Icons.Rounded.Clear,
                    contentDescription = stringResource(R.string.clear),
                    tint = TodometerColors.onSurfaceMediumEmphasis
                )
            }
        }
    }
    item {
        var taskChecklistItemText by remember { mutableStateOf("") }
        val addTaskChecklistItemAction = {
            if (taskChecklistItemText.isNotBlank()) {
                onAddTaskCheckListItem(taskChecklistItemText)
                taskChecklistItemText = ""
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
            OutlinedTextField(
                value = taskChecklistItemText,
                onValueChange = { taskChecklistItemText = it },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent
                ),
                placeholder = { Text(stringResource(R.string.add_element)) },
                maxLines = 1,
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = { addTaskChecklistItemAction() }
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
            if (taskChecklistItemText.isNotBlank()) {
                IconButton(
                    onClick = addTaskChecklistItemAction
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = stringResource(R.string.add),
                        tint = TodometerColors.primary
                    )
                }
            }
        }
    }
    item {
        HorizontalDivider()
    }
}

fun LazyListScope.taskDescription(description: String?) {
    // TODO: padding top 24.dp
    item {
        TaskDetailSectionTitle(stringResource(R.string.description))
    }
    item {
        if (!description.isNullOrBlank()) {
            Text(
                text = description,
                style = TodometerTypography.body1,
                modifier = Modifier.padding(
                    start = SECTION_PADDING.dp,
                    end = SECTION_PADDING.dp,
                    bottom = SECTION_PADDING.dp
                )
            )
        } else {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = R.string.no_description),
                    style = TodometerTypography.body1,
                    modifier = Modifier.padding(
                        top = 16.dp, start = SECTION_PADDING.dp,
                        end = SECTION_PADDING.dp,
                        bottom = SECTION_PADDING.dp
                    )
                )
            }
        }
    }
}

@Composable
fun TaskDetailSectionTitle(text: String) {
    Text(
        text,
        color = TodometerColors.primary,
        style = TodometerTypography.caption,
        modifier = Modifier.padding(start = SECTION_PADDING.dp, bottom = 8.dp)
    )
}
