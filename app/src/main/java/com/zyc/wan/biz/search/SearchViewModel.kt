package com.zyc.wan.biz.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zyc.wan.biz.extracted.FavoriteViewModel
import com.zyc.wan.data.network.response.Article
import com.zyc.wan.data.repo.SearchRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchViewModel(private val searchRepo: SearchRepo) : FavoriteViewModel(searchRepo) {

    val favoriteMap = mutableStateMapOf<Int, Boolean>()

    val searchContent = mutableStateOf("")

    val listState by mutableStateOf(LazyListState())

    var searchedPagingFlow by mutableStateOf<Flow<PagingData<Article>>>(
        flow { PagingData.empty<PagingData<Article>>() }
    )

    fun searchArticlePagingData(key: String) {
        searchedPagingFlow = searchRepo.searchArticlePagingData(key).cachedIn(viewModelScope)
    }
}