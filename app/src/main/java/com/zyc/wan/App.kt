package com.zyc.wan

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.zyc.wan.biz.home.wx.WxListsViewModel
import com.zyc.wan.biz.search.SearchViewModel
import com.zyc.wan.data.repo.SearchRepo
import com.zyc.wan.data.repo.WxListsRepo
import com.zyc.wan.definable.Constant
import com.zyc.wan.definable.Def
import com.zyc.wan.di.NetworkModule
import com.zyc.wan.reusable.Preference
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    lateinit var wxListsViewModel: WxListsViewModel
    lateinit var searchViewModel: SearchViewModel

    @SuppressLint("MissingSuperCall")
    override fun onCreate() {
        instance = this
        wxListsViewModel = WxListsViewModel(
            WxListsRepo(
                NetworkModule.provideWebApi(
                    NetworkModule.provideRetrofit(
                        NetworkModule.provideOkHttpClient()
                    )
                )
            )
        )
        searchViewModel = SearchViewModel(
            SearchRepo(
                NetworkModule.provideWebApi(
                    NetworkModule.provideRetrofit(
                        NetworkModule.provideOkHttpClient()
                    )
                )
            )
        )
    }

    companion object {
        lateinit var instance: App
    }
}

val Context.dataStore by preferencesDataStore(Def.PREFERENCES_NAME)

var prefIsLogin by Preference(Constant.LOGIN_KEY, false)

var prefUserName by Preference(Constant.USERNAME_KEY, "")

var prefPassword by Preference(Constant.PASSWORD_KEY, "")
