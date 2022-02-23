package com.zyc.wan

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.spec.Direction
import com.zyc.wan.biz.AppNavigation
import com.zyc.wan.biz.destinations.*
import com.zyc.wan.ui.theme.WanTheme

/**
 * @author devzyc
 */
@ExperimentalAnimationApi
@Composable
fun WanApp() {
    WanTheme {
        val uiController = rememberSystemUiController()
        val color = colors.primary
        SideEffect {
            uiController.setSystemBarsColor(color)
        }
        val navController = rememberAnimatedNavController()

        Scaffold(
            bottomBar = {
                val tabRoutes = remember {
                    WanTabs.values().map { it.direction.route }
                }
                if (navController.currentBackStackEntryAsState().value?.destination?.route
                    in tabRoutes
                ) {
                    HomeBottomNavigation(navController)
                }
            }
        ) {
            AppNavigation(navController)
        }
    }
}

@Composable
fun HomeBottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
        ?: WanTabs.TAB_ONE.direction.route

    BottomNavigation(
        Modifier.navigationBarsHeight(additional = 56.dp)
    ) {
        WanTabs.values().forEach { tab ->
            val selected = currentRoute == tab.direction.route
            BottomNavigationItem(
                icon = { Icon(painterResource(tab.icon), contentDescription = stringResource(tab.title)) },
                label = { Text(stringResource(tab.title)) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(tab.direction.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                selectedContentColor = colors.secondary,
                unselectedContentColor = LocalContentColor.current,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalComposeUiApi::class)
enum class WanTabs(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val direction: Direction
) {
    TAB_ONE(R.string.tab_one, R.drawable.ic_tab_one_black_24dp, TabOneScreenDestination),
    SQUARE(R.string.square, R.drawable.ic_square_black_24dp, SquareScreenDestination),
    WECHAT(R.string.wechat, R.drawable.ic_wechat_black_24dp, WxListsScreenDestination),
    KNOWLEDGE_SYSTEM(R.string.knowledge_system, R.drawable.ic_knowledge_system_black_24dp, SystemScreenDestination),
    PROJECT(R.string.project, R.drawable.ic_project_black_24dp, ProjectScreenDestination),
}