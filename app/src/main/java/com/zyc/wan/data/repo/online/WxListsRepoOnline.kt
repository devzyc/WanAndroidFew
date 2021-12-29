package com.zyc.wan.data.repo.online

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.WebApi
import com.zyc.wan.data.network.response.WxArticle
import com.zyc.wan.data.network.response.WxChannel
import com.zyc.wan.data.repo.WxListsRepo
import com.zyc.wan.data.repo.WxPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WxListsRepoOnline(private val webApi: WebApi) : WxListsRepo {

    override fun getArticlePagingData(
        channelId: Int,
        pageSize: Int
    ): Flow<PagingData<WxArticle>> = Pager(
        PagingConfig(pageSize)
    ) {
        WxPagingSource(wxListsRepo = this, channelId)
    }.flow

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
    ): Either<AppError, List<WxArticle>> {
        return try {
            webApi.getWxArticles(page, channelId)
                .run { Either.Right(this) }
        } catch (e: AppError) {
            Either.Left(e)
        }
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