package com.zyc.wan.data.repo.online

import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.WebApi
import com.zyc.wan.data.network.response.LoginResult
import com.zyc.wan.data.repo.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepoOnline(private val webApi: WebApi) : UserRepo {

    override fun login(
        userName: String,
        password: String
    ): Flow<Either<AppError, LoginResult>> {
        return flow {
            emit(try {
                webApi.login(userName, password)
                    .run { Either.Right(this) }
            } catch (e: AppError) {
                Either.Left(e)
            })
        }.flowOn(Dispatchers.IO)
    }
}