package com.zyc.wan.biz

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.animations.utils.animatedComposable
import com.ramcosta.composedestinations.navigation.DestinationsNavController
import com.zyc.wan.App
import com.zyc.wan.biz.destinations.*
import com.zyc.wan.biz.detail.ArticleDetailScreen
import com.zyc.wan.biz.home.project.ProjectScreen
import com.zyc.wan.biz.home.square.SquareScreen
import com.zyc.wan.biz.home.system.SystemScreen
import com.zyc.wan.biz.home.tabone.TabOneScreen
import com.zyc.wan.biz.home.wx.WxListsScreen
import com.zyc.wan.biz.home.wx.WxListsViewModel
import com.zyc.wan.biz.login.LoginScreen
import com.zyc.wan.biz.search.SearchScreen
import com.zyc.wan.biz.search.SearchViewModel
import com.zyc.wan.data.repo.SearchRepo
import com.zyc.wan.data.repo.WxListsRepo
import com.zyc.wan.definable.Def
import com.zyc.wan.di.NetworkModule

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AppNavigation(navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Def.GRAPH_HOME,
    ) {
        navigation(
            route = Def.GRAPH_HOME,
            startDestination = TabOneScreenDestination.route,
        ) {
            addHomeGraph(navController)
        }

        animatedComposable(ArticleDetailScreenDestination) { navArgs, _ ->
            ArticleDetailScreen(
                url = navArgs.url,
                onBack = navController::popBackStack
            )
        }
        animatedComposable(LoginScreenDestination) {
            LoginScreen(
                onBack = navController::popBackStack,
                onLoginSuccess = { navController.navigate(WxListsScreenDestination.route) }
            )
        }
        animatedComposable(SearchScreenDestination) { _, entry ->
            if (navController.previousBackStackEntry?.destination?.route == LoginScreenDestination.route) {
                App.instance.searchViewModel = SearchViewModel(
                    SearchRepo(
                        NetworkModule.provideWebApi(
                            NetworkModule.provideRetrofit(
                                NetworkModule.provideOkHttpClient()
                            )
                        )
                    )
                )
                navController.popBackStack()
                navController.popBackStack() // close LoginScreen and previous SearchScreen
            }
            SearchScreen(
                viewModel = App.instance.searchViewModel,
                onFavoriteButtonClick = {
                    navController.navigate(LoginScreenDestination.route)
                },
                onItemClick = { url ->
                    openArticle(navController, entry, url)
                },
                onBack = navController::popBackStack
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
fun NavGraphBuilder.addHomeGraph(
    navController: NavHostController,
) {
    animatedComposable(TabOneScreenDestination) {
        TabOneScreen(viewModel = hiltViewModel())
    }
    animatedComposable(SquareScreenDestination) {
        SquareScreen(viewModel = hiltViewModel())
    }
    animatedComposable(WxListsScreenDestination) { entry ->
        if (navController.previousBackStackEntry?.destination?.route == LoginScreenDestination.route) {
            App.instance.wxListsViewModel = WxListsViewModel(
                WxListsRepo(
                    NetworkModule.provideWebApi(
                        NetworkModule.provideRetrofit(
                            NetworkModule.provideOkHttpClient()
                        )
                    )
                )
            )
            navController.popBackStack()
            navController.popBackStack() // close LoginScreen and previous WxListsScreen
        }
        WxListsScreen(
            viewModel = App.instance.wxListsViewModel,
            onSearchClick = {
                navController.navigate(SearchScreenDestination.route)
            },
            onFavoriteButtonClick = {
                navController.navigate(LoginScreenDestination.route)
            },
            onItemClick = { url ->
                openArticle(navController, entry, url)
            }
        )
    }
    animatedComposable(SystemScreenDestination) {
        SystemScreen(viewModel = hiltViewModel())
    }
    animatedComposable(ProjectScreenDestination) {
        ProjectScreen(viewModel = hiltViewModel())
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun openArticle(navController: NavHostController, entry: NavBackStackEntry, url: String) {
    DestinationsNavController(navController, entry)
        .navigate(ArticleDetailScreenDestination(url))
}

