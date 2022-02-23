package com.zyc.wan.biz.home.system

import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.zyc.wan.data.model.LoginResult
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.repo.SystemRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SystemViewModel @Inject constructor(
    private val systemRepo: SystemRepo
) : ViewModel() {

    fun login(
        userName: String,
        password: String
    ): Flow<Either<AppError, LoginResult>> {
        return systemRepo.login(userName, password)
    }
}