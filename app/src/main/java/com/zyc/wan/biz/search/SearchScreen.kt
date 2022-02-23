package com.zyc.wan.biz.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.zyc.wan.R
import com.zyc.wan.ui.DefaultTransitions
import com.zyc.wan.biz.extracted.ArticleList
import com.zyc.wan.reusable.composable.CenterTopAppBar

/**
 * @author devzyc
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination(style = DefaultTransitions::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onFavoriteButtonClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onBack: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            val handleSearchClick: () -> Unit = {
                viewModel.searchArticlePagingData()
                keyboardController?.hide()
            }
            SearchBar(
                keyWord = viewModel.searchContent,
                onTextChange = {
                    viewModel.searchContent = it
                },
                onSearchClick = handleSearchClick,
                onClearClick = {
                    viewModel.searchContent = ""
                    handleSearchClick()
                },
                onBack = onBack
            )
        }
    ) {
        val pagingItems = viewModel.searchedPagingFlow.collectAsLazyPagingItems()
        val listState = remember { viewModel.listState }

        ArticleList(
            pagingItems,
            onRefresh = { viewModel.favoriteMap.clear() },
            onFavoriteButtonClick = onFavoriteButtonClick,
            onItemClick = onItemClick,
            favoriteMap = viewModel.favoriteMap,
            favoriteViewModel = viewModel,
            listState = if (pagingItems.itemCount > 0) listState else LazyListState(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    keyWord: String,
    onTextChange: (text: String) -> Unit,
    onSearchClick: () -> Unit,
    onClearClick: () -> Unit,
    onBack: () -> Unit,
) {
    CenterTopAppBar(
        backgroundColor = colors.primary,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "go back",
                    tint = colors.onPrimary,
                )
            }
        },
    ) {
        Row(
            modifier = Modifier
                .padding(3.dp)
                .height(53.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val focusRequester = remember { FocusRequester() }

                if (keyWord.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_tint),
                        fontSize = 16.sp,
                        color = colors.onSecondary,
                        modifier = Modifier.padding(5.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onClearClick) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "clear search input",
                                tint = colors.onPrimary,
                            )
                        }
                    }
                }

                BasicTextField(
                    value = keyWord,
                    onValueChange = {
                        if (!it.contains("\n")) { // filter enter key
                            onTextChange(it)
                        }
                    },
                    decorationBox = { innerTextField -> innerTextField() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, end = 37.dp)
                        .focusRequester(focusRequester)
                        .onKeyEvent { event ->
                            if (event.key == Key.Enter && event.type == KeyEventType.KeyUp && keyWord.isNotEmpty()) {
                                onSearchClick()
                                return@onKeyEvent true
                            }
                            false
                        },
                    maxLines = 1,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = colors.onPrimary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "perform search",
                    tint = colors.onPrimary
                )
            }
        }
    }
}


