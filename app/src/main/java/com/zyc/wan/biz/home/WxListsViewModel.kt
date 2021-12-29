package com.zyc.wan.biz.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import arrow.core.Either
import com.zyc.wan.App
import com.zyc.wan.R
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.response.WxArticle
import com.zyc.wan.data.network.response.WxChannel
import com.zyc.wan.data.repo.WxListsRepo
import com.zyc.wan.reusable.toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WxListsViewModel(private val wxListsRepo: WxListsRepo) : ViewModel() {

    val pagingFlowMap = mutableMapOf<Int, Flow<PagingData<WxArticle>>>()

    var channels = mutableStateOf<List<WxChannel>>(listOf())

    var loadingChannels by mutableStateOf(true)

    val favoriteStore = mutableMapOf<Int, SnapshotStateMap<Int, Boolean>>()

    init {
        viewModelScope.launch {
            wxListsRepo.getChannels()
                .collect {
                    when (it) {
                        is Either.Left -> {
                            when (it.value) {
                                is AppError.NetworkError ->
                                    App.instance.toast(R.string.prompt_no_network)
                                else ->
                                    App.instance.toast(R.string.failed_to_load_wx_channels)
                            }
                        }
                        is Either.Right -> {
                            channels.value = it.value
                            for (channel in it.value) {
                                favoriteStore[channel.id] = mutableStateMapOf()
                            }
                        }
                    }
                    loadingChannels = false
                }
        }
    }

    fun getArticlePagingData(channelId: Int): Flow<PagingData<WxArticle>> {
        return if (pagingFlowMap.contains(channelId)) {
            pagingFlowMap[channelId]!!
        } else {
            val flow = wxListsRepo.getArticlePagingData(
                channelId = channelId,
                pageSize = 1
            ).cachedIn(viewModelScope)
            pagingFlowMap[channelId] = flow
            flow
        }
    }

    fun addFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>> {
        return wxListsRepo.addFavoriteArticle(id)
    }

    fun removeFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>> {
        return wxListsRepo.removeFavoriteArticle(id)
    }
}