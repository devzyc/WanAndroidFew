package com.zyc.wan.data.repo

import arrow.core.Either
import com.zyc.wan.data.model.LoginResult
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.remote.WebApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author devzyc
 */
@Singleton
class SystemRepo @Inject constructor(
    private val webApi: WebApi
) {

    fun login(
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