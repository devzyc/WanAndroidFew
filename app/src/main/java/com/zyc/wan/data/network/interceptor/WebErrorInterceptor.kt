package com.zyc.wan.data.network.interceptor

import com.zyc.wan.data.network.AppError
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author devzyc
 */
class WebErrorInterceptor: Interceptor {

    @Throws(AppError::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val response = chain.proceed(chain.request())
            when (response.code) {
                in 400..499 -> {
                    throw AppError.ClientError(response.message)
                }
                in 500..599 -> {
                    throw AppError.ServerError(response.message)
                }
                else -> return response
            }
        } catch (e: Exception) {
            val message = e.message.toString()
            throw  when (e) {
                is UnknownHostException -> AppError.NetworkError(message)
                is SocketTimeoutException -> AppError.NetworkError(message)
                is AppError -> e
                else -> AppError.GenericError(message)
            }
        }
    }
}