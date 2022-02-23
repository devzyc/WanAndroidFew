package com.zyc.wan.data.remote

import java.io.IOException

sealed class AppError(message: String) : IOException(message) {
    class BusinessError(message: String) : AppError(message)
    class ClientError(message: String) : AppError(message)
    class ServerError(message: String) : AppError(message)
    class NetworkError(message: String) : AppError(message)
    class GenericError(message: String) : AppError(message)
}
