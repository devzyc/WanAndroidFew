package com.zyc.wan.data.repo.online

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.WebApi
import com.zyc.wan.data.network.response.Article
import com.zyc.wan.data.network.response.WxChannel
import com.zyc.wan.data.repo.WxListsRepo
import com.zyc.wan.data.repo.extracted.ArticlePagingSource
import com.zyc.wan.data.repo.extracted.FavoriteRepoOnline
import com.zyc.wan.definable.Def
import com.zyc.wan.reusable.Paged
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * @author devzyc
 */
class WxListsRepoOnline(webApi: WebApi) : FavoriteRepoOnline(webApi), WxListsRepo {

    override fun getChannels(): Flow<Either<AppError, List<WxChannel>>> {
        return flow {
            emit(try {
                webApi.getWxChannels()
                    .run { Either.Right(this) }
            } catch (e: AppError) {
                Either.Left(e)
            })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getArticles(
        page: Int,
        channelId: Int
    ): Either<AppError, Paged<Article>> {
        return try {
            webApi.getArticles(page, channelId)
                .run { Either.Right(this) }
        } catch (e: AppError) {
            Either.Left(e)
        }
    }

    override fun getArticlePagingData(channelId: Int): Flow<PagingData<Article>> {
        return Pager(PagingConfig(Def.PAGE_SIZE)) {
            WxPagingSource(repo = this, channelId)
        }.flow
    }

    override fun addFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>> {
        return flow {
            emit(try {
                webApi.addFavoriteArticle(id)
                    .run { Either.Right(true) }
            } catch (e: AppError) {
                Either.Left(e)
            })
        }.flowOn(Dispatchers.IO)
    }

    override fun removeFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>> {
        return flow {
            emit(try {
                webApi.removeFavoriteArticle(id)
                    .run { Either.Right(true) }
            } catch (e: AppError) {
                Either.Left(e)
            })
        }.flowOn(Dispatchers.IO)
    }
}

class WxPagingSource(
    private val repo: WxListsRepo,
    private val channelId: Int
) : ArticlePagingSource() {

    override suspend fun getPagedArticles(nextPage: Int) = repo.getArticles(nextPage, channelId)
}