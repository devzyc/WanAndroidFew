package com.zyc.wan.biz.extracted

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import arrow.core.Either
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.zyc.wan.R
import com.zyc.wan.biz.destinations.ArticleDetailScreenDestination
import com.zyc.wan.biz.destinations.LoginScreenDestination
import com.zyc.wan.data.network.response.Article
import com.zyc.wan.prefIsLogin
import com.zyc.wan.reusable.composable.GotoTop
import com.zyc.wan.reusable.toast
import com.zyc.wan.ui.theme.grey300
import com.zyc.wan.ui.theme.grey500
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun ArticleList(
    pagingItems: LazyPagingItems<Article>,
    onRefresh: () -> Unit,
    navigator: DestinationsNavigator,
    favoriteMap: SnapshotStateMap<Int, Boolean>,
    favoriteViewModel: FavoriteViewModel,
    listState: LazyListState = rememberLazyListState()
) {
    val topButtonScope = rememberCoroutineScope()
    val refreshState: SwipeRefreshState = rememberSwipeRefreshState(false)

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            onRefresh()
            pagingItems.refresh()
        },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(state, trigger, contentColor = MaterialTheme.colors.primary)
        },
    ) {
        refreshState.isRefreshing = pagingItems.loadState.refresh is LoadState.Loading

        LazyColumn(state = listState) {
            itemsIndexed(items = pagingItems) { index, item ->
                if (item == null) return@itemsIndexed
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                ArticleItem(
                    item,
                    favoriteMap,
                    onClick = {
                        navigator.navigate(ArticleDetailScreenDestination(url = item.link))
                    },
                    onFavoriteClick = {
                        if (!prefIsLogin) {
                            navigator.navigate(LoginScreenDestination)
                            return@ArticleItem
                        }
                        scope.launch {
                            toggleFavorite(item, favoriteViewModel, context, favoriteMap)
                        }
                    },
                )
                if (index < pagingItems.itemCount) {
                    Divider(modifier = Modifier.padding(horizontal = 6.dp), color = grey300)
                }
            }
            item {
                pagingItems.apply {
                    when (loadState.append) {
                        is LoadState.Loading -> LoadingItem()
                        is LoadState.Error -> ErrorItem { retry() }
                        is LoadState.NotLoading -> {
                            if (loadState.append.endOfPaginationReached) {
                                NoMoreItem()
                            }
                        }
                    }
                    when (loadState.refresh) {
                        is LoadState.Loading -> LoadingItem()
                        is LoadState.Error -> {
                            if (pagingItems.itemCount <= 0) { //刷新的时候，如果itemCount小于0，说明是第一次进来，出错了显示一个大的错误内容
                                ErrorContent { retry() }
                            } else {
                                ErrorItem { retry() }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
        GotoTop(
            listState = listState,
            modifier = Modifier.fillMaxSize(),
            scope = topButtonScope,
        )
    }
}

@Composable
fun ArticleItem(
    article: Article,
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

private suspend fun toggleFavorite(
    item: Article,
    viewModel: FavoriteViewModel,
    context: Context,
    favoriteMap: SnapshotStateMap<Int, Boolean>,
) {
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
    article: Article
) = favoriteMap.getOrDefault(article.id, article.collect)

@Composable
fun NoMoreItem() {
    Text(
        text = "没有更多了",
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

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