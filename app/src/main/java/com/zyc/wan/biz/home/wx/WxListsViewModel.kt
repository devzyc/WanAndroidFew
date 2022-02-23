package com.zyc.wan.biz.home.wx

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import arrow.core.Either
import com.zyc.wan.App
import com.zyc.wan.R
import com.zyc.wan.biz.extracted.FavoriteViewModel
import com.zyc.wan.data.model.Article
import com.zyc.wan.data.model.WxChannel
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.repo.WxListsRepo
import com.zyc.wan.reusable.extension.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WxListsViewModel @Inject constructor(
    private val wxListsRepo: WxListsRepo
) : FavoriteViewModel(wxListsRepo) {

    val pagingFlowMap = mutableMapOf<Int, Flow<PagingData<Article>>>()

    var channels = mutableStateOf<List<WxChannel>>(listOf())

    var loadingChannels by mutableStateOf(true)

    val favoriteStore = mutableMapOf<Int, SnapshotStateMap<Int, Boolean>>()

    val listStateMap = mutableMapOf<Int, MutableState<LazyListState>>()

    init {
        viewModelScope.launch {
            wxListsRepo.getChannels()
                .collect {
                    when (it) {
                        is Either.Right -> {
                            channels.value = it.value
                            for (channel in it.value) {
                                favoriteStore[channel.id] = mutableStateMapOf()
                                listStateMap[channel.id] = mutableStateOf(LazyListState())
                            }
                        }
                        is Either.Left -> {
                            when (it.value) {
                                is AppError.NetworkError ->
                                    App.instance.toast(R.string.prompt_no_network)
                                else ->
                                    App.instance.toast(R.string.failed_to_load_wx_channels)
                            }
                        }
                    }
                    loadingChannels = false
                }
        }
    }

    fun getArticlePagingFlow(channelId: Int): Flow<PagingData<Article>> {
        return if (pagingFlowMap.contains(channelId)) {
            pagingFlowMap[channelId]!!
        } else {
            val flow = wxListsRepo.getArticlePagingData(channelId).cachedIn(viewModelScope)
            pagingFlowMap[channelId] = flow
            flow
        }
    }
}