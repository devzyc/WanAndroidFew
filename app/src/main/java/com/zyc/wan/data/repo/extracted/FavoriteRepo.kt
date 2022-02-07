package com.zyc.wan.data.repo.extracted

import arrow.core.Either
import com.zyc.wan.data.network.AppError
import kotlinx.coroutines.flow.Flow

/**
 * @author devzyc
 */
interface FavoriteRepo {

    fun addFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>>

    fun removeFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>>
}