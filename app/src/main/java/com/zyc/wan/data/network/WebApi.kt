package com.zyc.wan.data.network

import android.util.Pair
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.`$Gson$Types`
import com.zyc.wan.Toggle
import com.zyc.wan.data.network.deserializer.ItemsDeserializer
import com.zyc.wan.data.network.deserializer.PojoDeserializer
import com.zyc.wan.data.network.interceptor.HeaderInterceptor
import com.zyc.wan.data.network.interceptor.SaveCookieInterceptor
import com.zyc.wan.data.network.interceptor.WebErrorInterceptor
import com.zyc.wan.data.network.response.LoginResult
import com.zyc.wan.data.network.response.UserInfo
import com.zyc.wan.data.network.response.WxArticle
import com.zyc.wan.data.network.response.WxChannel
import com.zyc.wan.definable.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

interface WebApi {

    @GET("wxarticle/chapters/json")
    suspend fun getWxChannels(): List<WxChannel>

    @GET("article/list/{page}/json")
    suspend fun getWxArticles(
        @Path("page") page: Int,
        @Query("cid") channelId: Int
    ): List<WxArticle>

    @POST("lg/collect/{id}/json")
    suspend fun addFavoriteArticle(@Path("id") id: Int): Any

    @POST("lg/uncollect_originId/{id}/json")
    suspend fun removeFavoriteArticle(@Path("id") id: Int): Any

    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResult

    @GET("/lg/coin/userinfo/json")
    suspend fun getUserInfo(): UserInfo

    companion object {

        fun create(): WebApi {
            val builder = OkHttpClient().newBuilder()
                .run {
                    connectTimeout(if (Toggle.WAIT_TIMEOUT) 2000 else 20.toLong(), TimeUnit.SECONDS)
                    readTimeout(if (Toggle.WAIT_TIMEOUT) 2000 else 20.toLong(), TimeUnit.SECONDS)
                    addInterceptor(HeaderInterceptor())
                    addInterceptor(SaveCookieInterceptor())
                    addInterceptor(WebErrorInterceptor())
                    addInterceptor(HttpLoggingInterceptor().setLevel(Level.BODY))
                }
            return Retrofit.Builder()
                .baseUrl(Url.API_HOST)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .build()
                .create(WebApi::class.java)
        }

        private fun gson(): Gson {
            return GsonBuilder()
                .run {
                    listOf<Class<*>>(LoginResult::class.java)
                        .forEach {
                            registerTypeAdapter(typeOf(it), PojoDeserializer<Any>())
                        }
                    listOf<Pair<Class<*>, String>>(
                        Pair(WxChannel::class.java, ""),
                        Pair(WxArticle::class.java, "datas"),
                    ).forEach {
                        registerListTypeAdapter(it.first, it.second, this)
                    }
                    create()
                }
        }

        private fun registerListTypeAdapter(
            itemClass: Class<*>,
            listKey: String,
            gsonBuilder: GsonBuilder
        ) {
            gsonBuilder.registerTypeAdapter(
                typeFor(MutableList::class.java, itemClass),
                ItemsDeserializer<Any>(listKey)
            )
        }
    }
}

@Suppress("UnstableApiUsage")
fun <T> typeOf(clazz: Class<T>): Type {
    return TypeToken.of(clazz).type
}

fun typeFor(
    typeOfTemplate: Type,
    typeOfTemplateArg: Type
): ParameterizedType {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, typeOfTemplate, typeOfTemplateArg)
}
