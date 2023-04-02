/*
 * Copyright 2021 Sergio Belda
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

package dev.sergiobelda.todometer.common.compose.ui.components.task

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sergiobelda.todometer.common.compose.ui.designsystem.theme.ToDometerTheme
import dev.sergiobelda.todometer.common.compose.ui.mapper.composeColorOf
import dev.sergiobelda.todometer.common.domain.model.TaskItem
import dev.sergiobelda.todometer.common.domain.model.TaskState
import dev.sergiobelda.todometer.common.resources.MR
import dev.sergiobelda.todometer.common.resources.ToDometerIcons
import dev.sergiobelda.todometer.common.resources.painterResource
import dev.sergiobelda.todometer.common.resources.stringResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableTaskItem(
    taskItem: TaskItem,
    onDoingClick: (String) -> Unit,
    onDoneClick: (String) -> Unit,
    onTaskItemClick: (String) -> Unit,
    onTaskItemLongClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSwipeToDismiss: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd) {
                onSwipeToDismiss()
            }
            it != DismissValue.DismissedToEnd
        }
    )
    val backgroundColor by animateColorAsState(
        if (dismissState.targetValue == DismissValue.Default) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
    val backgroundIconTint by animateColorAsState(
        if (dismissState.targetValue == DismissValue.Default) ToDometerTheme.toDometerColors.onSurfaceMediumEmphasis else MaterialTheme.colorScheme.onError,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
    val taskItemCornerRadius by animateDpAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.dp else 8.dp,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        dismissThresholds = {
            FractionalThreshold(0.1f)
        },
        background = {
            // TODO: Enable AVD when available in multiplatform.
            // val icon = AnimatedImageVector.animatedVectorResource(R.drawable.avd_delete)
            Box(
                Modifier.fillMaxSize().background(backgroundColor).padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // TODO: Enable AVD when available in multiplatform.
                /*
                Icon(
                    rememberAnimatedVectorPainter(
                        icon,
                        atEnd = dismissState.targetValue == DismissValue.DismissedToEnd
                    ),
                    contentDescription = stringResource(MR.strings.delete_task),
                    tint = tint
                )
                */
                Icon(
                    painter = painterResource(ToDometerIcons.Delete),
                    contentDescription = stringResource(MR.strings.delete_task),
                    tint = backgroundIconTint
                )
            }
        },
        dismissContent = {
            TaskItem(
                taskItem,
                onDoingClick = onDoingClick,
                onDoneClick = onDoneClick,
                onClick = onTaskItemClick,
                onLongClick = onTaskItemLongClick,
                shape = RoundedCornerShape(taskItemCornerRadius)
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskItem(
    taskItem: TaskItem,
    onDoingClick: (String) -> Unit,
    onDoneClick: (String) -> Unit,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = shape,
        modifier = modifier.combinedClickable(
            onClick = {
                onClick(taskItem.id)
            },
            onLongClick = {
                onLongClick(taskItem.id)
            }
        ).fillMaxWidth()
    ) {
        TaskItemHeadlineContent(
            taskItem = taskItem,
            onDoingClick = onDoingClick,
            onDoneClick = onDoneClick
        )
        TaskItemSupportingContent(taskItem)
    }
}

@Composable
private fun TaskItemHeadlineContent(
    taskItem: TaskItem,
    onDoingClick: (String) -> Unit,
    onDoneClick: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = TaskItemPaddingStart, end = TaskItemPaddingEnd)
    ) {
        Box(
            modifier = Modifier
                .size(TaskItemTagIndicatorSize)
                .clip(CircleShape)
                .background(ToDometerTheme.toDometerColors.composeColorOf(taskItem.tag))
        )
        Text(
            taskItem.title,
            textDecoration = taskItemTitleTextDecoration(taskItem.state),
            color = taskItemTitleColor(taskItem.state),
            modifier = Modifier.padding(start = 8.dp).weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = {
                if (taskItem.state == TaskState.DONE) {
                    onDoingClick(taskItem.id)
                } else {
                    onDoneClick(taskItem.id)
                }
            }
        ) {
            Icon(
                taskItemActionIcon(taskItem.state),
                contentDescription = taskItemActionContentDescription(taskItem.state),
                tint = ToDometerTheme.toDometerColors.check
            )
        }
    }
}

@Composable
private fun TaskItemSupportingContent(taskItem: TaskItem) {
    LazyRow(
        modifier = Modifier.padding(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.width(32.dp))
        }
        if (taskItem.state == TaskState.DOING) {
            taskItem.dueDate?.let { dueDate ->
                item {
                    TaskDueDateChip(dueDate, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }
        if (taskItem.totalChecklistItems > 0) {
            item {
                TaskChecklistItemsChip(taskItem.checklistItemsDone, taskItem.totalChecklistItems)
            }
        }
        item {
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun taskItemTitleColor(state: TaskState): Color =
    when (state) {
        TaskState.DOING -> MaterialTheme.colorScheme.onSurface
        TaskState.DONE -> ToDometerTheme.toDometerColors.onSurfaceMediumEmphasis
    }

private fun taskItemTitleTextDecoration(state: TaskState): TextDecoration =
    when (state) {
        TaskState.DOING -> TextDecoration.None
        TaskState.DONE -> TextDecoration.LineThrough
    }

@Composable
private fun taskItemActionIcon(state: TaskState): Painter =
    when (state) {
        TaskState.DOING -> painterResource(ToDometerIcons.Check)
        TaskState.DONE -> painterResource(ToDometerIcons.Replay)
    }

@Composable
private fun taskItemActionContentDescription(state: TaskState): String =
    when (state) {
        TaskState.DOING -> stringResource(MR.strings.check_task)
        TaskState.DONE -> stringResource(MR.strings.uncheck_task)
    }

private val TaskItemTagIndicatorSize: Dp = 16.dp
private val TaskItemPaddingStart: Dp = 20.dp
private val TaskItemPaddingEnd: Dp = 12.dp
