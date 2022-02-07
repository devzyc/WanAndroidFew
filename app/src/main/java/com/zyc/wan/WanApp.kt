package com.zyc.wan

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.zyc.wan.biz.AppNavHost
import com.zyc.wan.data.AppContainer
import com.zyc.wan.ui.theme.WanTheme

/**
 * @author devzyc
 */
@ExperimentalAnimationApi
@Composable
fun WanApp(appContainer: AppContainer) {
    WanTheme {
        val uiController = rememberSystemUiController()
        val color = colors.primary
        SideEffect {
            uiController.setSystemBarsColor(color)
        }
        AppNavHost(appContainer)
    }
}
