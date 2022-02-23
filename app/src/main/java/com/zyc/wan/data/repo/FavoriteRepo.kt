package com.zyc.wan.data.repo

import arrow.core.Either
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.remote.WebApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * @author devzyc
 */
open class FavoriteRepo(private val webApi: WebApi) {

    fun addFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>> {
        return flow {
            emit(try {
                webApi.addFavoriteArticle(id)
                    .run { Either.Right(true) }
            } catch (e: AppError) {
                Either.Left(e)
            })
        }.flowOn(Dispatchers.IO)
    }

    fun removeFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>> {
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