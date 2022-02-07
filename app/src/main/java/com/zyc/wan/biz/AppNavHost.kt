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
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.animatedComposable
import com.zyc.wan.biz.destinations.ArticleDetailScreenDestination
import com.zyc.wan.biz.destinations.LoginScreenDestination
import com.zyc.wan.biz.destinations.SearchScreenDestination
import com.zyc.wan.biz.destinations.WxListsScreenDestination
import com.zyc.wan.biz.detail.ArticleDetailScreen
import com.zyc.wan.biz.home.wx.WxListsScreen
import com.zyc.wan.biz.home.wx.WxListsViewModel
import com.zyc.wan.biz.login.LoginScreen
import com.zyc.wan.biz.login.LoginViewModel
import com.zyc.wan.biz.search.SearchScreen
import com.zyc.wan.biz.search.SearchViewModel
import com.zyc.wan.data.AppContainer
import com.zyc.wan.data.network.WebApi
import com.zyc.wan.data.repo.online.SearchRepoOnline
import com.zyc.wan.data.repo.online.WxListsRepoOnline
import com.zyc.wan.definable.Def

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AppNavHost(container: AppContainer) {
    val controller = rememberAnimatedNavController()

    val navHostEngine = rememberAnimatedNavHostEngine(
        rootDefaultAnimations = RootNavGraphDefaultAnimations(
            enterTransition = { slideLeft() },
            popEnterTransition = { slideRight() }
        ),
        defaultAnimationsForNestedNavGraph = mapOf(
            NavGraphs.root to NestedNavGraphDefaultAnimations(
                enterTransition = { slideLeft() },
                popEnterTransition = { slideRight() }
            )
        )
    )

    DestinationsNavHost(
        navGraph = NavGraphs.root,
        engine = navHostEngine,
        navController = controller,
    ) {
        animatedComposable(WxListsScreenDestination) {
            if (navController.previousBackStackEntry?.destination?.route == LoginScreenDestination.route) {
                container.wxListsViewModel = WxListsViewModel(WxListsRepoOnline(WebApi.create()))
                navController.popBackStack()
                navController.popBackStack() // close LoginScreen and previous WxListsScreen
            }
            WxListsScreen(viewModel = container.wxListsViewModel, destinationsNavigator)
        }
        animatedComposable(ArticleDetailScreenDestination) {
            ArticleDetailScreen(navArgs.url, destinationsNavigator)
        }
        animatedComposable(LoginScreenDestination) {
            LoginScreen(viewModel = LoginViewModel(container.userRepo), destinationsNavigator)
        }
        animatedComposable(SearchScreenDestination) {
            if (navController.previousBackStackEntry?.destination?.route == LoginScreenDestination.route) {
                container.searchViewModel = SearchViewModel(SearchRepoOnline(WebApi.create()))
                navController.popBackStack()
                navController.popBackStack() // close LoginScreen and previous SearchScreen
            }
            SearchScreen(viewModel = container.searchViewModel, destinationsNavigator)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentScope<NavBackStackEntry>.slideLeft(): EnterTransition = slideIntoContainer(
    AnimatedContentScope.SlideDirection.Left,
    animationSpec = tween(Def.SCREEN_TRANSITION_DURATION)
) + fadeIn(animationSpec = tween(Def.SCREEN_TRANSITION_DURATION))

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentScope<NavBackStackEntry>.slideRight(): EnterTransition = slideIntoContainer(
    AnimatedContentScope.SlideDirection.Right,
    animationSpec = tween(Def.SCREEN_TRANSITION_DURATION)
) + fadeIn(animationSpec = tween(Def.SCREEN_TRANSITION_DURATION))
