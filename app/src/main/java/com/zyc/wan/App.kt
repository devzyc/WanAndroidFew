package com.zyc.wan

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.zyc.wan.data.AppContainer
import com.zyc.wan.data.AppContainerImpl
import com.zyc.wan.data.network.response.UserInfo
import com.zyc.wan.definable.Constant
import com.zyc.wan.definable.Def
import com.zyc.wan.reusable.Preference

class App : Application() {

    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer

    @SuppressLint("MissingSuperCall")
    override fun onCreate() {
        instance = this
        container = AppContainerImpl(this)
    }

    companion object {
        var userInfo: UserInfo? = null
        lateinit var instance: App
    }
}

val Context.dataStore by preferencesDataStore(Def.PREFERENCES_NAME)

var prefIsLogin by Preference(Constant.LOGIN_KEY, false)

var prefUserName by Preference(Constant.USERNAME_KEY, "")

var prefPassword by Preference(Constant.PASSWORD_KEY, "")
