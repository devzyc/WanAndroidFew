package com.zyc.wan.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import arrow.core.Either
import com.zyc.wan.data.network.response.WxArticle

class WxPagingSource(
    private val wxListsRepo: WxListsRepo,
    private val channelId: Int
) : PagingSource<Int, WxArticle>() {

    override fun getRefreshKey(state: PagingState<Int, WxArticle>): Int? {
        return state.anchorPosition
            ?.let {
                val closestPos = state.closestPageToPosition(it)
                closestPos?.prevKey?.plus(1)
                    ?: closestPos?.nextKey?.minus(1)
            }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WxArticle> {
        val nextPage = params.key ?: 0
        return when (val result = wxListsRepo.getArticles(nextPage, channelId)) {
            is Either.Right -> {
                LoadResult.Page(
                    data = result.value,
                    prevKey = if (nextPage == 0) null else nextPage.minus(1),
                    nextKey = nextPage.plus(1)
                )
            }
            is Either.Left -> {
                LoadResult.Error(throwable = Exception(result.value.message))
            }
        }
    }
}