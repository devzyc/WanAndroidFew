package com.zyc.wan.data.network.deserializer

import com.google.gson.*
import com.zyc.wan.data.network.WebApiException
import java.lang.reflect.Type

class PojoDeserializer<T> : BaseDeserializer<T>(), JsonDeserializer<T> {

    @Throws(JsonParseException::class, WebApiException::class)
    override fun deserialize(
        element: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): T {
        return Gson().fromJson(stripSurface(element), typeOfT)
    }
}