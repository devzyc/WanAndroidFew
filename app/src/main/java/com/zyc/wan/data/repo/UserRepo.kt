package com.zyc.wan.data.repo

import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.response.LoginResult
import kotlinx.coroutines.flow.Flow

/**
 * @author devzyc
 */
interface UserRepo {

    fun login(
        userName: String,
        password: String
    ): Flow<Either<AppError, LoginResult>>
}