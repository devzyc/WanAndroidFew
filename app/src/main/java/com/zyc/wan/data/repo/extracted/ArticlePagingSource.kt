package com.zyc.wan.data.repo.extracted

import androidx.paging.PagingSource
import androidx.paging.PagingState
import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.response.Article
import com.zyc.wan.reusable.Paged

/**
 * @author devzyc
 */
abstract class ArticlePagingSource : PagingSource<Int, Article>() {

    abstract suspend fun getPagedArticles(nextPage: Int): Either<AppError, Paged<Article>>

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition
            ?.let {
                val closestPos = state.closestPageToPosition(it)
                closestPos?.prevKey?.plus(1)
                    ?: closestPos?.nextKey?.minus(1)
            }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val nextPage = params.key ?: 0
        return when (val result = getPagedArticles(nextPage)) {
            is Either.Right<Paged<Article>> -> {
                LoadResult.Page(
                    data = result.value.list,
                    prevKey = if (nextPage == 0) null else nextPage.minus(1),
                    nextKey = if (result.value.isLastPage) null else nextPage.plus(1)
                )
            }
            is Either.Left<AppError> -> {
                LoadResult.Error(throwable = Exception(result.value.message))
            }
        }
    }
}