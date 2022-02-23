package com.zyc.wan.biz.login

import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.zyc.wan.data.model.LoginResult
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {

    fun login(
        userName: String,
        password: String
    ): Flow<Either<AppError, LoginResult>> {
        return userRepo.login(userName, password)
    }
}