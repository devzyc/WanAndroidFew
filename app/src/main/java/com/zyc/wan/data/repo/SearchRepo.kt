package com.zyc.wan.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import arrow.core.Either
import com.zyc.wan.data.model.Article
import com.zyc.wan.data.paging.SearchArticlesPagingSource
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.remote.WebApi
import com.zyc.wan.definable.Def
import com.zyc.wan.reusable.Paged
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author devzyc
 */
@Singleton
class SearchRepo @Inject constructor(
    private val webApi: WebApi
) : FavoriteRepo(webApi) {

    fun searchArticlePagingData(key: String): Flow<PagingData<Article>> {
        return Pager(PagingConfig(Def.PAGE_SIZE)) {
            SearchArticlesPagingSource(repo = this, key)
        }.flow
    }

    suspend fun searchArticles(
        page: Int,
        key: String
    ): Either<AppError, Paged<Article>> {
        return try {
            webApi.searchArticles(page, key)
                .run { Either.Right(this) }
        } catch (e: AppError) {
            Either.Left(e)
        }
    }
}