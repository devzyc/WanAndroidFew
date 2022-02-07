@file:Suppress("TrailingComma")

package com.zyc.wan.reusable.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.zyc.wan.ui.theme.Dimens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Composable
fun GotoTop(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    scope: CoroutineScope
) {
    val showScrollButton = derivedStateOf {
        listState.firstVisibleItemIndex > 0
    }

    AnimatedVisibility(
        visible = showScrollButton.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .padding(vertical = Dimens.MediumPadding.size)
                .statusBarsPadding(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                containerColor = colors.primary,
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowUpward,
                    contentDescription = "Scroll Up button",
                    tint = colors.background,
                    modifier = Modifier
                        .padding(horizontal = Dimens.MediumPadding.size),
                )
            }
        }
    }
}
