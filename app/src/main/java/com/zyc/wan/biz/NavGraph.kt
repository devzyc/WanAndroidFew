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

@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package com.zyc.wan.biz

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.zyc.wan.biz.detail.ArticleDetailScreen
import com.zyc.wan.biz.home.WxListsScreen
import com.zyc.wan.biz.home.WxListsViewModel
import com.zyc.wan.biz.login.LoginScreen
import com.zyc.wan.biz.login.LoginViewModel
import com.zyc.wan.data.AppContainer
import com.zyc.wan.data.network.WebApi
import com.zyc.wan.data.repo.online.WxListsRepoOnline
import com.zyc.wan.definable.Def
import com.zyc.wan.definable.PassKey
import com.zyc.wan.definable.Route
import java.net.URLDecoder

var wxListsViewModel = WxListsViewModel(WxListsRepoOnline(WebApi.create()))

@ExperimentalAnimationApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavGraph(appContainer: AppContainer) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = Route.WX_LISTS,
    ) {
        composable2(route = Route.WX_LISTS) {
            if (navController.previousBackStackEntry?.destination?.route == Route.LOGIN) {
                wxListsViewModel = WxListsViewModel(WxListsRepoOnline(WebApi.create()))
                navController.popBackStack()
                navController.popBackStack() // close LoginScreen and previous WxListsScreen
            }
            WxListsScreen(viewModel = wxListsViewModel, navController)
        }
        composable2(route = Route.LOGIN) {
            LoginScreen(viewModel = LoginViewModel(appContainer.userRepo), navController)
        }
        composable2(
            route = "${Route.ARTICLE_DETAIL}/{${PassKey.URL}}",
            arguments = listOf(navArgument(PassKey.URL) { type = NavType.StringType })
        ) {
            ArticleDetailScreen(
                navController,
                URLDecoder.decode(it.arguments?.getString(PassKey.URL), Charsets.UTF_8.name())
            )
        }
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.composable2(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    exitTransition: (
    AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?
    )? = null,
    popExitTransition: (
    AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?
    )? = exitTransition,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route, arguments, deepLinks,
        enterTransition = { _, _ -> slideLeft() },
        exitTransition,
        popEnterTransition = { _, _ -> slideRight() },
        popExitTransition, content
    )
}

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentScope<String>.slideLeft() =
    slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(Def.TRANSITION_DURATION))

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentScope<String>.slideRight() =
    slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(Def.TRANSITION_DURATION))
