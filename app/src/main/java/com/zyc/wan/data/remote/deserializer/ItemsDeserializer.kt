package com.zyc.wan.data.remote.deserializer

import com.google.gson.*
import java.lang.reflect.Type

class ItemsDeserializer<T>(
    private val listKey: String
) : BaseDeserializer<T>(), JsonDeserializer<List<T>> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        element: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): List<T> {
        val striped = stripSurface(element)
        return Gson().fromJson(
            if (listKey.isEmpty())
                striped
            else
                striped.asJsonObject[listKey],
            typeOfT
        )
    }
}