package com.zyc.wan.data.network.deserializer

import com.google.gson.JsonElement
import com.zyc.wan.data.network.AppError

open class BaseDeserializer<T> {

    fun stripSurface(element: JsonElement): JsonElement {
        val jo = element.asJsonObject
        if (jo["errorCode"].asInt != 0) {
            throw AppError.BusinessError(jo["errorMsg"].asString)
        }
        return jo["data"]
    }
}