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
import com.zyc.wan.biz.home.wx.WxListsViewModel
import com.zyc.wan.biz.search.SearchViewModel
import com.zyc.wan.data.network.WebApi
import com.zyc.wan.data.repo.UserRepo
import com.zyc.wan.data.repo.online.SearchRepoOnline
import com.zyc.wan.data.repo.online.UserRepoOnline
import com.zyc.wan.data.repo.online.WxListsRepoOnline

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val userRepo: UserRepo
    var wxListsViewModel: WxListsViewModel
    var searchViewModel: SearchViewModel
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    private var _wxListsViewModel: WxListsViewModel = WxListsViewModel(WxListsRepoOnline(WebApi.create()))

    private var _searchViewModel: SearchViewModel = SearchViewModel(SearchRepoOnline(WebApi.create()))

    override val userRepo: UserRepo by lazy {
        UserRepoOnline(WebApi.create())
    }

    override var wxListsViewModel: WxListsViewModel
        get() = _wxListsViewModel
        set(value) {
            _wxListsViewModel = value
        }

    override var searchViewModel: SearchViewModel
        get() = _searchViewModel
        set(value) {
            _searchViewModel = value
        }
}
