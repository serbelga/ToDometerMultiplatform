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

package dev.sergiobelda.todometer.common.di

import dev.sergiobelda.todometer.common.data.database.createDatabase
import dev.sergiobelda.todometer.common.data.database.dao.ITaskChecklistItemDao
import dev.sergiobelda.todometer.common.data.database.dao.ITaskDao
import dev.sergiobelda.todometer.common.data.database.dao.ITaskListDao
import dev.sergiobelda.todometer.common.data.database.dao.TaskChecklistItemDao
import dev.sergiobelda.todometer.common.data.database.dao.TaskDao
import dev.sergiobelda.todometer.common.data.database.dao.TaskListDao
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val localDatabaseModule = module {
    single { createDatabase() }
    singleOf(::TaskDao) bind ITaskDao::class
    singleOf(::TaskListDao) bind ITaskListDao::class
    singleOf(::TaskChecklistItemDao) bind ITaskChecklistItemDao::class
}
