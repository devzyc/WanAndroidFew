package com.zyc.wan.biz.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.zyc.wan.biz.extracted.ArticleList
import com.zyc.wan.reusable.composable.CenterTopAppBar

/**
 * @author devzyc
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navigator: DestinationsNavigator,
) {
    val keyboardCtrl = LocalSoftwareKeyboardController.current
    val searchText by remember { viewModel.searchContent }

    Scaffold(
        topBar = {
            SearchHead(
                keyWord = searchText,
                onTextChange = {
                    viewModel.searchContent.value = it
                },
                onSearchClick = {
                    if (it.trim().isNotEmpty()) {
                        viewModel.searchArticlePagingData(it)
                    }
                    keyboardCtrl?.hide()
                },
                navigator
            )
        }
    ) {
        val pagingItems = viewModel.searchedPagingFlow.collectAsLazyPagingItems()
        val listState = remember { viewModel.listState }

        ArticleList(
            pagingItems,
            onRefresh = { viewModel.favoriteMap.clear() },
            navigator,
            favoriteMap = viewModel.favoriteMap,
            favoriteViewModel = viewModel,
            listState = if (pagingItems.itemCount > 0) listState else LazyListState(),
        )
    }
}

/**
 * 搜索框
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchHead(
    keyWord: String,
    onTextChange: (text: String) -> Unit,
    onSearchClick: (key: String) -> Unit,
    navigator: DestinationsNavigator,
) {
    CenterTopAppBar(
        backgroundColor = colors.primary,
        navigationIcon = {
            IconButton(onClick = { navigator.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "go back",
                    tint = colors.onPrimary,
                )
            }
        },
    ) {
        Row(
            Modifier.padding(10.dp)
        ) {
            BasicTextField(
                value = keyWord,
                onValueChange = { onTextChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(28.dp)
                    .background(
                        color = colors.primary,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .padding(start = 10.dp, top = 4.dp)
                    .align(Alignment.CenterVertically),
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle(
                    color = colors.onPrimary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchClick(keyWord) }),
            )
            IconButton(onClick = { onSearchClick(keyWord) }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "perform search",
                    tint = colors.onPrimary
                )
            }
        }
    }
}


