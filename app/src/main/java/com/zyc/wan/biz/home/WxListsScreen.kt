@file:Suppress("EXPERIMENTAL_ANNOTATION_ON_OVERRIDE_WARNING", "EXPERIMENTAL_IS_NOT_ENABLED")

package com.zyc.wan.biz.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import arrow.core.Either
import com.google.accompanist.pager.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zyc.wan.R
import com.zyc.wan.data.network.response.WxArticle
import com.zyc.wan.data.network.response.WxChannel
import com.zyc.wan.definable.Route
import com.zyc.wan.prefIsLogin
import com.zyc.wan.reusable.toast
import com.zyc.wan.ui.theme.grey300
import com.zyc.wan.ui.theme.grey500
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.net.URLEncoder

/**
 * @author devzyc
 */
@OptIn(ExperimentalPagerApi::class)
@ExperimentalComposeUiApi
@Composable
fun WxListsScreen(
    viewModel: WxListsViewModel,
    navController: NavController,
) {
    val channels by remember { viewModel.channels }
    if (channels.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.loading_wx_channels))
        }
    } else {
        Column(modifier = Modifier.background(Color.White)) {
            val pagerState = rememberPagerState()

            Tabs(pagerState, channels)
            TabsContent(pagerState, channels, viewModel, navController)
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
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
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
                        color = if (pagerState.currentPage == index) Color.White else Color.LightGray,
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

@ExperimentalPagerApi
@Composable
fun TabsContent(
    pagerState: PagerState,
    channels: List<WxChannel>,
    viewModel: WxListsViewModel,
    navController: NavController,
) {
    HorizontalPager(
        count = channels.size,
        state = pagerState,
    ) { page ->
        val channelId = channels[page].id
        val pagingItems = viewModel.getArticlePagingData(channelId).collectAsLazyPagingItems()
        val refreshState: SwipeRefreshState = rememberSwipeRefreshState(false)

        SwipeRefresh(
            state = refreshState,
            onRefresh = {
                viewModel.pagingFlowMap.remove(channelId)
                pagingItems.refresh()
            },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(state, trigger, contentColor = MaterialTheme.colors.primary)
            },
        ) {
            refreshState.isRefreshing = pagingItems.loadState.refresh is LoadState.Loading

            LazyColumn {
                itemsIndexed(items = pagingItems) { index, item ->
                    if (item == null) return@itemsIndexed
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()

                    ArticleItem(
                        article = item,
                        favoriteMap = viewModel.favoriteStore[channelId]!!,
                        onClick = {
                            navController.navigate(
                                "${Route.ARTICLE_DETAIL}/${URLEncoder.encode(item.link, Charsets.UTF_8.name())}"
                            )
                        },
                        onFavoriteClick = {
                            if (!prefIsLogin) {
                                navController.navigate(Route.LOGIN)
                                return@ArticleItem
                            }
                            scope.launch {
                                toggleFavorite(item, viewModel, context)
                            }
                        },
                    )
                    if (index < pagingItems.itemCount - 1) {
                        Divider(modifier = Modifier.padding(horizontal = 6.dp), color = grey300)
                    }
                }
                pagingItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading
                                || loadState.append is LoadState.Loading -> {
                            item { LoadingItem() }
                        }
                        loadState.append is LoadState.Error -> { //加载更多的时候出错了，就在底部显示错误的item
                            item {
                                ErrorItem { pagingItems.retry() }
                            }
                        }
                        loadState.refresh is LoadState.Error -> {
                            if (pagingItems.itemCount <= 0) { //刷新的时候，如果itemCount小于0，说明是第一次进来，出错了显示一个大的错误内容
                                item {
                                    ErrorContent { pagingItems.retry() }
                                }
                            } else {
                                item {
                                    ErrorItem { pagingItems.retry() }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun toggleFavorite(
    item: WxArticle,
    viewModel: WxListsViewModel,
    context: Context,
) {
    val favoriteMap = viewModel.favoriteStore[item.chapterId]!!
    if (isFavorite(favoriteMap, item)) {
        viewModel.removeFavoriteArticle(item.id)
            .collect {
                if (it is Either.Right) {
                    favoriteMap[item.id] = false
                    context.toast(R.string.succeed_in_removing_fav)
                } else {
                    context.toast(R.string.failed_to_remove_fav)
                }
            }
    } else {
        viewModel.addFavoriteArticle(item.id)
            .collect {
                if (it is Either.Right) {
                    favoriteMap[item.id] = true
                    context.toast(R.string.succeed_in_adding_fav)
                } else {
                    context.toast(R.string.failed_to_add_fav)
                }
            }
    }
}

private fun isFavorite(
    favoriteMap: SnapshotStateMap<Int, Boolean>,
    article: WxArticle
) = favoriteMap.getOrDefault(article.id, article.collect)

@Composable
fun LoadingItem() {
    CircularProgressIndicator(
        modifier = Modifier
            .testTag("ProgressBarItem")
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
    )
}

@Composable
fun ErrorItem(retry: () -> Unit) {
    Button(
        onClick = retry,
        modifier = Modifier.padding(10.dp),
    ) {
        Text(text = stringResource(R.string.retry))
    }
}

@Composable
fun ErrorContent(retry: () -> Unit) {
    Text(text = stringResource(R.string.load_failed))
    Button(
        onClick = retry,
        modifier = Modifier.padding(10.dp),
    ) {
        Text(text = stringResource(R.string.retry))
    }
}

@Composable
fun ArticleItem(
    article: WxArticle,
    favoriteMap: SnapshotStateMap<Int, Boolean>,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = article.title,
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xff19191B),
            fontSize = 16.sp,
        )
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp),
        ) {
            Text(
                text = article.niceDate,
                color = grey500,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(
                    if (isFavorite(favoriteMap, article)) R.drawable.ic_like else R.drawable.ic_like_not
                ),
                contentDescription = "add to favorites or remove added",
                modifier = Modifier.clickable(onClick = onFavoriteClick),
            )
        }
    }
}