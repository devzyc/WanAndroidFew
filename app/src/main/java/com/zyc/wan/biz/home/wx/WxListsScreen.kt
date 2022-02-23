@file:Suppress("EXPERIMENTAL_ANNOTATION_ON_OVERRIDE_WARNING", "EXPERIMENTAL_IS_NOT_ENABLED")

package com.zyc.wan.biz.home.wx

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.*
import com.ramcosta.composedestinations.annotation.Destination
import com.zyc.wan.R
import com.zyc.wan.ui.DefaultTransitions
import com.zyc.wan.biz.extracted.ArticleList
import com.zyc.wan.data.model.WxChannel
import com.zyc.wan.definable.Def
import kotlinx.coroutines.launch

/**
 * @author devzyc
 */
@OptIn(ExperimentalPagerApi::class)
@ExperimentalComposeUiApi
@Destination(
    navGraph = Def.GRAPH_HOME,
    route= "${Def.GRAPH_HOME}/wx_lists",
    style = DefaultTransitions::class,
)
@Composable
fun WxListsScreen(
    viewModel: WxListsViewModel,
    onSearchClick: () -> Unit,
    onFavoriteButtonClick: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colors.primary),
            contentAlignment = Alignment.CenterEnd,
        ) {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "search",
                    tint = colors.onPrimary
                )
            }
        }

        val channels by remember { viewModel.channels }
        if (channels.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.loading_wx_channels))
            }
        } else {
            Column {
                val pagerState = rememberPagerState()

                Tabs(pagerState, channels)
                PagerBody(pagerState, channels, viewModel, onFavoriteButtonClick, onItemClick)
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
fun Tabs(
    pagerState: PagerState,
    channels: List<WxChannel>,
) {
    val scope = rememberCoroutineScope()

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = colors.primary,
        contentColor = colors.onPrimary,
        divider = {
            TabRowDefaults.Divider(thickness = 1.dp, color = Color.Transparent)
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color(0xffeeeeee),
            )
        }
    ) {
        channels.forEachIndexed { index, wxChannel ->
            Tab(
                text = {
                    Text(
                        text = wxChannel.name,
                        color = if (pagerState.currentPage == index) colors.onPrimary else colors.onSecondary,
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalPagerApi
@Composable
fun PagerBody(
    pagerState: PagerState,
    channels: List<WxChannel>,
    viewModel: WxListsViewModel,
    onFavoriteButtonClick: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    HorizontalPager(
        count = channels.size,
        state = pagerState,
    ) { page ->
        val channelId = channels[page].id
        val pagingItems = viewModel.getArticlePagingFlow(channelId).collectAsLazyPagingItems()
        val listState = remember { viewModel.listStateMap[channelId] }!!

        ArticleList(
            pagingItems = pagingItems,
            onRefresh = {
                viewModel.pagingFlowMap.remove(channelId)
            },
            onFavoriteButtonClick = onFavoriteButtonClick,
            onItemClick = onItemClick,
            favoriteMap = viewModel.favoriteStore[channelId]!!,
            favoriteViewModel = viewModel,
            listState = if (pagingItems.itemCount > 0) listState.value else LazyListState(),
        )
    }
}


