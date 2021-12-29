package com.zyc.wan.data.repo

import androidx.paging.PagingData
import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.response.WxArticle
import com.zyc.wan.data.network.response.WxChannel
import kotlinx.coroutines.flow.Flow

interface WxListsRepo {

    fun getChannels(): Flow<Either<AppError, List<WxChannel>>>

    fun getArticlePagingData(
        channelId: Int,
        pageSize: Int
    ): Flow<PagingData<WxArticle>>

    suspend fun getArticles(
        page: Int,
        channelId: Int
    ): Either<AppError, List<WxArticle>>

    fun addFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>>

    fun removeFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>>
}