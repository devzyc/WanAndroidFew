package com.zyc.wan.data.remote

import android.util.Pair
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.`$Gson$Types`
import com.zyc.wan.Toggle
import com.zyc.wan.data.model.Article
import com.zyc.wan.data.model.LoginResult
import com.zyc.wan.data.model.UserInfo
import com.zyc.wan.data.model.WxChannel
import com.zyc.wan.data.remote.deserializer.ItemsDeserializer
import com.zyc.wan.data.remote.deserializer.PagedItemsDeserializer
import com.zyc.wan.data.remote.deserializer.PojoDeserializer
import com.zyc.wan.data.remote.interceptor.HeaderInterceptor
import com.zyc.wan.data.remote.interceptor.SaveCookieInterceptor
import com.zyc.wan.data.remote.interceptor.WebErrorInterceptor
import com.zyc.wan.definable.Url
import com.zyc.wan.reusable.Paged
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
    suspend fun getArticles(
        @Path("page") page: Int,
        @Query("cid") channelId: Int
    ): Paged<Article>

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

    @POST("article/query/{page}/json")
    @FormUrlEncoded
    suspend fun searchArticles(
        @Path("page") page: Int,
        @Field("k") key: String
    ): Paged<Article>

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
                    listOf<Class<*>>(
                        LoginResult::class.java, UserInfo::class.java,
                    ).forEach {
                        registerTypeAdapter(typeOf(it), PojoDeserializer<Any>())
                    }

                    listOf<Class<*>>(
                        Article::class.java,
                    ).map {
                        combinedTypeOf(Paged::class.java, it)
                    }.forEach {
                        registerTypeAdapter(it, PagedItemsDeserializer<Any>())
                    }

                    listOf<Pair<Class<*>, String>>(
                        Pair(WxChannel::class.java, ""),
                    ).forEach {
                        registerListTypeAdapter(itemClass = it.first, listKey = it.second, gsonBuilder = this)
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
                combinedTypeOf(MutableList::class.java, itemClass),
                ItemsDeserializer<Any>(listKey)
            )
        }
    }
}

@Suppress("UnstableApiUsage")
fun <T> typeOf(clazz: Class<T>): Type {
    return TypeToken.of(clazz).type
}

fun combinedTypeOf(
    typeOfTemplate: Type,
    typeOfTemplateArg: Type
): ParameterizedType {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, typeOfTemplate, typeOfTemplateArg)
}
