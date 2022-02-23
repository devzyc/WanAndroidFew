package com.zyc.wan.di

import android.util.Pair
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zyc.wan.Toggle
import com.zyc.wan.data.model.Article
import com.zyc.wan.data.model.LoginResult
import com.zyc.wan.data.model.UserInfo
import com.zyc.wan.data.model.WxChannel
import com.zyc.wan.data.remote.WebApi
import com.zyc.wan.data.remote.combinedTypeOf
import com.zyc.wan.data.remote.deserializer.ItemsDeserializer
import com.zyc.wan.data.remote.deserializer.PagedItemsDeserializer
import com.zyc.wan.data.remote.deserializer.PojoDeserializer
import com.zyc.wan.data.remote.interceptor.HeaderInterceptor
import com.zyc.wan.data.remote.interceptor.SaveCookieInterceptor
import com.zyc.wan.data.remote.interceptor.WebErrorInterceptor
import com.zyc.wan.data.remote.typeOf
import com.zyc.wan.definable.Url
import com.zyc.wan.reusable.Paged
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .run {
                connectTimeout(if (Toggle.WAIT_TIMEOUT) 2000 else 20, TimeUnit.SECONDS)
                readTimeout(if (Toggle.WAIT_TIMEOUT) 2000 else 20, TimeUnit.SECONDS)
                addInterceptor(HeaderInterceptor())
                addInterceptor(SaveCookieInterceptor())
                addInterceptor(WebErrorInterceptor())
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Url.API_HOST)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .build()
    }

    @Provides
    @Singleton
    fun provideWebApi(retrofit: Retrofit): WebApi {
        return retrofit.create(WebApi::class.java)
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
                    registerListTypeAdapter(itemClass = it.first, listKey = it.second, builder = this)
                }

                create()
            }
    }

    private fun registerListTypeAdapter(
        itemClass: Class<*>,
        listKey: String,
        builder: GsonBuilder
    ) {
        builder.registerTypeAdapter(
            combinedTypeOf(MutableList::class.java, itemClass),
            ItemsDeserializer<Any>(listKey)
        )
    }
}