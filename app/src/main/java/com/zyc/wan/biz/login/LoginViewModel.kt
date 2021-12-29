package com.zyc.wan.biz.login

import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.zyc.wan.data.network.AppError
import com.zyc.wan.data.network.response.LoginResult
import com.zyc.wan.data.repo.UserRepo
import kotlinx.coroutines.flow.Flow

class LoginViewModel(private val userRepo: UserRepo) : ViewModel() {

    fun login(
        userName: String,
        password: String
    ): Flow<Either<AppError, LoginResult>> {
        return userRepo.login(userName, password)
    }
}