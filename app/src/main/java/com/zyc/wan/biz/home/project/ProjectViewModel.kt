package com.zyc.wan.biz.home.project

import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.zyc.wan.data.model.LoginResult
import com.zyc.wan.data.remote.AppError
import com.zyc.wan.data.repo.ProjectRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepo: ProjectRepo
) : ViewModel() {

    fun login(
        userName: String,
        password: String
    ): Flow<Either<AppError, LoginResult>> {
        return projectRepo.login(userName, password)
    }
}