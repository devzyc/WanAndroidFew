package com.zyc.wan.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import arrow.core.Either
import com.zyc.wan.data.model.Article
import com.zyc.wan.data.model.WxChannel
import com.zyc.wan.data.paging.WxPagingSource
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.remote.WebApi
import com.zyc.wan.definable.Def
import com.zyc.wan.reusable.Paged
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WxListsRepo @Inject constructor(
    private val webApi: WebApi
) : FavoriteRepo(webApi) {

    fun getChannels(): Flow<Either<AppError, List<WxChannel>>> {
        return flow {
            emit(try {
                webApi.getWxChannels()
                    .run { Either.Right(this) }
            } catch (e: AppError) {
                Either.Left(e)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getArticles(
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

    fun getArticlePagingData(channelId: Int): Flow<PagingData<Article>> {
        return Pager(PagingConfig(Def.PAGE_SIZE)) {
            WxPagingSource(repo = this, channelId)
        }.flow
    }
}