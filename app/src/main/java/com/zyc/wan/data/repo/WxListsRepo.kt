package com.zyc.wan.data.repo

import androidx.paging.PagingData
import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.response.Article
import com.zyc.wan.data.network.response.WxChannel
import com.zyc.wan.data.repo.extracted.FavoriteRepo
import com.zyc.wan.reusable.Paged
import kotlinx.coroutines.flow.Flow

interface WxListsRepo : FavoriteRepo {

    fun getChannels(): Flow<Either<AppError, List<WxChannel>>>

    suspend fun getArticles(
        page: Int,
        channelId: Int
    ): Either<AppError, Paged<Article>>

    fun getArticlePagingData(channelId: Int): Flow<PagingData<Article>>
}