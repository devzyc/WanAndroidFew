package com.zyc.wan.data.repo

import androidx.paging.PagingData
import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.response.Article
import com.zyc.wan.data.repo.extracted.FavoriteRepo
import com.zyc.wan.reusable.Paged
import kotlinx.coroutines.flow.Flow

/**
 * @author devzyc
 */
interface SearchRepo: FavoriteRepo {

    fun searchArticlePagingData(key: String): Flow<PagingData<Article>>

    suspend fun searchArticles(
        page: Int,
        key: String
    ): Either<AppError, Paged<Article>>
}