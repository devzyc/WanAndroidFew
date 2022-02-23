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
import com.zyc.wan.data.model.Article
import com.zyc.wan.data.repo.SearchRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepo: SearchRepo
) : FavoriteViewModel(searchRepo) {

    val favoriteMap = mutableStateMapOf<Int, Boolean>()

    var searchContent by mutableStateOf("")

    val listState by mutableStateOf(LazyListState())

    var searchedPagingFlow by mutableStateOf<Flow<PagingData<Article>>>(
        flow { PagingData.empty<PagingData<Article>>() }
    )

    fun searchArticlePagingData() {
        searchedPagingFlow = searchRepo.searchArticlePagingData(searchContent)
            .cachedIn(viewModelScope)
    }
}