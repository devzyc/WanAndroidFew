package com.zyc.wan.biz.extracted

import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.repo.extracted.FavoriteRepo
import kotlinx.coroutines.flow.Flow

open class FavoriteViewModel(private val favoriteRepo: FavoriteRepo) : ViewModel() {

    fun addFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>> {
        return favoriteRepo.addFavoriteArticle(id)
    }

    fun removeFavoriteArticle(id: Int): Flow<Either<AppError, Boolean>> {
        return favoriteRepo.removeFavoriteArticle(id)
    }
}