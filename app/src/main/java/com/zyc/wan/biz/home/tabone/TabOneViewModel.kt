package com.zyc.wan.biz.home.tabone

import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.zyc.wan.data.model.LoginResult
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.repo.TabOneRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TabOneViewModel @Inject constructor(
    private val tabOneRepo: TabOneRepo
) : ViewModel() {

    fun login(
        userName: String,
        password: String
    ): Flow<Either<AppError, LoginResult>> {
        return tabOneRepo.login(userName, password)
    }
}