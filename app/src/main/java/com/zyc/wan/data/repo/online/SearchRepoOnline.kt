package com.zyc.wan.data.repo.online

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.WebApi
import com.zyc.wan.data.network.response.Article
import com.zyc.wan.data.repo.SearchRepo
import com.zyc.wan.data.repo.extracted.ArticlePagingSource
import com.zyc.wan.data.repo.extracted.FavoriteRepoOnline
import com.zyc.wan.definable.Def
import com.zyc.wan.reusable.Paged
import kotlinx.coroutines.flow.Flow

/**
 * @author devzyc
 */
class SearchRepoOnline(webApi: WebApi) : FavoriteRepoOnline(webApi), SearchRepo {

    override fun searchArticlePagingData(key: String): Flow<PagingData<Article>> {
        return Pager(PagingConfig(Def.PAGE_SIZE)) {
            SearchArticlesPagingSource(repo = this, key)
        }.flow
    }

    override suspend fun searchArticles(
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

class SearchArticlesPagingSource(
    private val repo: SearchRepo,
    private val key: String
) : ArticlePagingSource() {

    override suspend fun getPagedArticles(nextPage: Int) = repo.searchArticles(nextPage, key)
}