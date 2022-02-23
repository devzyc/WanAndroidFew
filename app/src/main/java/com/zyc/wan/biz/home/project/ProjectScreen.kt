@file:Suppress("EXPERIMENTAL_ANNOTATION_ON_OVERRIDE_WARNING", "EXPERIMENTAL_IS_NOT_ENABLED")

package com.zyc.wan.biz.home.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ramcosta.composedestinations.annotation.Destination
import com.zyc.wan.ui.DefaultTransitions
import com.zyc.wan.definable.Def

/**
 * @author devzyc
 */
@OptIn(ExperimentalPagerApi::class)
@ExperimentalComposeUiApi
@Destination(
    navGraph = Def.GRAPH_HOME,
    route= "${Def.GRAPH_HOME}/project",
    style = DefaultTransitions::class,
)
@Composable
fun ProjectScreen(
    viewModel: ProjectViewModel,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text("project")
    }
}
