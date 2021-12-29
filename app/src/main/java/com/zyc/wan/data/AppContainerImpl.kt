/*
 * Copyright 2020 The Android Open Source Project
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

package com.zyc.wan.data

import android.content.Context
import com.zyc.wan.data.network.WebApi
import com.zyc.wan.data.repo.UserRepo
import com.zyc.wan.data.repo.WxListsRepo
import com.zyc.wan.data.repo.online.UserRepoOnline
import com.zyc.wan.data.repo.online.WxListsRepoOnline

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val wxListsRepo: WxListsRepo
    val userRepo: UserRepo
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val wxListsRepo: WxListsRepo by lazy {
        WxListsRepoOnline(WebApi.create())
    }

    override val userRepo: UserRepo by lazy {
        UserRepoOnline(WebApi.create())
    }
}
