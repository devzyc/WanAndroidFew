package com.zyc.wan.data.repo.extracted

import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.WebApi
import com.zyc.wan.data.repo.WxListsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * @author devzyc
 */
open class FavoriteRepoOnline(val webApi: WebApi) : FavoriteRepo {

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