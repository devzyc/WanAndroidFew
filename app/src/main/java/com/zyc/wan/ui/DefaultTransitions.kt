package com.zyc.wan.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.zyc.wan.WanTabs
import com.zyc.wan.definable.Def

@OptIn(ExperimentalAnimationApi::class)
object DefaultTransitions : DestinationStyle.Animated {

    var tabRoutes: List<String>? = null

    private val AnimatedContentScope<NavBackStackEntry>.isHomeTabSwitching: Boolean
        get() {
            if (tabRoutes == null) {
                tabRoutes = WanTabs.values().map { it.direction.route }
            }
            return initialState.destination.route in tabRoutes!! && targetState.destination.route in tabRoutes!!
        }

    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition =
        if (isHomeTabSwitching) EnterTransition.None else slideLeft()

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition =
        if (isHomeTabSwitching) EnterTransition.None else slideRight()
}

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentScope<NavBackStackEntry>.slideLeft(): EnterTransition =
    slideIntoContainer(
        AnimatedContentScope.SlideDirection.Left,
        animationSpec = tween(Def.SCREEN_TRANSITION_DURATION)
    ) + fadeIn(animationSpec = tween(Def.SCREEN_TRANSITION_DURATION))

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentScope<NavBackStackEntry>.slideRight(): EnterTransition =
    slideIntoContainer(
        AnimatedContentScope.SlideDirection.Right,
        animationSpec = tween(Def.SCREEN_TRANSITION_DURATION)
    ) + fadeIn(animationSpec = tween(Def.SCREEN_TRANSITION_DURATION))
