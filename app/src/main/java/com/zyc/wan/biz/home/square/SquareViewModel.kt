package com.zyc.wan.biz.home.square

import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.zyc.wan.data.model.LoginResult
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.repo.SquareRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SquareViewModel @Inject constructor(
    private val squareRepo: SquareRepo
) : ViewModel() {

    fun login(
        userName: String,
        password: String
    ): Flow<Either<AppError, LoginResult>> {
        return squareRepo.login(userName, password)
    }
}