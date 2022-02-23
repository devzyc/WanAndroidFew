package com.zyc.wan.data.remote.deserializer

import com.google.gson.*
import com.zyc.wan.data.remote.WebApiException
import com.zyc.wan.reusable.Paged
import java.lang.reflect.Type

class PagedItemsDeserializer<T> : BaseDeserializer<T>(), JsonDeserializer<Paged<T>> {

    @Throws(JsonParseException::class, WebApiException::class)
    override fun deserialize(
        element: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Paged<T> {
        return Gson().fromJson(
            stripSurface(element).asJsonObject,
            typeOfT
        )
    }
}